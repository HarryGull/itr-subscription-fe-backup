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
import config.{FrontendAppConfig, WSHttp}
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._

class AuthConnectorSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  lazy val mockHttp = mock[WSHttp]
  implicit val hc = new HeaderCarrier()
  val uri = "/test"

  object TestConnector extends AuthConnector {
    override lazy val http = mockHttp
    override lazy val applicationConfig = MockConfig
  }

  "SubscriptionConnector" should {

    "Use WSHttp" in {
      AuthConnector.http shouldBe WSHttp
    }

    "Use FrontendAppConfig" in {
      AuthConnector.applicationConfig shouldBe FrontendAppConfig
    }

  }

  "getAuthority" should {

    "return the http response from http call" in {
      when(mockHttp.GET[HttpResponse](Matchers.eq(s"${TestConnector.serviceUrl}/${TestConnector.authorityUri}"))
        (Matchers.any(),Matchers.any())).thenReturn(HttpResponse(OK))
      val result = TestConnector.getAuthority()
      await(result).status shouldBe OK
    }

  }

  "getUserDetails" should {

    "build the url using the uri and return the http response" in {
      when(mockHttp.GET[HttpResponse](Matchers.eq(s"${TestConnector.serviceUrl}$uri"))
        (Matchers.any(),Matchers.any())).thenReturn(HttpResponse(OK))
      val result = TestConnector.getUserDetails(uri)
      await(result).status shouldBe OK
    }

  }

}
