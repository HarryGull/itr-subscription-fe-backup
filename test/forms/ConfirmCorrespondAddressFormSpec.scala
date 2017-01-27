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

package forms

import common.{BaseTestSpec, Constants}
import models.ConfirmCorrespondAddressModel
import play.api.i18n.Messages
import play.api.libs.json.Json

class ConfirmCorrespondAddressFormSpec extends BaseTestSpec {

  val confirmCorrespondAddressJson = """{"contactAddressUse":"Yes"}"""
  val confirmCorrespondAddressModel = ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue)

  "The Confirm Correspondence Address Form" should {
    "Return an error if no radio button is selected" in {
      val form = confirmCorrespondAddressForm.form.bind(Map("contactAddressUse" -> ""))
      form.hasErrors shouldBe true
      form.errors.length shouldBe 1
      Messages(form.errors.head.message) shouldBe Messages("error.required")
    }
  }

  "The Confirm Correspondence Address Form" should {
    "not return an error if the 'Yes' option is selected" in {
      val form = confirmCorrespondAddressForm.form.bind(Map("contactAddressUse" -> Constants.StandardRadioButtonYesValue))
      form.hasErrors shouldBe false
    }
  }


  // form model to json - apply
  "The Confirm Correspondence Address Form model" should {
    "call apply correctly on the model" in {
      implicit val formats = Json.format[ConfirmCorrespondAddressModel]
      val form = confirmCorrespondAddressForm.form.fill(confirmCorrespondAddressModel)
      form.get.contactAddressUse shouldBe Constants.StandardRadioButtonYesValue
    }

    // form json to model - unapply
    "call unapply successfully to create expected Json" in {
      implicit val formats = Json.format[ConfirmCorrespondAddressModel]
      val form = confirmCorrespondAddressForm.form.fill(confirmCorrespondAddressModel)
      val formJson = Json.toJson(form.get).toString()
      formJson shouldBe confirmCorrespondAddressJson
    }
  }
}
