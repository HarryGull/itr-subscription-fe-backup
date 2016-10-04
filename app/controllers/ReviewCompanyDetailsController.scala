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

package controllers

import auth.AuthorisedForTAVC
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import models.{CompanyRegistrationReviewDetailsModel, ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel, ReviewCompanyDetailsModel}
import play.api.mvc.{AnyContent, Request, Result}
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ReviewCompanyDetails

import scala.concurrent.Future

object ReviewCompanyDetailsController extends ReviewCompanyDetailsController {
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override lazy val keystoreConnector = KeystoreConnector
}

trait ReviewCompanyDetailsController extends FrontendController with AuthorisedForTAVC {

  val keystoreConnector: KeystoreConnector

  val show = Authorised.async { implicit user => implicit request =>
    for {
      registrationReviewDetails <- registeredBusinessCustomerService.getReviewBusinessCustomerDetails
      correspondenceAddress <- keystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress)
      contactDetails <- keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription)
      result <- createReviewCompanyDetailsModel(registrationReviewDetails,correspondenceAddress,contactDetails)
    } yield result
  }

  val submit = Authorised.async { implicit user => implicit request =>
    Future.successful(Redirect(routes.ReviewCompanyDetailsController.show()))
  }

  private def createReviewCompanyDetailsModel(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel],
                                              correspondenceAddress: Option[ProvideCorrespondAddressModel],
                                              contactDetails: Option[ContactDetailsSubscriptionModel])
                                             (implicit request: Request[AnyContent]): Future[Result] = {
    if (registrationReviewDetails.isDefined && correspondenceAddress.isDefined && contactDetails.isDefined) {
      Future.successful(Ok(ReviewCompanyDetails(
        ReviewCompanyDetailsModel(registrationReviewDetails.get,correspondenceAddress.get,contactDetails.get))))
    } else Future.successful(Redirect(routes.ConfirmCorrespondAddressController.show()))
  }

}
