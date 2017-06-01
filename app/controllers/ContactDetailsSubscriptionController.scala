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
import forms.ContactDetailsSubscriptionForm
import models.ContactDetailsSubscriptionModel
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ContactDetailsSubscription
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

@Singleton
class ContactDetailsSubscriptionController @Inject()(authorised: AuthorisedActions,
                                                     keystoreConnector: KeystoreConnector,
                                                     val messagesApi: MessagesApi,
                                                     contactDetailsSubscriptionForm: ContactDetailsSubscriptionForm,
                                                     implicit val applicationConfig: AppConfig) extends FrontendController with I18nSupport {

  def show: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription).map {
      case Some(data) => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm.form.fill(data)))
      case None => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm.form))
    }
  }

  def submit: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    contactDetailsSubscriptionForm.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ContactDetailsSubscription(formWithErrors)))
      },
      validFormData => {
        keystoreConnector.saveFormData(KeystoreKeys.contactDetailsSubscription, validFormData)
        Future.successful(Redirect(routes.EmailVerificationController.show(1)))
      }
    )
  }
}
