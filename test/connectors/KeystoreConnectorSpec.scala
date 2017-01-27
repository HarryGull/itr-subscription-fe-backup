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

import common.BaseTestSpec
import models.{CompanyRegistrationReviewDetailsModel, ConfirmCorrespondAddressModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import play.api.test.Helpers._

import scala.concurrent.Future

class KeystoreConnectorSpec extends BaseTestSpec {
  
  val testConnector = new KeystoreConnectorImpl(mockSessionCache, mockSessionCache)
  val confirmCorrespondAddressModel = ConfirmCorrespondAddressModel("test")

  "fetchAndGetFormData" should {

    "fetch and get from keystore" in {
      when(mockSessionCache.fetchAndGetEntry[ConfirmCorrespondAddressModel](Matchers.anyString())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Option(confirmCorrespondAddressModel)))
      val result = testConnector.fetchAndGetFormData[ConfirmCorrespondAddressModel]("test")
      await(result) shouldBe Some(confirmCorrespondAddressModel)
    }
  }

  "saveFormData" should {

    "save data to keystore" in {
      val returnedCacheMap = CacheMap("test", Map("data" -> Json.toJson(confirmCorrespondAddressModel)))
      when(mockSessionCache.cache[ConfirmCorrespondAddressModel](Matchers.anyString(), Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(returnedCacheMap))
      val result = testConnector.saveFormData("test", confirmCorrespondAddressModel)
      await(result) shouldBe returnedCacheMap
    }
  }

  "clearKeystore" should {

    "clear the data from keystore" in {
      when(mockSessionCache.remove()(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(HttpResponse(OK)))
      val result = testConnector.clearKeystore()
      await(result).status shouldBe OK
    }

  }

  "fetchAndGetReviewDetailsForSession" should {

    "convert review details json to CompanyRegistrationReviewDetailsModel" in {
      when(mockSessionCache.fetchAndGetEntry[CompanyRegistrationReviewDetailsModel]
        (Matchers.anyString())(Matchers.any[HeaderCarrier](),Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      val result = testConnector.fetchAndGetReviewDetailsForSession
      await(result) shouldBe Some(validModel)
    }

  }
}
