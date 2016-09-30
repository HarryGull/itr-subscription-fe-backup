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
import common.{Constants, KeystoreKeys}
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import forms.ConfirmCorrespondAddressForm._
import models.ConfirmCorrespondAddressModel
import play.api.mvc.Action
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ConfirmCorrespondAddress

import scala.concurrent.Future

object ConfirmCorrespondAddressController extends ConfirmCorrespondAddressController{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override val keyStoreConnector = KeystoreConnector
}

trait ConfirmCorrespondAddressController extends FrontendController with AuthorisedForTAVC {

  val keyStoreConnector: KeystoreConnector

  val show = Authorised.async { implicit user => implicit request =>
    keyStoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](KeystoreKeys.confirmContactAddress).map {
      case Some(data) => Ok(ConfirmCorrespondAddress(confirmCorrespondAddressForm.fill(data)))
      case None => Ok(ConfirmCorrespondAddress(confirmCorrespondAddressForm))
    }
  }

  val submit = Authorised.async { implicit user => implicit request =>
    confirmCorrespondAddressForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ConfirmCorrespondAddress(formWithErrors)))
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.confirmContactAddress, validFormData)

        validFormData.contactAddressUse match {
          case Constants.StandardRadioButtonYesValue => {
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
