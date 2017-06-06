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
import common.Constants
import config.{AppConfig, FrontendGlobal}
import connectors.KeystoreConnector
import models.EmailVerificationModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.EmailVerificationService
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.registrationInformation.EmailVerification

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EmailVerificationController @Inject()(authorised: AuthorisedActions,
                                            keystoreConnector: KeystoreConnector,
                                            implicit val applicationConfig: AppConfig,
                                            emailVerificationService: EmailVerificationService,
                                            val messagesApi: MessagesApi)extends FrontendController with I18nSupport {

  def show(urlPosition: Int, email: Option[String]): Action[AnyContent] = authorised.async { implicit user =>
    implicit request =>
      urlPosition match {
        case Constants.ContactDetailsReturnUrl => {
          val verifyStatus = for {
            isVerified <- emailVerificationService.verifyEmailAddress(email.getOrElse(""))

          } yield isVerified.getOrElse(false)

          val result = verifyStatus.flatMap {
            case true => Future {Constants.EmailVerified}
            case _ => {
              emailVerificationService.sendVerificationLink(email.getOrElse(""), applicationConfig.emailVerificationReturnUrlOne,
                applicationConfig.emailVerificationTemplate).map { r =>
                r.status match {
                  case CREATED => Constants.EmailNotVerified
                  case CONFLICT => Constants.EmailVerified
                  case _ => Constants.EmailVerificationError
                }
              }
            }
          }
          processSendEmailVerification(result, email)
        }
      }
  }

  private def processSendEmailVerification(result: Future[String], email: Option[String])
                                          (implicit request: Request[AnyContent]): Future[Result] ={
    result.flatMap {
      case Constants.EmailVerified => Future.successful(Redirect(controllers.routes.ReviewCompanyDetailsController.show()))
      case Constants.EmailNotVerified => Future.successful(Ok(EmailVerification(EmailVerificationModel(email.getOrElse("")))))
      case _ => Future.successful(InternalServerError(FrontendGlobal.internalServerErrorTemplate))
    }
  }

}
