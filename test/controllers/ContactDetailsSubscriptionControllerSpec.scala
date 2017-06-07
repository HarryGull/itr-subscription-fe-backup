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
import common.{BaseTestSpec, Constants}
import models.ContactDetailsSubscriptionModel
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class ContactDetailsSubscriptionControllerSpec extends BaseTestSpec {
  
  val testController = new ContactDetailsSubscriptionController(mockAuthorisedActions, mockKeystoreConnector, messagesApi,
    contactDetailsSubscriptionForm, MockConfig)

  val contactDetailsSubscriptionModel = ContactDetailsSubscriptionModel("First","Last",Some("86"),Some("86"),"test@test.com")

  "Sending a GET request to ContactDetailsSubscriptionController" should {
    "return a 200 when something is fetched from keystore" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(contactDetailsSubscriptionModel)))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }

    "provide an empty model and return a 200 when nothing is fetched using keystore" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe OK
      )
    }
  }

  "Sending a valid form submit to the ContactDetailsSubscriptionController" should {

    val formInput = Seq("firstName" -> "First",
      "lastName" -> "Last",
      "telephoneNumber" -> "00000000000",
      "telephoneNumber2" -> "",
      "email" -> "test@test.com")

    "return a 303" in {
      submitWithSessionAndAuth(testController.submit,formInput:_*)(
        result => {
          status(result) shouldBe SEE_OTHER
        }
      )
    }

    "redirect to the Review Company Details Controller page" in {
      submitWithSessionAndAuth(testController.submit,formInput:_*)(
        result => {
          redirectLocation(result) shouldBe Some(routes.EmailVerificationController.show(Constants.ContactDetailsReturnUrl).url)
        }
      )
    }
  }

  "Sending an invalid form submission with validation errors to the ContactDetailsSubscriptionController" should {
    "redirect with a bad request" in {
      val formInput =
        Seq("firstName" -> "First",
        "lastName" -> "Last",
        "telephoneNumber" -> "00000000000",
        "telephoneNumber2" -> "",
        "email" -> "")

      submitWithSessionAndAuth(testController.submit,formInput:_*)(
        result => {
          status(result) shouldBe BAD_REQUEST
        }
      )
    }
  }

  "Sending an empty invalid form submission with validation errors to the ContactDetailsSubscriptionController" should {
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
