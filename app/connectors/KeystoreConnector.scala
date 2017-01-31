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

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import models.CompanyRegistrationReviewDetailsModel
import play.api.libs.json.Format
import uk.gov.hmrc.http.cache.client.{CacheMap, SessionCache}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

@Singleton
class KeystoreConnectorImpl @Inject()(@Named("ITR") itrSessionCache: SessionCache,
                                      @Named("Business Customer") bcSessionCache: SessionCache) extends KeystoreConnector {

  val bcSourceId = "BC_Business_Details"

  def saveFormData[T](key: String, data : T)(implicit hc: HeaderCarrier, format: Format[T]): Future[CacheMap] = {
    itrSessionCache.cache[T](key, data)
  }

  def fetchAndGetFormData[T](key : String)(implicit hc: HeaderCarrier, format: Format[T]): Future[Option[T]] = {
    itrSessionCache.fetchAndGetEntry(key)
  }

  def clearKeystore()(implicit hc : HeaderCarrier) : Future[HttpResponse] = {
    itrSessionCache.remove()
  }

  def fetchAndGetReviewDetailsForSession(implicit hc: HeaderCarrier): Future[Option[CompanyRegistrationReviewDetailsModel]] = {
    bcSessionCache.fetchAndGetEntry[CompanyRegistrationReviewDetailsModel](bcSourceId)
  }
}

trait KeystoreConnector {

  def saveFormData[T](key: String, data : T)(implicit hc: HeaderCarrier, format: Format[T]): Future[CacheMap]

  def fetchAndGetFormData[T](key : String)(implicit hc: HeaderCarrier, format: Format[T]): Future[Option[T]]

  def clearKeystore()(implicit hc : HeaderCarrier) : Future[HttpResponse]

  def fetchAndGetReviewDetailsForSession(implicit hc: HeaderCarrier): Future[Option[CompanyRegistrationReviewDetailsModel]]
}
