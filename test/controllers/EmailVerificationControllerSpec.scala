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
import models.{ContactDetailsSubscriptionModel, EmailVerificationModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.http.Status.CREATED
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class EmailVerificationControllerSpec extends BaseTestSpec {

  val testController = new EmailVerificationController(mockAuthorisedActions, mockKeystoreConnector, MockConfig,
    mockEmailVerificationService, messagesApi)
  val email = Some("test@test.com")
  val contactDetailsSubscriptionModel = ContactDetailsSubscriptionModel("First","Last",Some("86"),Some("86"),"test@test.com")
  val emailVerificationModel = EmailVerificationModel("test@test.com")

  "Sending a GET request to EmailVerificationController" should {

    "return a 200 when EMAIL is not verified" in {
      when(mockEmailVerificationService.sendVerificationLink(Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(CREATED)))

      when(mockEmailVerificationService.verifyEmailAddress(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(false)))
      showWithSessionAndAuth(testController.show(Constants.ContactDetailsReturnUrl, email))(
        result => status(result) shouldBe OK
      )
    }

    "redirect to the Review Company Details Controller page if email verified" in {
      when(mockEmailVerificationService.verifyEmailAddress(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(true)))
      showWithSessionAndAuth(testController.show(Constants.ContactDetailsReturnUrl, email))(
        result => redirectLocation(result) shouldBe Some(routes.ReviewCompanyDetailsController.show().url)
      )
    }

  }
}
