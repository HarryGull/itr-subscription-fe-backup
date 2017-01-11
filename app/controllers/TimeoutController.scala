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

import views.html.warnings._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.passcode.authentication.PasscodeAuthentication
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.http.{HeaderCarrier, SessionKeys}
import play.api.i18n.Messages.Implicits._
import play.api.Play.current
import uk.gov.hmrc.passcode.authentication.{PasscodeAuthenticationProvider, PasscodeVerificationConfig}
import play.api.Play.configuration

import scala.concurrent.Future

object TimeoutController extends TimeoutController {
  override def config = new PasscodeVerificationConfig(configuration)
  override def passcodeAuthenticationProvider = new PasscodeAuthenticationProvider(config)
}

trait TimeoutController extends FrontendController with PasscodeAuthentication {

  def timeout:Action[AnyContent] = PasscodeAuthenticatedActionAsync { implicit request =>
    Future.successful(Ok(sessionTimeout()))
  }
}
