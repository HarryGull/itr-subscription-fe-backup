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

/**
  * Important:
  * This is a Test only Connector to reset the Enrolments. It is not used in production
  */
package testOnly.connectors

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, _}

import scala.concurrent.Future

@Singleton
class GgStubsConnectorImpl @Inject()(http: WSHttp) extends GgStubsConnector with ServicesConfig with RawResponseReads {

  lazy val serviceURL = baseUrl("government-gateway-stubs")
  val resetURI = "test-only/with-refreshed-enrolments/false"

  def resetEnrolments()(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.POSTEmpty(s"$serviceURL/$resetURI")

}

trait GgStubsConnector {

  def resetEnrolments()(implicit hc: HeaderCarrier): Future[HttpResponse]

}