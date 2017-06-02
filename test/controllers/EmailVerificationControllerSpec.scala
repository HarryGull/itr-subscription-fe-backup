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
import models.{ContactDetailsSubscriptionModel, EmailVerificationModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.Future

class EmailVerificationControllerSpec extends BaseTestSpec {

  val testController = new EmailVerificationController(mockAuthorisedActions, mockKeystoreConnector, MockConfig,
    mockEmailVerificationService, messagesApi)

  val contactDetailsSubscriptionModel = ContactDetailsSubscriptionModel("First","Last",Some("86"),Some("86"),"test@test.com")
  val emailVerificationModel = EmailVerificationModel(contactDetailsSubscriptionModel)

  "Sending a GET request to EmailVerificationController" should {

    "return a 200 when EMAIL is not verified" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(contactDetailsSubscriptionModel)))
      when(mockEmailVerificationService.sendVerificationLink(Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(true)))
      when(mockEmailVerificationService.verifyEmailAddress(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(false)))
      showWithSessionAndAuth(testController.show(1))(
        result => status(result) shouldBe OK
      )
    }

    "redirect to the Review Company Details Controller page if email verified" in {
      when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(contactDetailsSubscriptionModel)))
      when(mockEmailVerificationService.sendVerificationLink(Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(true)))
      when(mockEmailVerificationService.verifyEmailAddress(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(Some(true)))
      showWithSessionAndAuth(testController.show(1))(
        result => redirectLocation(result) shouldBe Some(routes.ReviewCompanyDetailsController.show().url)
      )
    }

  }
}
