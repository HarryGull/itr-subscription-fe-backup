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
import config.BusinessCustomerSessionCache
import models.CompanyRegistrationReviewDetailsModel
import org.mockito.Matchers
import org.mockito.Mockito._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class DataCacheConnectorSpec extends BaseTestSpec {

  object TestConnector extends DataCacheConnector {

    override val sessionCache = mockSessionCache
  }

  "BusinessCustomerDataCacheConnector" should {

    "use BusinessCustomerSessionCache as the session cache" in {
      BusinessCustomerDataCacheConnector.sessionCache shouldBe BusinessCustomerSessionCache
    }

  }

  "DataCacheConnectorSpec.fetchAndGetReviewDetailsForSession" should {

    "convert review details json to CompanyRegistrationReviewDetailsModel" in {
      when(TestConnector.sessionCache.fetchAndGetEntry[CompanyRegistrationReviewDetailsModel]
        (Matchers.anyString())(Matchers.any[HeaderCarrier](),Matchers.any()))
        .thenReturn(Future.successful(Some(validModel)))
      val result = TestConnector.fetchAndGetReviewDetailsForSession
      await(result) shouldBe Some(validModel)
    }
  }

}
