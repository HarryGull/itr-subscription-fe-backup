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

import auth.MockConfig
import common.{BaseTestSpec, Constants, KeystoreKeys}
import models.ConfirmCorrespondAddressModel
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.Future

class ConfirmCorrespondAddressControllerSpec extends BaseTestSpec {

  val testController = new ConfirmCorrespondAddressController(mockAuthorisedActions, mockKeystoreConnector, mockRegisteredBusinessCustomerService,
    confirmCorrespondAddressForm, countriesHelper, messagesApi, MockConfig)

  val confirmCorrespondAddressModel = ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue)

  val cacheMapTokenId: CacheMap = CacheMap("", Map("" -> Json.toJson(tokenId)))


  "Sending a GET request to ConfirmCorrespondAddressController redirect method" should {
    "save the token from the url into keystore and redirect to the show method of this page when a token is passed in the url" in {
      when(mockKeystoreConnector.saveFormData[String](Matchers.eq(KeystoreKeys.tokenId),Matchers.any())(Matchers.any(),Matchers.any()))
        .thenReturn(Future.successful(cacheMapTokenId))
      val result = testController.redirect(Some(tokenId)).apply(fakeRequest)
      await(result).map{
        res => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/confirm-correspondence-address")
        }
      }
    }

    "redirect to the show method of this page when no token is passed in the url" in {
      val result = testController.redirect(Some(tokenId)).apply(fakeRequest)
      await(result).map{
        res => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/confirm-correspondence-address")
        }
      }
    }
  }

  "Sending a GET request to ConfirmCorrespondAddressController" should {
    "return a 200 when something is fetched from keystore" in {
      when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.eq(KeystoreKeys.confirmContactAddress))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Option(confirmCorrespondAddressModel)))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty confirmCorrespondAddressModel and return a 200 when nothing is fetched using keystore" in {
      when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }
  }


  "Sending a valid form submission with Yes option to the ConfirmCorrespondAddressController" should {
    "redirect Contact Details Subscription page" in {
      val formInput = "contactAddressUse" -> Constants.StandardRadioButtonYesValue
      when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      submitWithSessionAndAuth(testController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/contact-details-subscription")
        }
      )
    }
  }

  "Sending a valid form submission with No option to the ConfirmCorrespondAddressController when authenticated" should {
    "redirect to provide Correspondence Address page" in {
      val formInput = "contactAddressUse" -> Constants.StandardRadioButtonNoValue
      submitWithSessionAndAuth(testController.submit,formInput)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("/investment-tax-relief-subscription/provide-correspondence-address")
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ConfirmCorrespondAddressController" should {
    "redirect to itself" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel](Matchers.eq(KeystoreKeys.confirmContactAddress))
        (Matchers.any(), Matchers.any())).thenReturn(Future.successful(Option(confirmCorrespondAddressModel)))
      when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      val formInput = "contactAddressUse" -> ""

      submitWithSessionAndAuth(testController.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

}
