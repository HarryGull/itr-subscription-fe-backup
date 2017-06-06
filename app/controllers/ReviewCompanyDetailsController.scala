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

package controllers

import auth.AuthorisedActions
import com.google.inject.{Inject, Singleton}
import common.{Constants, KeystoreKeys}
import config.{AppConfig, FrontendGlobal}
import connectors.KeystoreConnector
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{EmailVerificationService, RegisteredBusinessCustomerService, SubscriptionService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import utils.CountriesHelper
import views.html.registrationInformation.ReviewCompanyDetails

import scala.concurrent.Future

@Singleton
class ReviewCompanyDetailsController @Inject()(authorised: AuthorisedActions,
                                               keystoreConnector: KeystoreConnector,
                                               registeredBusinessCustomerService: RegisteredBusinessCustomerService,
                                               subscriptionService: SubscriptionService,
                                               emailVerificationService: EmailVerificationService,
                                               countriesHelper: CountriesHelper,
                                               implicit val applicationConfig: AppConfig,
                                               val messagesApi: MessagesApi)extends FrontendController with I18nSupport {

  def show: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    for {
      registrationReviewDetails <- registeredBusinessCustomerService.getReviewBusinessCustomerDetails
      correspondenceAddress <- keystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress)
      contactDetails <- keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription)
      result <- createReviewCompanyDetailsModel(registrationReviewDetails,correspondenceAddress,contactDetails)
      isVerified <- emailVerificationService.verifyEmailAddress(if(contactDetails.isDefined) contactDetails.get.email else "")
    } yield if(!isVerified.getOrElse(false)) Redirect(routes.EmailVerificationController.show(Constants.ContactDetailsReturnUrl, Some(contactDetails.get.email))) else result
  }

  def submit: Action[AnyContent] = authorised.async { implicit user =>
    implicit request =>
      for {
        data <- keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription)
        isVerified <- emailVerificationService.verifyEmailAddress(data.get.email)
        response <- subscriptionService.subscribe
      } yield if(!isVerified.getOrElse(false)) Redirect(routes.EmailVerificationController.show(Constants.ContactDetailsReturnUrl, Some(data.get.email))) else {
        response.status match {
          case NO_CONTENT => Redirect(applicationConfig.submissionUrl)
          case _ => InternalServerError(FrontendGlobal.internalServerErrorTemplate)
        }
      }
  }

  private def createReviewCompanyDetailsModel(registrationReviewDetails: Option[CompanyRegistrationReviewDetailsModel],
                                              correspondenceAddress: Option[ProvideCorrespondAddressModel],
                                              contactDetails: Option[ContactDetailsSubscriptionModel])
                                             (implicit request: Request[AnyContent]): Future[Result] = {
    (registrationReviewDetails, correspondenceAddress, contactDetails) match {
      case (Some(regDetails), Some(corrAddress), Some(contact)) => {
        val address = corrAddress.copy(countryCode = countriesHelper.getSelectedCountry(corrAddress.countryCode))
        Future.successful(Ok(ReviewCompanyDetails(ReviewCompanyDetailsModel(regDetails, address, contact))))
      }
      case (_, _, _) => Future.successful(Redirect(routes.ConfirmCorrespondAddressController.show()))
    }
  }

}
