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

import common.BaseTestSpec
import models.ContactDetailsSubscriptionModel
import play.api.i18n.Messages

class ContactDetailsSubscriptionFormSpec extends BaseTestSpec {

  "Creating a form using an empty model" should {
    lazy val form = contactDetailsSubscriptionForm.form
    "return an empty string for Name, lastName, telephone number and email" in {
      form.data.isEmpty shouldBe true
    }
  }

  "Creating a form using a valid model (with telelphone2)" should {
    "return a form with the data specified in the model" in {
      val model = ContactDetailsSubscriptionModel("Percy", "Montague", Some("06472 778833"), Some("06472 123998"), "harry@wishingwell.com")
      val form = contactDetailsSubscriptionForm.form.fill(model)
      form.data("firstName") shouldBe "Percy"
      form.data("lastName") shouldBe "Montague"
      form.data("landline") shouldBe "06472 778833"
      form.data("mobile") shouldBe "06472 123998"
      form.data("email") shouldBe "harry@wishingwell.com"
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using a valid model (no telelphone2)" should {
    "return a form with the data specified in the model" in {
      val model = ContactDetailsSubscriptionModel("Percy", "Montague", Some("06472 778833"), None, "harry@wishingwell.com")
      val form = contactDetailsSubscriptionForm.form.fill(model)
      form.data("firstName") shouldBe "Percy"
      form.data("lastName") shouldBe "Montague"
      form.data("landline") shouldBe "06472 778833"
      form.data("email") shouldBe "harry@wishingwell.com"
      form.data.size shouldBe 4
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for Name" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "",
        "lastName" -> "Jones",
        "landline" -> "02738 774893",
        "mobile" -> "",
        "email" -> "Test@email.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "firstName"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("firstName").get.message) shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for lastName" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "Tim",
        "lastName" -> "",
        "landline" -> "02738 774893",
        "mobile" -> "",
        "email" -> "Test@email.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "lastName"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("lastName").get.message) shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using a valid post" when {
    "supplied with no data for landline" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "Tim",
        "lastName" -> "Roth",
        "landline" -> "",
        "mobile" -> "",
        "email" -> "Test@email.com")
      )
      "raise no errors" in {
        form.hasErrors shouldBe false
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for email" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "Tim",
        "lastName" -> "Roth",
        "landline" -> "08746 716283",
        "mobile" -> "08746 716383",
        "email" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "email"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for Name and lastName" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "",
        "lastName" -> "",
        "landline" -> "01387 563748",
        "mobile" -> "",
        "email" -> "james.helix@hmrcaspire.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 2 form errors" in {
        form.errors.length shouldBe 2
        form.errors.head.key shouldBe "firstName"
        form.errors(1).key shouldBe "lastName"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("firstName").get.message) shouldBe Messages("error.required")
        Messages(form.error("lastName").get.message) shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for telephone number and email" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "James",
        "lastName" -> "Helix",
        "landline" -> "",
        "mobile" -> "",
        "email" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form errors" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "email"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for Name, lastName or telephone number" should {
      lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
        "firstName" -> "",
        "lastName" -> "",
        "landline" -> "",
        "mobile" -> "",
        "email" -> "james.helix@hmrcaspire.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 2 form errors" in {
        form.errors.length shouldBe 2
        form.errors.head.key shouldBe "firstName"
        form.errors(1).key shouldBe "lastName"
      }
      "associate the correct error message to the error" in {
        Messages(form.error("firstName").get.message) shouldBe Messages("error.required")
        Messages(form.error("lastName").get.message) shouldBe Messages("error.required")
      }
    }
  }

  "supplied with empty space for Name" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "    ",
      "lastName" -> "Pivot",
      "landline" -> "02635 789374",
      "mobile" -> "02635 789374",
      "email" -> "Matt.Pivot@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "firstName"
    }
    "associate the correct error message to the error " in {
      Messages(form.error("firstName").get.message) shouldBe Messages("error.required")
    }
  }

  "supplied with empty space for lastName" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "   ",
      "landline" -> "02635 789374",
      "mobile" -> "02635 789374",
      "email" -> "Matt.Pivot@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "lastName"
    }
    "associate the correct error message to the error " in {
      Messages(form.error("lastName").get.message) shouldBe Messages("error.required")
    }
  }

  "supplied with empty space for landline" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "landline" -> "     ",
      "mobile" -> "",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise no error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }

  }

  "supplied with empty space for mobile" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "landline" -> "01788777627",
      "mobile" -> "     ",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise no errors" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }

  }

  "supplied with empty space for email" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "landline" -> "02635 789374",
      "mobile" -> "",
      "email" -> "    ")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error " in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "supplied with numeric input for Name" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "D0ug",
      "lastName" -> "Last",
      "landline" -> "03782 098372",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for lastName" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "P3rry",
      "landline" -> "03782 098372",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with alphanumeric input for telephone number" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "OI782 O98372",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise no error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with alphanumeric input for telephone number2" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "AAAAAA000000",
      "email" -> "test@test.com")
    )
    "raise no error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with alphanumeric input for email" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  //  BVA

  "Name value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "D",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "lastName value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "P",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "0",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "email value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "D@d.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "Name value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "Thisnameisthirtyfivecharacterslongg",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "lastName value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Thisnameisthirtyfivecharacterslongg",
      "landline" -> "00000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "000000000000000000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "mobile value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "000000000000000000000000",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "1234567890123456789012345",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "mobile value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "1234567890123456789012345",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "mobile"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("mobile").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "123456789012345 789 01245",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "mobile value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "123456789012345 789 01245",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "mobile"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("mobile").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  //Telephone Number Regex

  "landline value supplied with multiple white space" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "0 13 8 4 5 5 5 8 6 9",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with brackets" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "(00000) 000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with +44" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "+44000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with /" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "0/00/00/00/00/00",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise no errors" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with #" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "#000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise no errors" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with *" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "*000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise no errors" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with :" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000:00000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with - (American)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "+1 000-000-0000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with - (France)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "+33(0)000000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
    }
  }

  "landline value supplied with ext (extensions)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "+44 00000000 ext 000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with . " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000.00.00.00.00",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("landline").get.message) shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with a leading space " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> " 00000 000000",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with a trailing space " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000 000000 ",
      "mobile" -> "",
      "email" -> "test@test.com")
    )
    "raise no error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  //Email Regex

  "email supplied with multiple white spaces" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "t est@tes t.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with multiple @" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "0000000000",
      "email" -> "test@@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "email supplied without @" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "0000000000",
      "email" -> "FirstLast.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with sub domain" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "0000000000",
      "email" -> "FirstLast@subdomain.ntlworld.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "email supplied with name.lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "email supplied with Name lastName <email@example.com>" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test <test@test.com>")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with name+lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "name+lastname@")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with name_lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - minimum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "P@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - nothing supplied for  part of the email (under the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "Part 1 - maximum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "thisisalongemailthisisalongemailthisisalongemailthisisalongemail@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - too many characters supplied for the  part of the email (over the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "thisisalongemailthisisalongemailthisisalongemailthisisalongemailx@test.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "Part 2 - minimum allowed supplied for email (on boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@P")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 2 - nothing supplied for second part of the email (under the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "Part 2 - maximum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@thisisalongemailthisisalongemailthisisalongemai.thisisalongemail")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 2 - too many characters supplied for the second part of the email (over the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "test@thisisalongemailthisisalongemailthisisalongemai.thisisalongemailx")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      Messages(form.error("email").get.message) shouldBe Messages("validation.error.email")
    }
  }

  "Part 3 - max characters supplied for the email (on both boundaries) " should {
    lazy val form = contactDetailsSubscriptionForm.form.bind(Map(
      "firstName" -> "First",
      "lastName" -> "Last",
      "landline" -> "00000000000",
      "mobile" -> "00000000000",
      "email" -> "thisisalongemailthisisalongemailthisisalongemailthisisalongemail@thisisalongemailthisisalongemailthisisalongemai.thisisalongemail")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }
}
