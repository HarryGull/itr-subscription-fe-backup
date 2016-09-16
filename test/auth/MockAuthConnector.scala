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

package auth

import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.frontend.auth.connectors.domain.ConfidenceLevel
import uk.gov.hmrc.play.frontend.auth.connectors.domain.ConfidenceLevel.{L500, L50}
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{CredentialStrength, Accounts, Authority, PayeAccount}
import uk.gov.hmrc.play.http.HttpGet


object MockAuthConnector extends AuthConnector {
  override val serviceUrl: String = ""
  override def http: HttpGet = ???

  private def strongStrengthUser: Option[Authority] =
    Some(Authority("mockuser",
      Accounts(),
      None,
      None,
      CredentialStrength.Strong,
      ConfidenceLevel.L50,
      None,
      None,
      None
    ))

  private def weakStrengthUser: Option[Authority] =
    Some(Authority("mockuser",
      Accounts(),
      None,
      None,
      CredentialStrength.Weak,
      ConfidenceLevel.L50,
      None,
      None,
      None
    ))

  private def noStrengthUser: Option[Authority] =
    Some(Authority("mockuser",
      Accounts(),
      None,
      None,
      CredentialStrength.None,
      ConfidenceLevel.L50,
      None,
      None,
      None
    ))
}