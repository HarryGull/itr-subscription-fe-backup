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
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class AuthConnectorSpec extends BaseTestSpec {

  val uri = "/test"

  val testConnector = new AuthConnectorImpl(mockHttp, MockConfig)

  "getAuthority" should {

    "return the http response from http call" in {
      when(mockHttp.GET[HttpResponse](Matchers.eq(s"${testConnector.serviceUrl}/${testConnector.authorityUri}"))
        (Matchers.any(),Matchers.any(), Matchers.any())).thenReturn(HttpResponse(OK))
      val result = testConnector.getAuthority()
      await(result).status shouldBe OK
    }

  }

}
