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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.Future
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._


@Singleton
class AuthConnectorImpl @Inject()(http: WSHttp, applicationConfig: AppConfig) extends AuthConnector {

  lazy val serviceUrl = applicationConfig.authUrl
  val authorityUri = "auth/authority"

  def getAuthority()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    http.GET[HttpResponse](s"$serviceUrl/$authorityUri")
  }

}

trait AuthConnector {

  def getAuthority()(implicit hc: HeaderCarrier): Future[HttpResponse]

}
