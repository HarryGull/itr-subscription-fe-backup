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

import com.google.inject.Inject
import connectors.KeystoreConnector
import models.{AddressModel, CompanyRegistrationReviewDetailsModel}
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class RegisteredBusinessCustomerServiceImpl @Inject()(keystoreConnector: KeystoreConnector) extends RegisteredBusinessCustomerService {

  def getReviewBusinessCustomerDetails(implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[Option[CompanyRegistrationReviewDetailsModel]] = {
//    keystoreConnector.fetchAndGetReviewDetailsForSession map {
//      case Some(data) => Some(data)
//      case _ => {
//        Logger.warn(s"[RegisteredBusinessService][getReviewBusinessDetails] - No Review Details Found")
//        None
//      }

        Future(Some(CompanyRegistrationReviewDetailsModel("Company Name", Some("LTD"), AddressModel("gary 1","line 2",Some("line 3"),Some("line 4 3"),Some("TF1 3NY"),"GB"), "222222", "XA0000100085318",false, true)))
    //}
  }

}

trait RegisteredBusinessCustomerService {

  def getReviewBusinessCustomerDetails(implicit hc: HeaderCarrier, ec: ExecutionContext) : Future[Option[CompanyRegistrationReviewDetailsModel]]

}
