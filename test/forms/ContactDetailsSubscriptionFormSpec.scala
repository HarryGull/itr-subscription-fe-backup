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

package forms

import forms.ContactDetailsSubscriptionForm._
import models.ContactDetailsSubscriptionModel
import play.api.i18n.Messages
import uk.gov.hmrc.play.test.UnitSpec

class ContactDetailsSubscriptionFormSpec extends UnitSpec {

  "Creating a form using an empty model" should {
    lazy val form = contactDetailsSubscriptionForm
    "return an empty string for firstName, lastName, telephone number and email" in {
      form.data.isEmpty shouldBe true
    }
  }

  "Creating a form using a valid model (with telelphone2)" should {
    "return a form with the data specified in the model" in {
      val model = ContactDetailsSubscriptionModel("Percy", "Montague", Some("06472 778833"), Some("06472 123998"), "harry@wishingwell.com")
      val form = contactDetailsSubscriptionForm.fill(model)
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
      val form = contactDetailsSubscriptionForm.fill(model)
      form.data("firstName") shouldBe "Percy"
      form.data("lastName") shouldBe "Montague"
      form.data("landline") shouldBe "06472 778833"
      form.data("email") shouldBe "harry@wishingwell.com"
      form.data.size shouldBe 4
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for firstName" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("firstName").get.message shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for lastName" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("lastName").get.message shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using a valid post" when {
    "supplied with no data for landline" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("email").get.message shouldBe Messages("validation.error.email")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for firstName and lastName" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("firstName").get.message shouldBe Messages("error.required")
        form.error("lastName").get.message shouldBe Messages("error.required")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for telephone number and email" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("email").get.message shouldBe Messages("validation.error.email")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for firstName, lastName or telephone number" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
        form.error("firstName").get.message shouldBe Messages("error.required")
        form.error("lastName").get.message shouldBe Messages("error.required")
      }
    }
  }

  "supplied with empty space for firstName" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
      form.error("firstName").get.message shouldBe Messages("error.required")
    }
  }

  "supplied with empty space for lastName" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
      form.error("lastName").get.message shouldBe Messages("error.required")
    }
  }

  "supplied with empty space for landline" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "landline" -> "     ",
      "mobile" -> "",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }

  }

  "supplied with empty space for mobile" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "landline" -> "01788777627",
      "mobile" -> "     ",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("mobile").get.message shouldBe Messages("validation.error.telephoneNumber")
    }

  }

  "supplied with empty space for email" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
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
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "supplied with numeric input for firstName" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "D0ug",
      "lastName" -> "Perry",
      "landline" -> "03782 098372",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for lastName" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "P3rry",
      "landline" -> "03782 098372",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with alphanumeric input for telephone number" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "OI782 O98372",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "supplied with alphanumeric input for telephone number2" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01782333829",
      "mobile" -> "OI782 O98372",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "mobile"
    }
    "associate the correct error message to the error" in {
      form.error("mobile").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "supplied with alphanumeric input for email" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01782 098372",
      "mobile" -> "",
      "email" -> "D0ug.P3rry@d1g1tal.hmrc.g0v.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  //  BVA

  "firstName value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "D",
      "lastName" -> "Perry",
      "landline" -> "01375 869472",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "lastName value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "P",
      "landline" -> "01375 869472",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "0",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "email value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01375 869472",
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

  "firstName value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Thisnameisthirtyfivecharacterslongg",
      "lastName" -> "Perry",
      "landline" -> "01375 869472",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "lastName value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Thisnameisthirtyfivecharacterslongg",
      "landline" -> "01375 869472",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "467846328764987832176776",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "mobile value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01788625142",
      "mobile" -> "467846328764987832176776",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "1234567890123456789012345",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "mobile value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01782555627",
      "mobile" -> "1234567890123456789012345",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "mobile"
    }
    "associate the correct error message to the error" in {
      form.error("mobile").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "123456789012345 789 01245",
      "mobile" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "mobile value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "landline" -> "01728385968",
      "mobile" -> "123456789012345 789 01245",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "mobile"
    }
    "associate the correct error message to the error" in {
      form.error("mobile").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  //Telephone Number Regex

  "landline value supplied with multiple white space" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "McShane",
      "landline" -> "0 13 8 4 5 5 5 8 6 9",
      "mobile" -> "",
      "email" -> "jules.mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with brackets" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "(01548) 665599",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with +44" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "+447567728337",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with /" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "0/13/84/55/33/82",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with #" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "#06534879542",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with *" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "*06534879542",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with :" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "06534:879542",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with - (American)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "+1 855-953-3597",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with - (France)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "+33(0)644444444",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with ext (extensions)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "+44 1611234567 ext 123",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with . " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "00336.44.44.44.44",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "landline value supplied with a leading space " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> " 01384 512364",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "landline value supplied with a trailing space " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "landline" -> "01384 512364 ",
      "mobile" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "landline"
    }
    "associate the correct error message to the error" in {
      form.error("landline").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  //Email Regex

  "email supplied with multiple white spaces" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 849375",
      "email" -> "P at@Butche r.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with multiple @" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "07675 849375",
      "email" -> "Pat@Butcher@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "email supplied without @" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08785 849375",
      "email" -> "PatButcher.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with sub domain" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08876 849375",
      "email" -> "PatButcher@subdomain.ntlworld.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "email supplied with firstname.lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "09827 849375",
      "email" -> "Pat.Butcher@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "email supplied with firstName lastName <email@example.com>" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 826273",
      "email" -> "Pat Butcher <Pat.Butcher@HMRC.gov.uk>")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with firstname+lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "098766 849375",
      "email" -> "Pat+Butcher@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "email supplied with firstname_lastname@" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08176 849375",
      "email" -> "Pat_Butcher@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - minimum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 284432",
      "email" -> "P@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - nothing supplied for first part of the email (under the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 872816",
      "email" -> "@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "Part 1 - maximum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08425 849375",
      "email" -> "thisisalongemailthisisalongemailthisisalongemailthisisalongemail@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 1 - too many characters supplied for the first part of the email (over the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 843375",
      "email" -> "thisisalongemailthisisalongemailthisisalongemailthisisalongemailx@HMRC.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "Part 2 - minimum allowed supplied for email (on boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "01475 849375",
      "email" -> "Pat.Butcher@P")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 2 - nothing supplied for second part of the email (under the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 849395",
      "email" -> "Pat.Butcher@")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "Part 2 - maximum allowed supplied for email (on boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 149375",
      "email" -> "Pat.Butcher@thisisalongemailthisisalongemailthisisalongemai.thisisalongemail")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form error" in {
      form.errors.length shouldBe 0
    }
  }

  "Part 2 - too many characters supplied for the second part of the email (over the boundary) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 849335",
      "email" -> "Pat.Butcher@thisisalongemailthisisalongemailthisisalongemai.thisisalongemailx")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "email"
    }
    "associate the correct error message to the error" in {
      form.error("email").get.message shouldBe Messages("validation.error.email")
    }
  }

  "Part 3 - max characters supplied for the email (on both boundaries) " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "landline" -> "08475 849375",
      "mobile" -> "08475 841375",
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
