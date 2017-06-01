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
import config.AppConfig
import connectors.KeystoreConnector
import models.{ContactDetailsSubscriptionModel, EmailVerificationModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.EmailVerificationService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.EmailVerification

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class EmailVerificationController @Inject()(authorised: AuthorisedActions,
                                            keystoreConnector: KeystoreConnector,
                                            implicit val applicationConfig: AppConfig,
                                            emailVerificationService: EmailVerificationService,
                                            val messagesApi: MessagesApi)extends FrontendController with I18nSupport {

  def show(urlPosition: Int): Action[AnyContent] = authorised.async { implicit user =>
    implicit request =>

      urlPosition match {
        case Constants.ContactDetailsReturnUrl => {
          for {
            data <- keystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](KeystoreKeys.contactDetailsSubscription)
            if(data.isDefined)
              isVerified <- emailVerificationService.verifyEmailAddress(data.get.email)
          } yield if (isVerified.getOrElse(false)) Redirect(controllers.routes.ReviewCompanyDetailsController.show()) else {
            if(data.isDefined){
              emailVerificationService.sendVerificationLink(data.get.email, applicationConfig.emailVerificationReturnUrlOne)
              Ok(EmailVerification(EmailVerificationModel(data.get)))
            }
            else Redirect(controllers.routes.ContactDetailsSubscriptionController.show())
          }
        }
      }
  }

}
