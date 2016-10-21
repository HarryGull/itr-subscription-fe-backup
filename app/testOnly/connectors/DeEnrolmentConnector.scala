/*
 * Copyright 2016 HM Revenue & Customs
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

import config.WSHttp
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._
import scala.concurrent.Future

trait DeEnrolmentConnector extends ServicesConfig with RawResponseReads {

  val serviceURL: String
  val deEnrolURI: String
  val http: HttpGet with HttpPost

  def deEnrol()(implicit hc: HeaderCarrier): Future[HttpResponse] =
    http.POSTEmpty(s"$serviceURL/$deEnrolURI")

}

object DeEnrolmentConnector extends DeEnrolmentConnector {
  val serviceURL = baseUrl("tax-enrolments")
  val deEnrolURI = "de-enrol/HMRC-TAVC-ORG"
  val http = WSHttp
}
