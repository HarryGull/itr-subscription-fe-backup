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

package models.etmp

import models.{ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class SubscriptionModelSpec extends UnitSpec {

  val maxProvideCorrespondAddressModel = ProvideCorrespondAddressModel("line1","line2",Some("line3"),Some("line4"),Some("AA1 1AA"),"GB")
  val maxContactDetailsSubscriptionModel = ContactDetailsSubscriptionModel("first","last",Some("00000000000"),Some("00000000000"),"test@test.com")
  val maxIntermediateCorrespondenceDetailsModel = IntermediateCorrespondenceDetailsModel(maxProvideCorrespondAddressModel,maxContactDetailsSubscriptionModel)
  val maxIntermediateSubscriptionTypeModel = IntermediateSubscriptionTypeModel(maxIntermediateCorrespondenceDetailsModel)

  val minProvideCorrespondAddressModel = ProvideCorrespondAddressModel("line1","line2",None,None,None,"AA")
  val minContactDetailsSubscriptionModel = ContactDetailsSubscriptionModel("first","last",Some("00000000000"),None,"test@test.com")
  val minIntermediateCorrespondenceDetailsModel = IntermediateCorrespondenceDetailsModel(minProvideCorrespondAddressModel,minContactDetailsSubscriptionModel)
  val minIntermediateSubscriptionTypeModel = IntermediateSubscriptionTypeModel(minIntermediateCorrespondenceDetailsModel)

  val maxContactAddress = ContactAddressModel("line1","line2",Some("line3"),Some("line4"),"GB",Some("AA1 1AA"))
  val maxContactDetails = ContactDetailsModel(Some("00000000000"),Some("00000000000"),None,Some("test@test.com"))
  val maxContactName = ContactNameModel("first",Some("last"))

  val minContactAddress = ContactAddressModel("line1","line2",None,None,"AA",None)
  val minContactDetails = ContactDetailsModel(Some("00000000000"),None,None,Some("test@test.com"))
  val minContactName = ContactNameModel("first",Some("last"))

  "SubscriptionTypeModel" when {

    "Converting from IntermediateSubscriptionTypeModel to SubscriptionTypeModel with all optional values" should {

      lazy val old = Json.toJson(maxIntermediateSubscriptionTypeModel)
      lazy val result = Json.parse(old.toString).as[SubscriptionTypeModel]

      "Convert the contact address correctly" in {
        await(result).correspondenceDetails.contactAddress.get shouldBe maxContactAddress
      }

      "Convert the contact details correctly" in {
        await(result).correspondenceDetails.contactDetails.get shouldBe maxContactDetails
      }

      "Convert the contact name correctly" in {
        await(result).correspondenceDetails.contactName.get shouldBe maxContactName
      }

    }

    "Converting from IntermediateSubscriptionTypeModel to SubscriptionTypeModel with no optional values" should {

      lazy val old = Json.toJson(minIntermediateSubscriptionTypeModel)
      lazy val result = Json.parse(old.toString).as[SubscriptionTypeModel]

      "Convert the contact address correctly" in {
        await(result).correspondenceDetails.contactAddress.get shouldBe minContactAddress
      }

      "Convert the contact details correctly" in {
        await(result).correspondenceDetails.contactDetails.get shouldBe minContactDetails
      }

      "Convert the contact name correctly" in {
        await(result).correspondenceDetails.contactName.get shouldBe minContactName
      }

    }
  }

}
