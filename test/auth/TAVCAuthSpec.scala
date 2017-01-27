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

import java.net.URLEncoder

import common.BaseTestSpec
import controllers.routes
import play.api.test.FakeRequest
import play.api.http.Status
import uk.gov.hmrc.play.frontend.auth.AuthenticationProviderIds
import play.api.test.Helpers._

class TAVCAuthSpec extends BaseTestSpec {

  val authorisedForTAVC = new AuthorisedForTAVC(MockAuthConnector, configuration, MockConfig, mockRegisteredBusinessCustomerService, mockKeystoreConnector)
  val testController = new AuthTestController(authorisedForTAVC)

  "Government Gateway Provider" should {
    "have an account type additional parameter set to organisation" in {
      val ggw=new GovernmentGatewayProvider(MockConfig.introductionUrl,MockConfig.ggSignInUrl)
      ggw.additionalLoginParameters("accountType") shouldEqual List("organisation")
    }
  }

  "Government Gateway Provider" should {
    "have a login url set from its second constructor parameter" in {
      val ggw=new GovernmentGatewayProvider(MockConfig.introductionUrl,MockConfig.ggSignInUrl)
      ggw.loginURL shouldEqual MockConfig.ggSignInUrl
    }
  }

  "Government Gateway Provider" should {
    "have a continueURL constructed from its first constructor parameter" in {
      val ggw=new GovernmentGatewayProvider(MockConfig.introductionUrl,MockConfig.ggSignInUrl)
      ggw.continueURL shouldEqual MockConfig.introductionUrl
    }
  }

  "Government Gateway Provider" should {
    "handle a session timeout with a redirect" in {
      implicit val fakeRequest = FakeRequest()
      val ggw=new GovernmentGatewayProvider(MockConfig.introductionUrl,MockConfig.ggSignInUrl)
      val timeoutHandler = ggw.handleSessionTimeout(fakeRequest)
      status(timeoutHandler) shouldBe SEE_OTHER
      redirectLocation(timeoutHandler) shouldBe Some(routes.TimeoutController.timeout().url)
    }
  }

  "Extract previously logged in time of logged in user" should {
    s"return ${previouslyLoggedInAt.get}"  in {
      val user = TAVCUser(allowedAuthContext)
      user.previouslyLoggedInAt shouldBe previouslyLoggedInAt
    }
  }

  "Calling authenticated async action with no login session" should {
    "result in a redirect to login" in {

      val result = testController.authorisedAsyncAction(fakeRequest)
      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(s"/gg/sign-in?continue=${URLEncoder.encode(MockConfig.introductionUrl,"UTF-8")}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
    }
  }

  "Calling authenticated async action with a default GG login session and business customer details in keystore" should {
    "result in an OK status" in {
      withRegDetails()
      val result = testController.authorisedAsyncAction(authenticatedFakeRequest(AuthenticationProviderIds.GovernmentGatewayId))
      status(result) shouldBe Status.OK
    }
  }

  "Calling authenticated async action with a default GG login session and no business customer details in keystore" should {
    "result in a SEE_OTHER status" in {
      noRegDetails()
      val result = testController.authorisedAsyncAction(authenticatedFakeRequest(AuthenticationProviderIds.GovernmentGatewayId))
      status(result) shouldBe Status.SEE_OTHER
    }
  }
}
