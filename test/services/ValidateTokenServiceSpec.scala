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

import common.{KeystoreKeys, BaseTestSpec}
import connectors.AuthConnector
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import uk.gov.hmrc.play.http.{HttpResponse, Upstream5xxResponse}
import play.api.test.Helpers._

import scala.concurrent.Future

class ValidateTokenServiceSpec extends BaseTestSpec {

  val testService =  new ValidateTokenServiceImpl(mockValidateTokenConnector)

  "validateTemporaryToken" should {

    "return true if the token is validated successfully" in {
      when(mockValidateTokenConnector.validateToken(Matchers.any())(Matchers.any())).
        thenReturn(Future.successful(Some(true)))
      val result = testService.validateTemporaryToken(Some(tokenId))
      await(result) shouldBe true
    }

    "return false if the token is not validated successfully" in {
      when(mockValidateTokenConnector.validateToken(Matchers.any())(Matchers.any())).
        thenReturn(Future.successful(Some(false)))
      val result = testService.validateTemporaryToken(Some(tokenId))
      await(result) shouldBe false
    }

    "return false if no token is found" in {
      when(mockValidateTokenConnector.validateToken(Matchers.any())(Matchers.any())).
        thenReturn(Future.successful(None))
      val result = testService.validateTemporaryToken(Some(tokenId))
      await(result) shouldBe false
    }

    "return false if an error occurs" in {
      when(mockValidateTokenConnector.validateToken(Matchers.any())(Matchers.any())).
        thenReturn(Future.failed(Upstream5xxResponse("", INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR)))
      val result = testService.validateTemporaryToken(Some(tokenId))
      await(result) shouldBe false
    }

  }

}
