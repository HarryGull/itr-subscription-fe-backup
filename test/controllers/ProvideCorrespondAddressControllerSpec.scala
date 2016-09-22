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

import java.net.URLEncoder
import java.util.UUID

import auth.{MockAuthConnector, MockConfig}
import builders.SessionBuilder
import common.Constants
import config.{FrontendAppConfig, FrontendAuthConnector}
import connectors.KeystoreConnector
import controllers.helpers.FakeRequestHelper
import models.ProvideCorrespondAddressModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class ProvideCorrespondAddressControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite with FakeRequestHelper{

  val mockKeyStoreConnector = mock[KeystoreConnector]


  object ProvideCorrespondAddressControllerTest extends ProvideCorrespondAddressController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    val keyStoreConnector: KeystoreConnector = mockKeyStoreConnector
  }

  val model = ProvideCorrespondAddressModel("Akina Speed Stars","Mt. Akina","","","","Japan")
  val cacheMap: CacheMap = CacheMap("", Map("" -> Json.toJson(model)))
  val keyStoreSavedProvideCorrespondAddress = ProvideCorrespondAddressModel("Akina Speed Stars","Mt. Akina","","","","Japan")

  implicit val hc = HeaderCarrier()

  override def beforeEach() {
    reset(mockKeyStoreConnector)
  }

  "ProvideCorrespondAddressController" should {
    "use the correct keystore connector" in {
      ProvideCorrespondAddressController.keyStoreConnector shouldBe KeystoreConnector
    }
  }

  "ProvideCorrespondAddressController" should {
    "use the correct auth connector" in {
      ProvideCorrespondAddressController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "Sending a GET request to ProvideCorrespondAddressController" should {
    "return a 200 when something is fetched from keystore" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(keyStoreSavedProvideCorrespondAddress)))
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      when(mockKeyStoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(cacheMap)
      when(mockKeyStoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(ProvideCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending an Unauthenticated request with a session to ProvideCorrespondAddressController" should {
    "return a 302 and redirect to GG login" in {
      showWithSessionWithoutAuth(ProvideCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl)
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
            URLEncoder.encode(MockConfig.introductionUrl)
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
    "redirect to the Confirm Correspondence Address Controller page" in {
      val formInput =
        Seq("addressline1" -> "Akina Speed Stars",
        "addressline2" -> "Mt. Akina",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/confirm-correspondence-address")
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect with a bad request" in {
      val formInput =
        Seq("addressline1" -> "Akina Speed Stars",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect to itself" in {

      val formInput = "addressline1" -> "Akina Speed Stars"

      submitWithSessionAndAuth(ProvideCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
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
            URLEncoder.encode(MockConfig.introductionUrl,"UTF-8")
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }

    "redirect to the GG login page with no session" in {
      submitWithoutSession(ProvideCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            URLEncoder.encode(MockConfig.introductionUrl,"UTF-8")
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
