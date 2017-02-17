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
import models.ContactDetailsSubscriptionModel
import forms.ContactDetailsSubscriptionForm._
import services.{AuthService, RegisteredBusinessCustomerService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ContactDetailsSubscription

import scala.concurrent.Future

object ContactDetailsSubscriptionController extends ContactDetailsSubscriptionController
{
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override lazy val keystoreConnector = KeystoreConnector
  override lazy val authService = AuthService
}

trait ContactDetailsSubscriptionController extends FrontendController with AuthorisedForTAVC {

  val show = Authorised.async { implicit user => implicit request =>
    keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription).map {
      case Some(data) => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm.fill(data)))
      case None => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm))
    }
  }

  val submit = Authorised.async { implicit user => implicit request =>
    contactDetailsSubscriptionForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ContactDetailsSubscription(formWithErrors)))
      },
      validFormData => {
        keystoreConnector.saveFormData(KeystoreKeys.contactDetailsSubscription, validFormData)
        Future.successful(Redirect(routes.ReviewCompanyDetailsController.show()))
      }
    )
  }
}
