/*
 * Copyright 2016 HM Revenue & Customs
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

import models.CompanyRegistrationReviewDetailsModel

import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec

class DataCacheConnectorSpec extends UnitSpec with MockitoSugar {

  val validHeaderCarrier = new HeaderCarrier(sessionId = Some(SessionId("valid-session")))

  object TestConnector extends DataCacheConnector {

    override val sessionCache: SessionCache = mock[SessionCache]
  }

  "BusinessCustomerSessionCache" when {

    "DataCacheConnectorSpec.fetchAndGetReviewDetailsForSession" should {

      implicit val hc: HeaderCarrier = validHeaderCarrier
      when(TestConnector.sessionCache.fetchAndGetEntry[CompanyRegistrationReviewDetailsModel](TestConnector.bcSourceId)(Matchers.any(),Matchers.any()))
        .thenReturn(Json.fromJson[CompanyRegistrationReviewDetailsModel](validJson).asOpt)
      val result = TestConnector.fetchAndGetReviewDetailsForSession

      "convert review details json to CompanyRegistrationReviewDetailsModel" in {
        result shouldBe Some(validModel)
      }

    }
  }

}
