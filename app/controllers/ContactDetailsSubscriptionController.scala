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

import common.KeystoreKeys
import connectors.KeystoreConnector
import controllers.predicates.ValidActiveSession
import play.api.mvc.Action
import models.ContactDetailsSubscriptionModel
import forms.ContactDetailsSubscriptionForm._
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.ContactDetailsSubscription

import scala.concurrent.Future

object ContactDetailsSubscriptionController extends ContactDetailsSubscriptionController
{
  val keyStoreConnector: KeystoreConnector = KeystoreConnector
}

trait ContactDetailsSubscriptionController extends FrontendController with ValidActiveSession {

  val keyStoreConnector: KeystoreConnector

  val show = ValidateSession.async { implicit request =>
    keyStoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription).map {
      case Some(data) => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm.fill(data)))
      case None => Ok(ContactDetailsSubscription(contactDetailsSubscriptionForm))
    }
  }

  val submit = Action.async { implicit request =>
    contactDetailsSubscriptionForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(ContactDetailsSubscription(formWithErrors)))
      },
      validFormData => {
        keyStoreConnector.saveFormData(KeystoreKeys.contactDetailsSubscription, validFormData)
        Future.successful(Redirect(routes.ConfirmCorrespondAddressController.show()))
      }
    )
  }
}
