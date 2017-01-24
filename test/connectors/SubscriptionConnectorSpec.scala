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

import config.WSHttp
import models.etmp.{CorrespondenceDetailsModel, SubscriptionTypeModel}
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._

class SubscriptionConnectorSpec extends UnitSpec with WithFakeApplication with ServicesConfig with MockitoSugar {

  lazy val mockHttp = mock[WSHttp]
  implicit val hc = new HeaderCarrier()
  val safeID = "ABC123"
  val postcode = "AB11AB"
  val url = "localhost"
  val subscriptionModel = SubscriptionTypeModel(CorrespondenceDetailsModel(None,None,None))

  object TestConnector extends SubscriptionConnector {
    override lazy val http = mockHttp
    override lazy val serviceUrl = url
  }

  "SubscriptionConnector" should {

    "Use WSHttp" in {
      SubscriptionConnector.http shouldBe WSHttp
    }

    "Use base url for investment-tax-relief-subscription" in {
      SubscriptionConnector.serviceUrl shouldBe baseUrl("investment-tax-relief-subscription")
    }

  }

  "subscribe" should {

    lazy val result = TestConnector.subscribe(subscriptionModel,safeID,postcode)

    "create a url using the safeID and postcode" in {
      when(mockHttp.POST[JsValue, HttpResponse](Matchers.eq(s"$url/investment-tax-relief-subscription/$safeID/$postcode/subscribe"),
        Matchers.any(),Matchers.any())(Matchers.any(),Matchers.any(),Matchers.any())).thenReturn(HttpResponse(OK))
      await(result).status shouldBe OK
    }

  }

}
