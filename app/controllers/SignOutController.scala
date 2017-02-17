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
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import play.api.mvc.{Action, AnyContent}
import services.{AuthService, RegisteredBusinessCustomerService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.signout.SignedOut

import scala.concurrent.Future

object SignOutController extends SignOutController {
  override lazy val applicationConfig = FrontendAppConfig
  override lazy val authConnector = FrontendAuthConnector
  override lazy val registeredBusinessCustomerService = RegisteredBusinessCustomerService
  override lazy val keystoreConnector = KeystoreConnector
  override lazy val authService = AuthService
}

trait SignOutController extends FrontendController with AuthorisedForTAVC {

  def signout(): Action[AnyContent] = Authorised.async { implicit user => implicit request =>
    Future.successful(Redirect(s"${applicationConfig.ggSignOutUrl}?continue=${applicationConfig.signOutPageUrl}"))
  }

  def show(): Action[AnyContent] = PasscodeAuthenticatedActionAsync { implicit request =>
    Future.successful(Ok(SignedOut()))
  }

}
