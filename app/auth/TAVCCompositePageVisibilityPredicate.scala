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

import connectors.KeystoreConnector
import services.{ValidateTokenService, AuthService, RegisteredBusinessCustomerService}
import uk.gov.hmrc.play.frontend.auth.connectors.domain.ConfidenceLevel.L50
import uk.gov.hmrc.play.frontend.auth.{CompositePageVisibilityPredicate, NonNegotiableIdentityConfidencePredicate, PageVisibilityPredicate}

import scala.concurrent.ExecutionContext

class TAVCCompositePageVisibilityPredicate(validateTokenService: ValidateTokenService,
                                           submissionFrontendUrl: String,
                                           businessCustomerFrontendUrl: String,
                                           rbcService: RegisteredBusinessCustomerService,
                                           keystoreConnector: KeystoreConnector,
                                           authService: AuthService)(implicit ec: ExecutionContext)
  extends CompositePageVisibilityPredicate {

  override def children: Seq[PageVisibilityPredicate] = Seq (
    //new PassedThrottlePredicate(validateTokenService, keystoreConnector, submissionFrontendUrl),
    new NonNegotiableIdentityConfidencePredicate(L50),
    new AffinityGroupPredicate(authService),
    new BusinessCustomerPredicate(businessCustomerFrontendUrl, rbcService)
  )

}
