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

import auth.AuthorisedForTAVC
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import forms.ConfirmCorrespondAddressForm._
import models.{AddressModel, CompanyRegistrationReviewDetailsModel, ConfirmCorrespondAddressModel, ProvideCorrespondAddressModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.HeaderCarrier
import views.html.registrationInformation.ConfirmCorrespondAddress
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

import scala.concurrent.Future

object ConfirmCorrespondAddressController extends ConfirmCorrespondAddressController{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override val keyStoreConnector = KeystoreConnector
}

trait ConfirmCorrespondAddressController extends FrontendController with AuthorisedForTAVC {

  val keyStoreConnector: KeystoreConnector

  def redirect(): Action[AnyContent] = Authorised.async { implicit user => implicit request =>
    Future.successful(Redirect(routes.ConfirmCorrespondAddressController.show().url))
  }

  private def getConfirmCorrespondenceModels(implicit headerCarrier: HeaderCarrier) : Future[(Option[ConfirmCorrespondAddressModel],
    CompanyRegistrationReviewDetailsModel)] = {
    for {
      confirmCorrespondAddress <- keyStoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](KeystoreKeys.confirmContactAddress)
      companyDetails <- registeredBusinessCustomerService.getReviewBusinessCustomerDetails
    } yield (confirmCorrespondAddress, companyDetails.get)
  }


  val show = Authorised.async { implicit user => implicit request =>

    getConfirmCorrespondenceModels.map {
      case (Some(confirmCorrespondAddress),companyDetails) =>
        Ok(ConfirmCorrespondAddress(confirmCorrespondAddressForm.fill(confirmCorrespondAddress),companyDetails))
      case (None,companyDetails) => Ok(ConfirmCorrespondAddress(confirmCorrespondAddressForm,companyDetails))
    }
  }

  val submit = Authorised.async { implicit user => implicit request =>
    confirmCorrespondAddressForm.bindFromRequest().fold(
      formWithErrors => {
        getConfirmCorrespondenceModels.map {
          case (_,companyDetails) => BadRequest(ConfirmCorrespondAddress(formWithErrors,companyDetails))
        }
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.confirmContactAddress, validFormData)

        validFormData.contactAddressUse match {
          case Constants.StandardRadioButtonYesValue => {
            registeredBusinessCustomerService.getReviewBusinessCustomerDetails.map(companyDetails => {
              keyStoreConnector.saveFormData(KeystoreKeys.provideCorrespondAddress,
                Json.toJson(companyDetails.get.businessAddress).as[ProvideCorrespondAddressModel])
            })
            keyStoreConnector.saveFormData(KeystoreKeys.backLinkConfirmCorrespondAddress,
              routes.ConfirmCorrespondAddressController.show().url)
            Future.successful(Redirect(routes.ContactDetailsSubscriptionController.show()))
          }
          case Constants.StandardRadioButtonNoValue => Future.successful(Redirect(routes.ProvideCorrespondAddressController.show()))
        }
      }
    )
  }
}
