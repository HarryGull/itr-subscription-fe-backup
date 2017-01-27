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

import com.google.inject.Inject
import models.ProvideCorrespondAddressModel
import utils.Validation
import play.api.data.Form
import play.api.data.Forms._

class ProvideCorrespondAddressForm @Inject()(validation: Validation) {
  val form = Form(
    mapping(
      "addressline1" -> validation.mandatoryAddressLineCheck,
      "addressline2" -> validation.mandatoryAddressLineCheck,
      "addressline3" -> optional(validation.optionalAddressLineCheck),
      "addressline4" -> optional(validation.addressLineFourCheck),
      "postcode" -> optional(validation.postcodeCheck),
      "countryCode" -> validation.countryCodeCheck
    )(ProvideCorrespondAddressModel.apply)(ProvideCorrespondAddressModel.unapply).verifying(validation.postcodeCountryCheckConstraint)
  )
}
