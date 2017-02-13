/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.google.inject.Inject
import common.KeystoreKeys
import connectors.{KeystoreConnector, SubscriptionConnector}
import models.etmp._
import models.{CompanyRegistrationReviewDetailsModel, ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.http.Status._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionServiceImpl @Inject()(subscriptionConnector: SubscriptionConnector,
                                        keystoreConnector: KeystoreConnector,
                                        registeredBusinessCustomerService: RegisteredBusinessCustomerService) extends SubscriptionService {

  def subscribe(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    for {
      registrationReviewDetails <- registeredBusinessCustomerService.getReviewBusinessCustomerDetails
      correspondenceAddress <- keystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress)
      contactDetails <- keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription)
      result <- sendSubscriptionRequest(
        getSafeID(registrationReviewDetails),
        getPostcode(registrationReviewDetails),
        createSubscriptionRequestModel(correspondenceAddress,contactDetails)
      )
    } yield result
  }

  private def createSubscriptionRequestModel(correspondenceAddress: Option[ProvideCorrespondAddressModel],
                                             contactDetails: Option[ContactDetailsSubscriptionModel]): Option[IntermediateSubscriptionTypeModel] = {
    (correspondenceAddress, contactDetails) match {
      case (Some(address), Some(contact)) =>
        Some(IntermediateSubscriptionTypeModel(
          IntermediateCorrespondenceDetailsModel(address,contact)))
      case (_,_) => None
    }
  }

  private def getSafeID(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel]): String =
    registrationReviewDetails.fold("")(_.safeId)

  private def getPostcode(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel]): String =
    registrationReviewDetails.fold("")(_.businessAddress.postcode.getOrElse("").replace(" ",""))

  private def sendSubscriptionRequest(safeID: String,
                                      postcode: String,
                                      subscriptionTypeModel: Option[IntermediateSubscriptionTypeModel])
                                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    (safeID.nonEmpty,postcode.nonEmpty,subscriptionTypeModel.isDefined) match {
      case (true,true,true) => {
        val json = Json.toJson(subscriptionTypeModel.get)
        val targetSubmissionModel = Json.parse(json.toString()).as[SubscriptionTypeModel]
        subscriptionConnector.subscribe(targetSubmissionModel,safeID,postcode).map {
          result => result
        }.recover {
          case e: Exception => {
            Logger.warn(s"[SubscriptionService][sendSubscriptionRequest] - Safe ID: ${safeID.isEmpty} " +
              s"Postcode: ${postcode.isEmpty} Subscription model: ${subscriptionTypeModel.isDefined}")
            HttpResponse(INTERNAL_SERVER_ERROR)
          }
        }
      }
      case (_,_,_) => {
        Logger.warn(s"[SubscriptionService][sendSubscriptionRequest] - Safe ID: ${safeID.isEmpty} " +
          s"Postcode: ${postcode.isEmpty} Subscription model: ${subscriptionTypeModel.isDefined}")
        Future.successful(HttpResponse(INTERNAL_SERVER_ERROR))
      }
    }
  }

}

trait SubscriptionService {

  def subscribe(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse]

}
