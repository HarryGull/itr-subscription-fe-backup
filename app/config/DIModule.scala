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

package config

import auth.{AuthorisedActions, AuthorisedForTAVC}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import connectors._
import services._
import testOnly.connectors._
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.frontend.auth.connectors.{AuthConnector => HMRCAuthConnector}
import uk.gov.hmrc.play.http.ws.WSHttp

class DIModule extends AbstractModule {

  protected def configure(): Unit = {

    // HTTP
    bind(classOf[WSHttp]).to(classOf[Http])

    // Config
    bind(classOf[AppConfig]).to(classOf[FrontendAppConfig])

    // Session caches
    bind(classOf[SessionCache]).annotatedWith(Names.named("Business Customer")).to(classOf[BusinessCustomerSessionCache])
    bind(classOf[SessionCache]).annotatedWith(Names.named("ITR")).to(classOf[ITRSessionCache])

    // Connectors
    bind(classOf[HMRCAuthConnector]).to(classOf[AuthorisationConnector])
    bind(classOf[KeystoreConnector]).to(classOf[KeystoreConnectorImpl])
    bind(classOf[SubscriptionConnector]).to(classOf[SubscriptionConnectorImpl])
    bind(classOf[AuthConnector]).to(classOf[AuthConnectorImpl])

    // Services
    bind(classOf[RegisteredBusinessCustomerService]).to(classOf[RegisteredBusinessCustomerServiceImpl])
    bind(classOf[SubscriptionService]).to(classOf[SubscriptionServiceImpl])
    bind(classOf[AuthService]).to(classOf[AuthServiceImpl])

    // Auth
    bind(classOf[AuthorisedActions]).to(classOf[AuthorisedForTAVC])

    // test only
    bind(classOf[AuthenticatorConnector]).to(classOf[AuthenticatorConnectorImpl])
    bind(classOf[GgStubsConnector]).to(classOf[GgStubsConnectorImpl])
    bind(classOf[DeEnrolmentConnector]).to(classOf[DeEnrolmentConnectorImpl])


  }

}
