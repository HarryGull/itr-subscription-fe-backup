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
import common.BaseTestSpec
import models.ProvideCorrespondAddressModel
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class ProvideCorrespondAddressControllerSpec extends BaseTestSpec {
  
  val testController = new ProvideCorrespondAddressController(mockAuthorisedActions, mockKeystoreConnector, provideCorrespondAddressForm,
    countriesHelper, messagesApi, MockConfig)

  val provideCorrespondAddressModel = ProvideCorrespondAddressModel("Line 1","Line 2",None,None,None,"JP")

  "Sending a GET request to ProvideCorrespondAddressController" should {
    "return a 200 when something is fetched from keystore" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(provideCorrespondAddressModel)))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending a valid form submit to the ProvideCorrespondAddressController" should {
    "redirect to the Contact Details Subscription Controller page" in {
      val formInput =
        Seq("addressline1" -> "Line 1",
        "addressline2" -> "Line 2",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "countryCode" -> "JP")

      submitWithSessionAndAuth(testController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ContactDetailsSubscriptionController.show().url)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect with a bad request" in {
      val formInput =
        Seq("addressline1" -> "Line 1",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "countryCode" -> "JP")

      submitWithSessionAndAuth(testController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ProvideCorrespondAddressController" should {
    "redirect to itself" in {
      val formInput = "addressline1" -> "Line 1"

      submitWithSessionAndAuth(testController.submit,formInput)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

}
