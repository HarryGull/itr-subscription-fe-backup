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

import com.google.inject.{Inject, Singleton}
import play.api.mvc._
import config.AppConfig
import services.{AuthService, RegisteredBusinessCustomerService, ValidateTokenService}
import connectors.KeystoreConnector
import play.api.Configuration
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.frontend.auth.{Actions, AuthenticationProvider, TaxRegime}
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.HeaderCarrierConverter

@Singleton
class AuthorisedForTAVC @Inject()(val authConnector: AuthConnector,
                                  configuration: Configuration,
                                  applicationConfig: AppConfig,
                                  validateTokenService: ValidateTokenService,
                                  registeredBusinessCustomerService: RegisteredBusinessCustomerService,
                                  keystoreConnector: KeystoreConnector,
                                  authService: AuthService)(implicit ec: ExecutionContext)
  extends AuthorisedActions with Actions {

  lazy val postSignInRedirectUrl: String = applicationConfig.introductionUrl

  // $COVERAGE-OFF$
  implicit private def hc(implicit request: Request[_]): HeaderCarrier = HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))
  // $COVERAGE-ON$

  private lazy val visibilityPredicate = new TAVCCompositePageVisibilityPredicate(
    validateTokenService,
    applicationConfig.submissionUrl,
    applicationConfig.businessCustomerUrl,
    registeredBusinessCustomerService,
    keystoreConnector,
    authService
  )

  def async(action: TAVCUser => Request[AnyContent] => Future[Result]): Action[AnyContent] = {
    AuthorisedFor(TAVCRegime, visibilityPredicate).async {
      implicit user => implicit request =>
        action(TAVCUser(user))(request)
    }
  }

  trait TAVCRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true
    override def authenticationType: AuthenticationProvider = new GovernmentGatewayProvider(postSignInRedirectUrl, applicationConfig.ggSignInUrl)
  }

  object TAVCRegime extends TAVCRegime
}

trait AuthorisedActions {

  def async(action: TAVCUser => Request[AnyContent] => Future[Result]): Action[AnyContent]

}