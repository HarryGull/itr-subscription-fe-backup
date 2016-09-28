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

package connectors

import config.{TavcSessionCache, BusinessCustomerSessionCache}
import models.{CompanyRegistrationReviewDetailsModel}
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait DataCacheConnector {

  val sessionCache: SessionCache

  val bcSourceId: String = "BC_Business_Details"
  val addressFormId: String = "Correspondence_Address"
  val contactFormId: String = "Contact_Details"

  def fetchAndGetReviewDetailsForSession(implicit hc: HeaderCarrier): Future[Option[CompanyRegistrationReviewDetailsModel]] = {
    sessionCache.fetchAndGetEntry[CompanyRegistrationReviewDetailsModel](bcSourceId)
  }
}

object BusinessCustomerDataCacheConnector extends DataCacheConnector {
  val sessionCache: SessionCache = BusinessCustomerSessionCache
}

object TavcSubscriptionDataCacheConnector extends DataCacheConnector {
  val sessionCache: SessionCache = TavcSessionCache
}
