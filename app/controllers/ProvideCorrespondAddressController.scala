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
import play.api.mvc.Action
import models.ProvideCorrespondAddressModel
import forms.ProvideCorrespondAddressForm._
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ProvideCorrespondAddress

import scala.concurrent.Future

object ProvideCorrespondAddressController extends ProvideCorrespondAddressController
{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  val keyStoreConnector: KeystoreConnector = KeystoreConnector
}

trait ProvideCorrespondAddressController extends FrontendController with AuthorisedForTAVC {

  val keyStoreConnector: KeystoreConnector

  val show = Authorised.async { implicit user => implicit request =>
    keyStoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress).map {
      case Some(data) => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm.fill(data)))
      case None => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm))
    }
  }

  val submit = Authorised.async { implicit user => implicit request =>
    provideCorrespondAddressForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ProvideCorrespondAddress(formWithErrors)))
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.provideCorrespondAddress, validFormData)
        Future.successful(Redirect(routes.ConfirmCorrespondAddressController.show()))
      }
    )
  }
}
