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

import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class DeEnrolmentConnectorSpec extends UnitSpec with MockitoSugar {

  object TestConnector extends DeEnrolmentConnector {
    val serviceURL = "tax-enrolments"
    val deEnrolURI = "de-enrol/test"
    val http = mock[WSHttp]
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("session1234")))
  val jsonError = Json.parse("""{"Message": "Error"}""")

  "Calling resetEnrolments" when {

    "receiving an OK response" should {
      lazy val result = TestConnector.deEnrol()
      lazy val response = await(result)
      "Return OK" in {
        when(TestConnector.http.POST[JsValue,HttpResponse]
          (Matchers.eq(s"${TestConnector.serviceURL}/${TestConnector.deEnrolURI}"), Matchers.any(), Matchers.any())
          (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(OK)))
        val result = TestConnector.deEnrol()
        val response = await(result)
        response.status shouldBe OK
      }
    }
  }

  "receiving a response other than OK" should {
    lazy val result = TestConnector.deEnrol()
    lazy val response = await(result)

    "Return OK" in {
      when(TestConnector.http.POST[JsValue,HttpResponse]
        (Matchers.eq(s"${TestConnector.serviceURL}/${TestConnector.deEnrolURI}"), Matchers.any(), Matchers.any())
        (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(jsonError))))
      response.status shouldBe BAD_REQUEST
    }

    "Return a JSON response" in {
      when(TestConnector.http.POST[JsValue,HttpResponse]
        (Matchers.eq(s"${TestConnector.serviceURL}/${TestConnector.deEnrolURI}"),Matchers.any(), Matchers.any())
        (Matchers.any(), Matchers.any(), Matchers.eq(hc))).thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Some(jsonError))))
      Json.parse(response.body) shouldBe jsonError
    }
  }
}
