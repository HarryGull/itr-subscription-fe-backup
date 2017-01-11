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
import connectors.KeystoreConnector
import play.api.test.Helpers._
import common.Encoder._
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.Play._
import play.api.mvc.{Request, Result}
import services.{RegisteredBusinessCustomerService, SubscriptionService}
import uk.gov.hmrc.passcode.authentication.{PasscodeAuthenticationProvider, PasscodeVerificationConfig}
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class ReviewCompanyDetailsControllerSpec extends BaseTestSpec {

  lazy val mockSubscriptionService = mock[SubscriptionService]

  object TestController extends ReviewCompanyDetailsController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override val keystoreConnector = mockKeystoreConnector
    override lazy val registeredBusinessCustomerService = mockRegisteredBusinessCustomerService
    override lazy val subscriptionService = mockSubscriptionService
    override def withVerifiedPasscode(body: => Future[Result])
                                     (implicit request: Request[_], user: AuthContext): Future[Result] = body
    override def config = new PasscodeVerificationConfig(configuration(app))
    override def passcodeAuthenticationProvider = new PasscodeAuthenticationProvider(config)
  }

  "ReviewCompanyDetailsController" should {
    "use the correct keystore connector" in {
      ReviewCompanyDetailsController.keystoreConnector shouldBe KeystoreConnector
    }
  }

  "ReviewCompanyDetailsController" should {
    "use the correct auth connector" in {
      ReviewCompanyDetailsController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "ReviewCompanyDetailsController" should {
    "use the correct registered business customer service" in {
      ReviewCompanyDetailsController.registeredBusinessCustomerService shouldBe RegisteredBusinessCustomerService
    }
  }

  "ReviewCompanyDetailsController" should {
    "use the correct subscription service" in {
      ReviewCompanyDetailsController.subscriptionService shouldBe SubscriptionService
    }
  }

  "ReviewCompanyDetailsController.show" when {

    "Sending a GET request to ReviewCompanyDetailsController and all details can be retrieved from keystore" should {

      "return a 200" in {
        allDetails()
        showWithSessionAndAuth(TestController.show)(
          result => status(result) shouldBe OK
        )
      }

    }

    "Sending a GET request to ReviewCompanyDetailsController and not all details can be retrieved from keystore" should {

      "return a 303" in {
        notAllDetails()
        showWithSessionAndAuth(TestController.show)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "redirect to the confirm correspondence address page" in {
        notAllDetails()
        showWithSessionAndAuth(TestController.show)(
          result => redirectLocation(result) shouldBe Some(routes.ConfirmCorrespondAddressController.show().url)
        )
      }

    }

    "Sending a GET request to ReviewCompanyDetailsController and business customer details are not in keystore" should {
      "return a 303" in {
        noRegDetails()
        showWithSessionAndAuth(TestController.show)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "should redirect to business customer frontend" in {
        noRegDetails()
        showWithSessionAndAuth(TestController.show)(
          result => redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        )
      }
    }

    "Sending an Unauthenticated request with a session to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to GG login" in {
        showWithSessionWithoutAuth(TestController.show())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
              encode(MockConfig.introductionUrl)
            }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
          }
        )
      }
    }

    "Sending a request with no session to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to GG login" in {
        showWithoutSession(TestController.show())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
              encode(MockConfig.introductionUrl)
            }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
          }
        )
      }
    }

    "Sending a timed-out request to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to the timeout page" in {
        showWithTimeout(TestController.show())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
          }
        )
      }
    }


  }

  "ReviewCompanyDetailsController.submit" when {

    "Sending a POST request to ReviewCompanyDetailsController and SubscriptionService returns OK" should {

      "return a 303" in {
        withRegDetails()
        when(mockSubscriptionService.subscribe(Matchers.any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        submitWithSessionAndAuth(TestController.submit)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "redirect to submission frontend" in {
        withRegDetails()
        when(mockSubscriptionService.subscribe(Matchers.any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        submitWithSessionAndAuth(TestController.submit)(
          result => redirectLocation(result) shouldBe Some(FrontendAppConfig.submissionUrl)
        )
      }

    }

    "Sending a POST request to ReviewCompanyDetailsController and SubscriptionService returns a non-OK response" should {

      "return an INTERNAL_SERVER_ERROR" in {
        withRegDetails()
        when(mockSubscriptionService.subscribe(Matchers.any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))
        submitWithSessionAndAuth(TestController.submit)(
          result => status(result) shouldBe INTERNAL_SERVER_ERROR
        )
      }

    }

    "Sending a POST request to ReviewCompanyDetailsController and business customer details are not in keystore" should {
      "return a 303" in {
        noRegDetails()
        submitWithSessionAndAuth(TestController.submit)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "should redirect to business customer frontend" in {
        noRegDetails()
        submitWithSessionAndAuth(TestController.submit)(
          result => redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        )
      }
    }

    "Sending an Unauthenticated request with a session to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to GG login" in {
        submitWithSessionWithoutAuth(TestController.submit())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
              encode(MockConfig.introductionUrl)
            }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
          }
        )
      }
    }

    "Sending a request with no session to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to GG login" in {
        submitWithoutSession(TestController.submit())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
              encode(MockConfig.introductionUrl)
            }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
          }
        )
      }
    }

    "Sending a timed-out request to ReviewCompanyDetailsController" should {
      "return a 302 and redirect to the timeout page" in {
        submitWithTimeout(TestController.submit())(
          result => {
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
          }
        )
      }
    }

  }

}
