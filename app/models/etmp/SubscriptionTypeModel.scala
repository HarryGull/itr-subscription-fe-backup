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

package models.etmp

import models.{ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class IntermediateCorrespondenceDetailsModel(correspondenceAddress: ProvideCorrespondAddressModel,
                                                  contactDetails: ContactDetailsSubscriptionModel)

case class IntermediateSubscriptionTypeModel(correspondenceDetails: IntermediateCorrespondenceDetailsModel)

object IntermediateCorrespondenceDetailsModel {
  implicit val formats = Json.format[IntermediateCorrespondenceDetailsModel]
}

object IntermediateSubscriptionTypeModel {
  implicit val formats = Json.format[IntermediateSubscriptionTypeModel]
}

case class ContactAddressModel (addressLine1 : String,
                                addressLine2: String,
                                addressLine3 : Option[String],
                                addressLine4: Option[String],
                                countryCode : String,
                                postalCode: Option[String])

object ContactAddressModel {

  implicit val reads: Reads[ContactAddressModel] = (
    (__ \"addressline1").read[String] and
      (__ \"addressline2").read[String] and
      (__ \"addressline3").readNullable[String] and
      (__ \"addressline4").readNullable[String] and
      (__ \"countryCode").read[String] and
      (__ \"postcode").readNullable[String]
    )(ContactAddressModel.apply _)

  implicit val writes = Json.writes[ContactAddressModel]
}

case class ContactDetailsModel (phoneNumber : Option[String],
                                mobileNumber: Option[String],
                                faxNumber : Option[String],
                                emailAddress: Option[String])

object ContactDetailsModel {

  implicit val reads: Reads[ContactDetailsModel] = (
    (__ \"telephoneNumber").readNullable[String] and
      (__ \"telephoneNumber2").readNullable[String] and
      (__ \"faxNumber").readNullable[String] and
      (__ \"email").readNullable[String]
    )(ContactDetailsModel.apply _)

  implicit val writes = Json.writes[ContactDetailsModel]
}

case class ContactNameModel (name1 : String,
                             name2: Option[String])

object ContactNameModel {

  implicit val reads: Reads[ContactNameModel] = (
    (__ \"firstName").read[String] and
      (__ \"lastName").readNullable[String]
    )(ContactNameModel.apply _)

  implicit val writes = Json.writes[ContactNameModel]
}

case class CorrespondenceDetailsModel (contactName: Option[ContactNameModel],
                                       contactDetails: Option[ContactDetailsModel],
                                       contactAddress: Option[ContactAddressModel])

object CorrespondenceDetailsModel {

  implicit val reads: Reads[CorrespondenceDetailsModel] = (
    (__ \"contactDetails").readNullable[ContactNameModel] and
      (__ \"contactDetails").readNullable[ContactDetailsModel] and
      (__ \"correspondenceAddress").readNullable[ContactAddressModel]
    )(CorrespondenceDetailsModel.apply _)

  implicit val writes = Json.writes[CorrespondenceDetailsModel]

}

case class SubscriptionTypeModel (correspondenceDetails: CorrespondenceDetailsModel)

object SubscriptionTypeModel {

  implicit val formats = Json.format[SubscriptionTypeModel]
}
