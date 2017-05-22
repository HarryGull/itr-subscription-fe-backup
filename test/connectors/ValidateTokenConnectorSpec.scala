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
import models.etmp.{CorrespondenceDetailsModel, SubscriptionTypeModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.HttpResponse
import play.api.test.Helpers._

class ValidateTokenConnectorSpec extends BaseTestSpec {



  val testConnector = new ValidateTokenConnectorImpl(mockHttp, MockConfig)

  "validateToken" should {
    "return the http response from http call when the tokenId is not empty" in {
      when(mockHttp.GET[Option[Boolean]](Matchers.eq(s"${testConnector.serviceUrl}/investment-tax-relief/token/validate-temporary-token/$tokenId"))
        (Matchers.any(),Matchers.any())).thenReturn(Some(true))
      val result = testConnector.validateToken(Some(tokenId))
      await(result) shouldBe Some(true)
    }

    "return false if the tokenId is empty" in {
      val result = testConnector.validateToken(None)
      await(result) shouldBe Some(false)
    }
  }

}
