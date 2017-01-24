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

package services

import connectors.{BusinessCustomerDataCacheConnector, DataCacheConnector}
import models.CompanyRegistrationReviewDetailsModel
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

trait RegisteredBusinessCustomerService {

  val dataCacheConnector: DataCacheConnector

  def getReviewBusinessCustomerDetails(implicit hc: HeaderCarrier) : Future[Option[CompanyRegistrationReviewDetailsModel]] = {
    dataCacheConnector.fetchAndGetReviewDetailsForSession map {
      case Some(data) => Some(data)
      case _ => {
        Logger.warn(s"[RegisteredBusinessService][getReviewBusinessDetails] - No Review Details Found")
        None
      }
    }
  }
}

object RegisteredBusinessCustomerService extends RegisteredBusinessCustomerService {
  val dataCacheConnector: DataCacheConnector = BusinessCustomerDataCacheConnector
}
