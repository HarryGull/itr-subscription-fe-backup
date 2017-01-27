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

package testOnly.connectors

import common.BaseTestSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future

class DeEnrolmentConnectorSpec extends BaseTestSpec {
  
  val testConnector = new DeEnrolmentConnectorImpl(mockHttp)

  val jsonError = Json.parse("""{"Message": "Error"}""")

  "Calling resetEnrolments" when {

    "receiving an OK response" should {
      "Return OK" in {
        when(mockHttp.POST[JsValue,HttpResponse]
          (Matchers.eq(s"${testConnector.serviceURL}/${testConnector.deEnrolURI}"), Matchers.any(), Matchers.any())
          (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(OK)))
        val result = testConnector.deEnrol()
        val response = await(result)
        response.status shouldBe OK
      }
    }
  }

  "receiving a response other than OK" should {
    lazy val result = testConnector.deEnrol()
    lazy val response = await(result)

    "Return OK" in {
      when(mockHttp.POST[JsValue,HttpResponse]
        (Matchers.eq(s"${testConnector.serviceURL}/${testConnector.deEnrolURI}"), Matchers.any(), Matchers.any())
        (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(jsonError))))
      response.status shouldBe BAD_REQUEST
    }

    "Return a JSON response" in {
      when(mockHttp.POST[JsValue,HttpResponse]
        (Matchers.eq(s"${testConnector.serviceURL}/${testConnector.deEnrolURI}"),Matchers.any(), Matchers.any())
        (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(jsonError))))
      Json.parse(response.body) shouldBe jsonError
    }
  }
}
