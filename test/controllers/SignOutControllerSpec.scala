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

import auth.{MockAuthConnector, MockConfig}
import common.BaseTestSpec
import config.{FrontendAppConfig, FrontendAuthConnector}
import play.api.Play._
import play.api.mvc.{Action, Request, Result}
import play.api.test.Helpers._
import uk.gov.hmrc.passcode.authentication.{PasscodeAuthenticationProvider, PasscodeVerificationConfig}
import uk.gov.hmrc.passcode.authentication.PlayRequestTypes._
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future


class SignOutControllerSpec extends BaseTestSpec {

  object TestController extends SignOutController {
    override lazy val applicationConfig = MockConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val registeredBusinessCustomerService = mockRegisteredBusinessCustomerService
    override def withVerifiedPasscode(body: => Future[Result])
                                     (implicit request: Request[_], user: AuthContext): Future[Result] = body
    override def PasscodeAuthenticatedActionAsync(body: => AsyncPlayRequest) = Action.async(body)
    override def config = new PasscodeVerificationConfig(configuration(app))
    override def passcodeAuthenticationProvider = new PasscodeAuthenticationProvider(config)
  }

  "SignOutController" should {
    "Use the correct application config" in {
      SignOutController.applicationConfig shouldBe FrontendAppConfig
    }
    "Use the correct auth connector" in {
      SignOutController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "SignOutController.signout" should {

    "Redirect to sign-out" in {
      withRegDetails()
      showWithSessionAndAuth(TestController.signout())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe
            Some(s"${TestController.applicationConfig.ggSignOutUrl}?continue=${TestController.applicationConfig.signOutPageUrl}")
        }
      )
    }

  }

  "SignOutController.show" should {

    "Show the signed out page" in {
      showWithoutSession(TestController.show())(
        result => {
          status(result) shouldBe OK
        }
      )
    }

  }

}
