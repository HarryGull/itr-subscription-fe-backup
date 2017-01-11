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

import common.BaseTestSpec
import connectors.{BusinessCustomerDataCacheConnector, DataCacheConnector}
import org.mockito.Matchers
import org.mockito.Mockito._
import uk.gov.hmrc.play.http.HeaderCarrier


class RegisteredBusinessCustomerServiceSpec extends BaseTestSpec {

  object TestService extends RegisteredBusinessCustomerService {
    override val dataCacheConnector =  mock[DataCacheConnector]
  }

  "RegisteredBusinessCustomerService" should {
    "use the correct keystore connector" in {
      RegisteredBusinessCustomerService.dataCacheConnector shouldBe BusinessCustomerDataCacheConnector
    }
  }

  "getReviewBusinessCustomerDetails" when {

    "dataCacheConnector returns a CompanyRegistrationReviewDetailsModel" should {

      "return the data" in {
        when(TestService.dataCacheConnector.fetchAndGetReviewDetailsForSession(Matchers.any[HeaderCarrier]()))
          .thenReturn(Some(validModel))
        val result = TestService.getReviewBusinessCustomerDetails
        await(result) shouldBe Some(validModel)
      }

    }

    "dataCacheConnector returns nothing" should {

      "return a None" in {
        when(TestService.dataCacheConnector.fetchAndGetReviewDetailsForSession(Matchers.any[HeaderCarrier]()))
          .thenReturn(None)
        val result = TestService.getReviewBusinessCustomerDetails
        await(result) shouldBe None
      }

    }

  }

}
