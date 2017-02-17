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
import common.KeystoreKeys
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import models.ProvideCorrespondAddressModel
import forms.ProvideCorrespondAddressForm._
import play.api.i18n.Messages
import services.{AuthService, RegisteredBusinessCustomerService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ProvideCorrespondAddress
import utils._

import scala.concurrent.Future

object ProvideCorrespondAddressController extends ProvideCorrespondAddressController
{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override lazy val keystoreConnector = KeystoreConnector
  override lazy val authService = AuthService
}

trait ProvideCorrespondAddressController extends FrontendController with AuthorisedForTAVC {

  lazy val countriesList = CountriesHelper.getIsoCodeTupleList

  val show = Authorised.async { implicit user => implicit request =>
    keystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress).map {
      case Some(data) => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm.fill(data), countriesList))
      case None => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm.fill(ProvideCorrespondAddressModel("","")), countriesList))
    }
  }

  val submit = Authorised.async { implicit user => implicit request =>
    provideCorrespondAddressForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ProvideCorrespondAddress(if(formWithErrors.hasGlobalErrors)
          formWithErrors.discardingErrors.withError("postcode", Messages("validation.error.countrypostcode"))
        else formWithErrors, countriesList)))

      },
      validFormData => {
        keystoreConnector.saveFormData(KeystoreKeys.provideCorrespondAddress, validFormData)
        Future.successful(Redirect(routes.ContactDetailsSubscriptionController.show()))
      }
    )
  }
}
