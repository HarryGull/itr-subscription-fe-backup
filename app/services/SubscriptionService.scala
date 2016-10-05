/*
 * Copyright 2016 HM Revenue & Customs
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

import common.KeystoreKeys
import connectors.{KeystoreConnector, SubscriptionConnector}
import models.etmp._
import models.{CompanyRegistrationReviewDetailsModel, ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import play.api.libs.json.Json
import play.api.mvc.{Result, Results}
import play.api.http.Status._
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object SubscriptionService extends SubscriptionService {
  override val subscriptionConnector = SubscriptionConnector
  override val keystoreConnector = KeystoreConnector
  override val registeredBusinessCustomerService = RegisteredBusinessCustomerService
}

trait SubscriptionService {

  val subscriptionConnector: SubscriptionConnector
  val keystoreConnector: KeystoreConnector
  val registeredBusinessCustomerService: RegisteredBusinessCustomerService

  def subscribe()(implicit hc: HeaderCarrier): Future[Result] = {
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

  private def getSafeID(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel]): String = {
    if(registrationReviewDetails.isDefined) registrationReviewDetails.get.safeId
    else ""
  }

  private def getPostcode(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel]): String = {
    if(registrationReviewDetails.isDefined) {
      val postcode = registrationReviewDetails.get.businessAddress.postcode.getOrElse("").replace(" ","")
      postcode
    }
    else ""
  }

  private def sendSubscriptionRequest(safeID: String,
                                      postcode: String,
                                      subscriptionTypeModel: Option[IntermediateSubscriptionTypeModel])
                                     (implicit hc: HeaderCarrier): Future[Result] = {
    (!safeID.isEmpty,subscriptionTypeModel.isDefined) match {
      case (true,true) => {
        val json = Json.toJson(subscriptionTypeModel.get)
        val targetSubmissionModel = Json.parse(json.toString()).as[SubscriptionTypeModel]
        subscriptionConnector.subscribe(targetSubmissionModel,safeID,postcode).map {
          response => response.status match {
            case OK => Results.Ok
            case _ => Results.InternalServerError
          }
        }
      }
      case (_,_) => Future.successful(Results.InternalServerError)
    }
  }

}
