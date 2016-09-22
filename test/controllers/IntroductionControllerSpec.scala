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

import auth.MockConfig
import auth.MockAuthConnector
import common.Encoder._
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import controllers.helpers.FakeRequestHelper
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class IntroductionControllerSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper{

  object TestIntroductionController extends IntroductionController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
  }

  "IntroductionController" should {
    "use the correct keystore connector" in {
      IntroductionController.keyStoreConnector shouldBe KeystoreConnector
    }
  }

  "IntroductionController" should {
    "use the correct auth connector" in {
      IntroductionController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "IntroductionController.introduction" should {

    "when called with no session" should {

      lazy val result = TestIntroductionController.show(fakeRequest)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      s"should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "when called with a session that's not authenticated" should {

      lazy val result = TestIntroductionController.show(fakeRequestWithSession)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      s"should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "when called with a session that's authenticated" should {

      lazy val result = TestIntroductionController.show(authorisedFakeRequest)

      "return a 200 in" in {
        status(result) shouldBe OK
      }
    }

    "when called with a session that's timed out" should {

      lazy val result = TestIntroductionController.show(timedOutFakeRequest)

      "return a 303 in" in {
        status(result) shouldBe SEE_OTHER
      }

      s"should redirect to timeout page" in {
        redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
      }
    }



  }
}

