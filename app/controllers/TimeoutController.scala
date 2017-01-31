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

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import views.html.warnings._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.passcode.authentication.PasscodeAuthentication
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.passcode.authentication.{PasscodeAuthenticationProvider, PasscodeVerificationConfig}

import scala.concurrent.Future

@Singleton
class TimeoutController @Inject()(configuration: Configuration,
                                  val messagesApi: MessagesApi,
                                  implicit val applicationConfig: AppConfig) extends FrontendController with PasscodeAuthentication with I18nSupport {

  override def config: PasscodeVerificationConfig = new PasscodeVerificationConfig(configuration)
  override def passcodeAuthenticationProvider: PasscodeAuthenticationProvider = new PasscodeAuthenticationProvider(config)

  def timeout:Action[AnyContent] = PasscodeAuthenticatedActionAsync { implicit request =>
    Future.successful(Ok(sessionTimeout()))
  }

}
