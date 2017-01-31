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
import play.api.test.Helpers._
import org.mockito.Matchers
import org.mockito.Mockito._
import services.SubscriptionService
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class ReviewCompanyDetailsControllerSpec extends BaseTestSpec {

  val mockSubscriptionService = mock[SubscriptionService]
  
  val testController = new ReviewCompanyDetailsController(mockAuthorisedActions, mockKeystoreConnector, mockRegisteredBusinessCustomerService,
    mockSubscriptionService, countriesHelper, MockConfig, messagesApi)

  "ReviewCompanyDetailsController.show" when {

    "Sending a GET request to ReviewCompanyDetailsController and all details can be retrieved from keystore" should {

      "return a 200" in {
        allDetails()
        showWithSessionAndAuth(testController.show)(
          result => status(result) shouldBe OK
        )
      }

    }

    "Sending a GET request to ReviewCompanyDetailsController and not all details can be retrieved from keystore" should {

      "return a 303" in {
        notAllDetails()
        showWithSessionAndAuth(testController.show)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "redirect to the confirm correspondence address page" in {
        notAllDetails()
        showWithSessionAndAuth(testController.show)(
          result => redirectLocation(result) shouldBe Some(routes.ConfirmCorrespondAddressController.show().url)
        )
      }

    }

  }

  "ReviewCompanyDetailsController.submit" when {

    "Sending a POST request to ReviewCompanyDetailsController and SubscriptionService returns OK" should {

      "return a 303" in {
        when(mockSubscriptionService.subscribe(Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        submitWithSessionAndAuth(testController.submit)(
          result => status(result) shouldBe SEE_OTHER
        )
      }

      "redirect to submission frontend" in {
        when(mockSubscriptionService.subscribe(Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(NO_CONTENT)))
        submitWithSessionAndAuth(testController.submit)(
          result => redirectLocation(result) shouldBe Some(MockConfig.submissionUrl)
        )
      }

    }

    "Sending a POST request to ReviewCompanyDetailsController and SubscriptionService returns a non-OK response" should {

      "return an INTERNAL_SERVER_ERROR" in {
        when(mockSubscriptionService.subscribe(Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))
        submitWithSessionAndAuth(testController.submit)(
          result => status(result) shouldBe INTERNAL_SERVER_ERROR
        )
      }

    }

  }

}
