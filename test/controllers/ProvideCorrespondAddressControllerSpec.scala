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
import common.Encoder._
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import helpers.FakeRequestHelper
import helpers.AuthHelper._
import models.ProvideCorrespondAddressModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import play.api.libs.json.Json
import play.api.mvc.{Request, Result}
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class ProvideCorrespondAddressControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite with FakeRequestHelper{

  object ProvideCorrespondAddressControllerTest extends ProvideCorrespondAddressController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val keystoreConnector: KeystoreConnector = mockKeystoreConnector
    override lazy val registeredBusinessCustomerService = mockRegisteredBusinessCustomerService
    override def withVerifiedPasscode(body: => Future[Result])
                                     (implicit request: Request[_], user: AuthContext): Future[Result] = body
    override lazy val authService = mockAuthService
  }

  val model = ProvideCorrespondAddressModel("Line 1","Line 2",None,None,None,"JP")
  val cacheMap: CacheMap = CacheMap("", Map("" -> Json.toJson(model)))
  val keyStoreSavedProvideCorrespondAddress = ProvideCorrespondAddressModel("Line 1","Line 2",None,None,None,"JP")

  implicit val hc = HeaderCarrier()

  override def beforeEach() {
    reset(mockKeystoreConnector)
  }

  "ProvideCorrespondAddressController" should {
    "use the correct keystore connector" in {
      ProvideCorrespondAddressController.keystoreConnector shouldBe KeystoreConnector
    }
  }

  "ProvideCorrespondAddressController" should {
    "use the correct auth connector" in {
      ProvideCorrespondAddressController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "Sending a GET request to ProvideCorrespondAddressController" should {
    "return a 200 when something is fetched from keystore" in {
      withRegDetails()
      when(mockKeystoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(keyStoreSavedProvideCorrespondAddress)))
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      withRegDetails()
      when(mockKeystoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending a GET request to ProvideCorrespondAddressController and business customer details are not in keystore" should {
    "return a 303" in {
      noRegDetails()
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe SEE_OTHER
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
      )
    }
  }

  "Sending an Unauthenticated request with a session to ProvideCorrespondAddressController" should {
    "return a 302 and redirect to GG login" in {
      showWithSessionWithoutAuth(ProvideCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a request with no session to ProvideCorrespondAddressController" should {
    "return a 302 and redirect to GG login" in {
      showWithoutSession(ProvideCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a timed-out request to ProvideCorrespondAddressController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(ProvideCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }

  "Sending a valid form submit to the ProvideCorrespondAddressController" should {
    "redirect to the Contact Details Subscription Controller page" in {
      withRegDetails()
      val formInput =
        Seq("addressline1" -> "Line 1",
        "addressline2" -> "Line 2",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "countryCode" -> "JP")

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ContactDetailsSubscriptionController.show().url)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect with a bad request" in {
      withRegDetails()
      val formInput =
        Seq("addressline1" -> "Line 1",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "countryCode" -> "JP")

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect to itself" in {
      withRegDetails()
      val formInput = "addressline1" -> "Line 1"

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending a valid form submit to the ProvideCorrespondAddressController " +
    "and business customer details are not in keystore" should {

    val formInput =
      Seq("addressline1" -> "Line 1",
        "addressline2" -> "Line 2",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the ProvideCorrespondAddressController " +
    "and business customer details are not in keystore" should {

    val formInput =
      Seq("addressline1" -> "Line 1",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ProvideCorrespondAddressController " +
    "and business customer details are not in keystore" should {

    val formInput = "addressline1" -> "Line 1"

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending a submission to the ProvideCorrespondAddressController when not authenticated" should {

    "redirect to the GG login page when having a session but not authenticated" in {
      submitWithSessionWithoutAuth(ProvideCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }

    "redirect to the GG login page with no session" in {
      submitWithoutSession(ProvideCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a submission to the ProvideCorrespondAddressController when a timeout has occured" should {
    "redirect to the Timeout page when session has timed out" in {
      submitWithTimeout(ProvideCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }
}
