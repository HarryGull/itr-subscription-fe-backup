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

package auth

import play.api.mvc.{Action, AnyContent, Request, Result}
import config.{AppConfig, FrontendAppConfig}
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.passcode.authentication.PasscodeAuthentication
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

trait AuthorisedForTAVC extends Actions with PasscodeAuthentication {

  val applicationConfig: AppConfig
  val registeredBusinessCustomerService: RegisteredBusinessCustomerService
  val postSignInRedirectUrl: String = applicationConfig.introductionUrl

  // $COVERAGE-OFF$
  implicit private def hc(implicit request: Request[_]): HeaderCarrier = HeaderCarrier.fromHeadersAndSession(request.headers, Some(request.session))
  // $COVERAGE-ON$
  lazy val visibilityPredicate = new TAVCCompositePageVisibilityPredicate(
    applicationConfig.businessCustomerUrl,
    registeredBusinessCustomerService
  )

  private type PlayRequest = Request[AnyContent] => Result
  private type UserRequest = TAVCUser => PlayRequest
  private type AsyncPlayRequest = Request[AnyContent] => Future[Result]
  private type AsyncUserRequest = TAVCUser => AsyncPlayRequest

  class AuthorisedBy(regime: TaxRegime) {
    def async(action: AsyncUserRequest): Action[AnyContent] = {
      AuthorisedFor(regime, visibilityPredicate).async {
        implicit user => implicit request =>
          withVerifiedPasscode(action(TAVCUser(user))(request))
      }
    }
    // $COVERAGE-OFF$
    def apply(action: UserRequest): Action[AnyContent] = async(user => request => Future.successful(action(user)(request)))
    // $COVERAGE-ON$
  }

  object Authorised extends AuthorisedBy(TAVCRegime)

  val tavcAuthProvider: GovernmentGatewayProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.ggSignInUrl)

  trait TAVCRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true
    override def authenticationType: AuthenticationProvider = tavcAuthProvider
  }

  object TAVCRegime extends TAVCRegime
}
