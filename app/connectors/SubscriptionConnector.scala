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

import config.{FrontendAppConfig, WSHttp}
import models.etmp.SubscriptionTypeModel
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpPost, HttpResponse}

import scala.concurrent.Future

object SubscriptionConnector extends SubscriptionConnector {
  override val http = WSHttp
  override val serviceUrl = FrontendAppConfig.subscriptionUrl
}

trait SubscriptionConnector {

  val http: HttpPost
  val serviceUrl: String

  def subscribe(subscriptionModel: SubscriptionTypeModel, safeID: String, postcode: String)
               (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](s"$serviceUrl/investment-tax-relief-subscription/$safeID/$postcode/subscribe",Json.toJson(subscriptionModel))
  }

}
