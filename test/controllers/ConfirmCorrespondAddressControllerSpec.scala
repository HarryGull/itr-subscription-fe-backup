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
import helpers.AuthHelper._
import common.{Constants, KeystoreKeys}
import common.Encoder._
import config.{FrontendAppConfig, FrontendAuthConnector}
import helpers.FakeRequestHelper
import connectors.{DataCacheConnector, KeystoreConnector}
import models.{CompanyRegistrationReviewDetailsModel, ConfirmCorrespondAddressModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import play.api.libs.json.Json
import play.api.mvc.{Request, Result}
import play.api.test.Helpers._
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.passcode.authentication.AuthorisationConnector
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class ConfirmCorrespondAddressControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite with FakeRequestHelper{

  object ConfirmCorrespondAddressControllerTest extends ConfirmCorrespondAddressController {
    override lazy val applicationConfig = FrontendAppConfig
    override lazy val authConnector = MockAuthConnector
    override lazy val registeredBusinessCustomerService: RegisteredBusinessCustomerService = mockRegisteredBusinessCustomerService
    override lazy val keystoreConnector: KeystoreConnector = mockKeystoreConnector
    override def withVerifiedPasscode(body: => Future[Result])
                            (implicit request: Request[_], user: AuthContext): Future[Result] = body
  }

  val confirmCorrespondAddressModel = ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue)
  val confirmCorrespondAddressCacheMap: CacheMap = CacheMap("", Map("" -> Json.toJson(confirmCorrespondAddressModel)))
  val keyStoreSavedConfirmCorrespondAddress = ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue)


  implicit val hc = HeaderCarrier()

  override def beforeEach() {
    reset(mockKeystoreConnector)
  }

  "ConfirmCorrespondAddressController" should {
    "use the correct keystore connector" in {
      ConfirmCorrespondAddressController.keystoreConnector shouldBe KeystoreConnector
    }
  }

  "ConfirmCorrespondAddressController" should {
    "use the correct auth connector" in {
      ConfirmCorrespondAddressController.authConnector shouldBe FrontendAuthConnector
    }
  }

  "Sending a GET request to ConfirmCorrespondAddressController" should {
    "return a 200 when something is fetched from keystore" in {
      withRegDetails()
      when(mockKeystoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(confirmCorrespondAddressCacheMap)
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.eq(KeystoreKeys.confirmContactAddress))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Option(keyStoreSavedConfirmCorrespondAddress)))
      showWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty confirmCorrespondAddressModel and return a 200 when nothing is fetched using keystore" in {
      withRegDetails()
      when(mockKeystoreConnector.saveFormData(Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(confirmCorrespondAddressCacheMap)
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending a GET request to ConfirmCorrespondAddressController and business customer details are not in keystore" should {
    "return a 303" in {
      noRegDetails()
      showWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.show)(
        result => status(result) shouldBe SEE_OTHER
      )
    }

    "should redirect to business customer frontend" in {
      noRegDetails()
      showWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.show)(
        result => redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
      )
    }
  }

  "Sending an Unauthenticated request with a session to ConfirmCorrespondAddressController" should {
    "return a 302 and redirect to GG login" in {
      showWithSessionWithoutAuth(ConfirmCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a request with no session to ConfirmCorrespondAddressController" should {
    "return a 302 and redirect to GG login" in {
      showWithoutSession(ConfirmCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a timed-out request to ConfirmCorrespondAddressController" should {
    "return a 302 and redirect to the timeout page" in {
      showWithTimeout(ConfirmCorrespondAddressControllerTest.show())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }


  "Sending a valid form submission with Yes option to the ConfirmCorrespondAddressController" should {
    "redirect Contact Details Subscription page" in {
      withRegDetails()
      val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue

      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/contact-details-subscription")
        }
      )
    }
  }

  "Sending a valid form submission with No option to the ConfirmCorrespondAddressController when authenticated" should {
    "redirect to provide Correspondence Address page" in {
      withRegDetails()
      val formInput = "contactAddressUse" -> Constants.StandardRadioButtonNoValue

      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/provide-correspondence-address")
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ConfirmCorrespondAddressController" should {
    "redirect to itself" in {
      withRegDetails()
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.eq(KeystoreKeys.confirmContactAddress))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Option(keyStoreSavedConfirmCorrespondAddress)))
      val formInput = "contactAddressUse" -> ""

      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending a valid form submission with Yes option to the ConfirmCorrespondAddressController " +
    "and business customer details are not in keystore" should {

    val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending a valid form submission with No option to the ConfirmCorrespondAddressController when authenticated " +
    "and business customer details are not in keystore" should {

    val formInput = "contactAddressUse" -> Constants.StandardRadioButtonNoValue

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ConfirmCorrespondAddressController " +
    "and business customer details are not in keystore" should {

    val formInput = "contactAddressUse" -> ""

    "return a 303" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to business customer frontend" in {
      noRegDetails()
      submitWithSessionAndAuth(ConfirmCorrespondAddressControllerTest.submit,formInput)(
        result => {
          redirectLocation(result) shouldBe Some(FrontendAppConfig.businessCustomerUrl)
        }
      )
    }
  }

  "Sending a submission to the ConfirmCorrespondAddressController when not authenticated" should {

    "redirect to the GG login page when having a session but not authenticated" in {
      submitWithSessionWithoutAuth(ConfirmCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }

    "redirect to the GG login page with no session" in {
      submitWithoutSession(ConfirmCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(s"${FrontendAppConfig.ggSignInUrl}?continue=${
            encode(MockConfig.introductionUrl)
          }&origin=investment-tax-relief-subscription-frontend&accountType=organisation")
        }
      )
    }
  }

  "Sending a submission to the ConfirmCorrespondAddressController when a timeout has occured" should {
    "redirect to the Timeout page when session has timed out" in {
      submitWithTimeout(ConfirmCorrespondAddressControllerTest.submit)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.TimeoutController.timeout().url)
        }
      )
    }
  }

}
