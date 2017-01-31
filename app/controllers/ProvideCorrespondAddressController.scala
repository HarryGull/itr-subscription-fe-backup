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
import common.KeystoreKeys
import config.AppConfig
import connectors.KeystoreConnector
import models.ProvideCorrespondAddressModel
import forms.ProvideCorrespondAddressForm
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ProvideCorrespondAddress
import utils.CountriesHelper

import scala.concurrent.Future

@Singleton
class ProvideCorrespondAddressController @Inject()(authorised: AuthorisedActions,
                                                   keystoreConnector: KeystoreConnector,
                                                   provideCorrespondAddressForm: ProvideCorrespondAddressForm,
                                                   countriesHelper: CountriesHelper,
                                                   val messagesApi: MessagesApi,
                                                   implicit val applicationConfig: AppConfig) extends FrontendController with I18nSupport {

  lazy val countriesList = countriesHelper.getIsoCodeTupleList

  def show: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    keystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](KeystoreKeys.provideCorrespondAddress).map {
      case Some(data) => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm.form.fill(data), countriesList))
      case None => Ok(ProvideCorrespondAddress(provideCorrespondAddressForm.form.fill(ProvideCorrespondAddressModel("","")), countriesList))
    }
  }

  val submit = authorised.async { implicit user => implicit request =>
    provideCorrespondAddressForm.form.bindFromRequest().fold(
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
