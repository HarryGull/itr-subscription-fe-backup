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

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import models.Email
import models.etmp.SubscriptionTypeModel
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{BadRequestException, HeaderCarrier, HttpResponse}

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SubscriptionConnectorImpl @Inject()(http: WSHttp, applicationConfig: AppConfig) extends SubscriptionConnector {
  lazy val serviceUrl = applicationConfig.subscriptionUrl

  def subscribe(subscriptionModel: SubscriptionTypeModel, safeID: String, postcode: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](s"$serviceUrl/investment-tax-relief-subscription/$safeID/$postcode/subscribe",Json.toJson(subscriptionModel))
  }

  def updateEmail(registrationId: String, email: Email)(implicit hc: HeaderCarrier): Future[Option[Email]] = {
    val json = Json.toJson(email)
    http.PUT[JsValue, Email](s"$serviceUrl/investment-tax-relief-subscription/update-email", json).map{
      e => Some(e)
    } recover {
      case ex: BadRequestException =>
        None
    }
  }
}

trait SubscriptionConnector {

  def subscribe(subscriptionModel: SubscriptionTypeModel, safeID: String, postcode: String)(implicit hc: HeaderCarrier): Future[HttpResponse]
  def updateEmail(registrationId: String, email: Email)(implicit hc: HeaderCarrier): Future[Option[Email]]
}
