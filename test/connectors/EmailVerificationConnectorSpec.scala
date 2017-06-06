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

package connectors

import auth.MockConfig
import common.BaseTestSpec
import models.EmailVerificationRequest
import org.mockito.Matchers
import org.mockito.Mockito.when
import play.api.http.Status.{CONFLICT, CREATED, OK}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse, NotFoundException}
import utils.WSHTTPMock

import scala.concurrent.Future

class EmailVerificationConnectorSpec extends BaseTestSpec with WSHTTPMock{

  val http = mockWSHttp
  val testConnector = new EmailVerificationConnectorImpl(http, MockConfig)
  val verifiedEmail = "verified@email.com"

  val verificationRequest = EmailVerificationRequest(
    "testEmail",
    "register_your_company_verification_email",
    Map(),
    "linkExpiry",
    "aContinueURL"
  )

  "requestVerificationEmail" should {

    "return true with valid email request" in {

      mockHttpPOST(testConnector.sendVerificationEmailURL, HttpResponse(CREATED))

      await(testConnector.requestVerificationEmail(verificationRequest).status) shouldBe CREATED
    }

    "return false with invalid email or verified email request" in {

      mockHttpPOST(testConnector.sendVerificationEmailURL, HttpResponse(CONFLICT))

      await(testConnector.requestVerificationEmail(verificationRequest).status) shouldBe CONFLICT
    }
  }

  "checkVerifiedEmail" should {

    "return true when passed an email that has been verified" in {
      mockHttpGet(testConnector.checkVerifiedEmailURL, HttpResponse(OK))

      await(testConnector.checkVerifiedEmail(verifiedEmail)) shouldBe true
    }

    "return false when passed an email either not valid or not verified yet" in {
      when(mockWSHttp.GET[HttpResponse](Matchers.anyString())(Matchers.any(), Matchers.any[HeaderCarrier]()))
        .thenReturn(Future.failed(new NotFoundException("error")))

      await(testConnector.checkVerifiedEmail(verifiedEmail)) shouldBe false
    }
  }

}
