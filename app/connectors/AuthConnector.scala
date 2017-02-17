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

import config.{AppConfig, FrontendAppConfig, WSHttp}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http._

import scala.concurrent.Future

object AuthConnector extends AuthConnector {
  override lazy val http = WSHttp
  override lazy val applicationConfig = FrontendAppConfig
}

trait AuthConnector {

  val applicationConfig : AppConfig
  val http : HttpGet

  lazy val serviceUrl = applicationConfig.authUrl
  val authorityUri = "auth/authority"

  def getAuthority()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.GET[HttpResponse](s"$serviceUrl/$authorityUri")
  }

  def getUserDetails(uri: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.GET[HttpResponse](s"$serviceUrl$uri")
  }

}
