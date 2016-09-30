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

import auth._
import common.Encoder._
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import helpers.FakeRequestHelper
import helpers.AuthHelper._
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class IntroductionControllerSpec extends UnitSpec with WithFakeApplication with FakeRequestHelper with MockitoSugar {

  object TestIntroductionController extends IntroductionController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override val keystoreConnector = mock[KeystoreConnector]
    override lazy val registeredBusinessCustomerService = mockRegisteredBusinessCustomerService
  }

  "IntroductionController" should {
    "use the correct keystore connector" in {
      IntroductionController.keystoreConnector shouldBe KeystoreConnector
    }
  }

  "IntroductionController" should {
    "use the correct auth connector" in {
      IntroductionController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "show" when {

    "called with a session that's authenticated and business customer details are in keystore" should {
      lazy val result = TestIntroductionController.show(authorisedFakeRequest)

      "return a 200" in {
        withRegDetails()
        status(result) shouldBe OK
      }
    }

    "called with a session that's authenticated and business customer details are not in keystore" should {
      lazy val result = TestIntroductionController.show(authorisedFakeRequest)

      "return a 303" in {
        noRegDetails()
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to business customer frontend" in {
        noRegDetails()
        redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
      }
    }

    "called with no session" should {
      lazy val result = TestIntroductionController.show(fakeRequest)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "called with a session that's not authenticated" should {
      lazy val result = TestIntroductionController.show(fakeRequestWithSession)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "called with a session that's timed out" should {
      lazy val result = TestIntroductionController.show(timedOutFakeRequest)

      "return a 303 in" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to timeout page" in {
        redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
      }
    }
  }

  "submit" when {

    "called with a session that's authenticated and business customer details are in keystore" should {
      lazy val result = TestIntroductionController.submit(authorisedFakeRequest)

      "return a 303" in {
        withRegDetails()
        status(result) shouldBe SEE_OTHER
      }

      "redirect to confirm correspondence address page" in {
        withRegDetails()
        redirectLocation(result) shouldBe Some(routes.ConfirmCorrespondAddressController.show().url)
      }
    }

    "called with a session that's authenticated and business customer details are not in keystore" should {
      lazy val result = TestIntroductionController.submit(authorisedFakeRequest)

      "return a 303" in {
        noRegDetails()
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to business customer frontend" in {
        noRegDetails()
        redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
      }
    }

    "called with no session" should {
      lazy val result = TestIntroductionController.submit(fakeRequest)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "called with a session that's not authenticated" should {
      lazy val result = TestIntroductionController.submit(fakeRequestWithSession)

      "return a 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to GG login" in {
        redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${encode(MockConfig.introductionUrl)}&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
      }
    }

    "called with a session that's timed out" should {
      lazy val result = TestIntroductionController.submit(timedOutFakeRequest)

      "return a 303 in" in {
        status(result) shouldBe SEE_OTHER
      }

      "should redirect to timeout page" in {
        redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
      }

    }
  }

  "restart" should {

    lazy val result = TestIntroductionController.restart()(authorisedFakeRequest)

    "return a 303" in {
      withRegDetails()
      when(TestIntroductionController.keystoreConnector.clearKeystore()(Matchers.any[HeaderCarrier]()))
        .thenReturn(Future.successful(HttpResponse(OK)))
      status(result) shouldBe SEE_OTHER
    }

    "redirect to confirm correspondence address page" in {
      withRegDetails()
      when(TestIntroductionController.keystoreConnector.clearKeystore()(Matchers.any[HeaderCarrier]()))
        .thenReturn(Future.successful(HttpResponse(OK)))
      redirectLocation(result) shouldBe Some(routes.StartController.start().url)
    }
  }
}

