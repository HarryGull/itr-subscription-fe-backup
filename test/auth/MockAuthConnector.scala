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

package auth

import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.frontend.auth.connectors.domain.ConfidenceLevel
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, Authority, CredentialStrength}

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.{HeaderCarrier, HttpGet}

object MockAuthConnector extends AuthConnector with MockitoSugar {

  override val http : HttpGet = mock[HttpGet]
  override val serviceUrl: String = ""

  override def currentAuthority(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Authority]] = {
    Future.successful(strongStrengthUser)
  }

  private def strongStrengthUser: Option[Authority] =
    Some(Authority("/auth/oid/mockuser",
      Accounts(),
      None,
      None,
      CredentialStrength.Strong,
      ConfidenceLevel.L50,
      None,
      None,
      None,
      "0000000000"
    ))

}
