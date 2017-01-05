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

import java.util.UUID

import models.ConfirmCorrespondAddressModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._

import scala.concurrent.Future

class KeystoreConnectorSpec extends UnitSpec with MockitoSugar {

  val mockSessionCache = mock[SessionCache]
  val sessionId = UUID.randomUUID.toString

  object TestKeyStoreConnector extends KeystoreConnector {
    override val sessionCache = mockSessionCache
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  "fetchAndGetFormData" should {

    "fetch and get from keystore" in {
      val testModel = ConfirmCorrespondAddressModel("test")
      when(mockSessionCache.fetchAndGetEntry[ConfirmCorrespondAddressModel](Matchers.anyString())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(testModel)))
      val result = TestKeyStoreConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel]("test")
      await(result) shouldBe Some(testModel)
    }
  }

  "saveFormData" should {

    "save data to keystore" in {
      val testModel = ConfirmCorrespondAddressModel("test")
      val returnedCacheMap = CacheMap("test", Map("data" -> Json.toJson(testModel)))
      when(mockSessionCache.cache[ConfirmCorrespondAddressModel](Matchers.anyString(), Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(returnedCacheMap))
      val result = TestKeyStoreConnector.saveFormData("test", testModel)
      await(result) shouldBe returnedCacheMap
    }
  }

  "clearKeystore" should {

    "clear the data from keystore" in {
      when(mockSessionCache.remove()(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(HttpResponse(OK)))
      val result = TestKeyStoreConnector.clearKeystore()
      await(result).status shouldBe OK
    }

  }
}
