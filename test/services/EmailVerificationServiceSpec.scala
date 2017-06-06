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

package services

import auth.MockConfig
import common.BaseTestSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.http.Status.{BAD_REQUEST, CREATED, _}
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class EmailVerificationServiceSpec extends BaseTestSpec {

  val testService =  new EmailVerificationServiceImpl(mockKeystoreConnector, mockEmailVerificationConnector)
  val emailAddress = "test@test.com"
  val emailVerificationTemplate = "verifyEmailAddress"

  "Email Verification service sendVerificationLink" should {

    "return true when the email is sent successfully" in {
      when(mockEmailVerificationConnector.requestVerificationEmail(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(CREATED)))
      val result = testService.sendVerificationLink(emailAddress, MockConfig.emailVerificationReturnUrlOne, emailVerificationTemplate)
      await(result.status) shouldBe CREATED
    }

    "return false when the email is not sent" in {
      when(mockEmailVerificationConnector.requestVerificationEmail(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST)))
      val result = testService.sendVerificationLink(emailAddress, MockConfig.emailVerificationReturnUrlOne, emailVerificationTemplate)
      await(result.status) shouldBe BAD_REQUEST
    }
  }

  "Email Verification service verifyEmailAddress" should {

    "return true when the email is verified" in {
      when(mockEmailVerificationConnector.checkVerifiedEmail(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(true))
      val result = testService.verifyEmailAddress(emailAddress)
      await(result) shouldBe Some(true)
    }

    "return false when the email is not verified yet" in {
      when(mockEmailVerificationConnector.checkVerifiedEmail(Matchers.any())(Matchers.any()))
        .thenReturn(Future.successful(false))
      val result = testService.verifyEmailAddress(emailAddress)
      await(result) shouldBe Some(false)
    }
  }
}
