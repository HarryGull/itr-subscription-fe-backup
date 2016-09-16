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

import java.net.{URLEncoder, URI}

import config.AppConfig
import auth.MockConfig
import org.joda.time.{DateTimeZone, DateTime}
import play.api.test.FakeRequest
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.frontend.auth.connectors.domain

package object auth {

    val mockConfig: AppConfig = MockConfig
    val mockAuthConnector = MockAuthConnector

    object ggSession {
      val userId = "/auth/oid/1234567890"
      val oid = "1234567890"
      val governmentGatewayToken = "token"
      val name = "Dave Agent"
    }


    object ggUser {

      val loggedInAt = Some(new DateTime(2015, 11, 22, 11, 33, 15, 234, DateTimeZone.UTC))

      val previouslyLoggedInAt = Some(new DateTime(2014, 8, 3, 9, 25, 44, 342, DateTimeZone.UTC))

      val sufficientAuthority = domain.Authority(
        uri = ggSession.userId,
        accounts = domain.Accounts(),
        loggedInAt = loggedInAt,
        previouslyLoggedInAt = previouslyLoggedInAt,
        credentialStrength = CredentialStrength.Weak,
        confidenceLevel = ConfidenceLevel.L50,
        userDetailsLink = None,
        enrolments = None,
        ids = None
      )

      val allowedAuthContext = AuthContext(
        authority = sufficientAuthority,
        governmentGatewayToken = Some(ggSession.governmentGatewayToken),
        nameFromSession = Some(ggSession.name)
      )

    }

    import uk.gov.hmrc.play.frontend.auth.AuthenticationProviderIds
    import uk.gov.hmrc.play.http.SessionKeys
    import uk.gov.hmrc.time.DateTimeUtils
    import java.util.UUID

    val mockUsername = "mockuser"
    val mockUserId = "/auth/oid/" + mockUsername

    lazy val fakeRequest = FakeRequest()

    def authenticatedFakeRequest(provider: String = AuthenticationProviderIds.GovernmentGatewayId,
                                 userId: String = mockUserId) =
      FakeRequest().withSession(
        SessionKeys.userId -> userId,
        SessionKeys.sessionId -> s"session-${UUID.randomUUID()}",
        SessionKeys.lastRequestTimestamp -> DateTimeUtils.now.getMillis.toString,
        SessionKeys.token -> "ANYOLDTOKEN",
        SessionKeys.authProvider -> provider
      )

    def weakCredentialsFakeRequest(provider: String = AuthenticationProviderIds.GovernmentGatewayId) =
      authenticatedFakeRequest(provider, "/auth/oid/mockweak")

    def lowConfidenceFakeRequest(provider: String = AuthenticationProviderIds.GovernmentGatewayId) =
      authenticatedFakeRequest(provider, "/auth/oid/mocklowconfidence")

}