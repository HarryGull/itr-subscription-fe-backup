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

package models

import play.api.libs.json.Json

case class AddressModel(line_1 : String,
                        line_2 : String,
                        line_3 : Option[String],
                        line_4 : Option[String],
                        postcode : Option[String],
                        country : String)

object AddressModel {
  implicit val format = Json.format[AddressModel]
  implicit val writes = Json.writes[AddressModel]
}

case class CompanyRegistrationReviewDetailsModel(businessName: String,
                                                 businessType: Option[String],
                                                 businessAddress: AddressModel,
                                                 sapNumber: String,
                                                 safeId: String,
                                                 isAGroup: Boolean,
                                                 directMatch: Boolean,
                                                 agentReferenceNumber: Option[String] = None,
                                                 firstName: Option[String] = None,
                                                 lastName: Option[String] = None)

object CompanyRegistrationReviewDetailsModel {
  implicit val formats = Json.format[CompanyRegistrationReviewDetailsModel]
}
