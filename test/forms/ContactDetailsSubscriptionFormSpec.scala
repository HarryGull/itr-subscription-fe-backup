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
      val model = ContactDetailsSubscriptionModel("Percy", "Montague", "06472 778833", "06472 123998", "harry@wishingwell.com")
      val form = contactDetailsSubscriptionForm.fill(model)
      form.data("firstName") shouldBe "Percy"
      form.data("lastName") shouldBe "Montague"
      form.data("telephoneNumber") shouldBe "06472 778833"
      form.data("telephoneNumber2") shouldBe "06472 123998"
      form.data("email") shouldBe "harry@wishingwell.com"
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using a valid model (no telelphone2)" should {
    "return a form with the data specified in the model" in {
      val model = ContactDetailsSubscriptionModel("Percy", "Montague", "06472 778833", "", "harry@wishingwell.com")
      val form = contactDetailsSubscriptionForm.fill(model)
      form.data("firstName") shouldBe "Percy"
      form.data("lastName") shouldBe "Montague"
      form.data("telephoneNumber") shouldBe "06472 778833"
      form.data("telephoneNumber2") shouldBe ""
      form.data("email") shouldBe "harry@wishingwell.com"
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for firstName" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
        "firstName" -> "",
        "lastName" -> "Jones",
        "telephoneNumber" -> "02738 774893",
        "telephoneNumber2" -> "",
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
        "telephoneNumber" -> "02738 774893",
        "telephoneNumber2" -> "",
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

  "Creating a form using an invalid post" when {
    "supplied with no data for telephoneNumber" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
        "firstName" -> "Tim",
        "lastName" -> "Roth",
        "telephoneNumber" -> "",
        "telephoneNumber2" -> "",
        "email" -> "Test@email.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "telephoneNumber"
      }
      "associate the correct error message to the error" in {
        form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for email" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
        "firstName" -> "Tim",
        "lastName" -> "Roth",
        "telephoneNumber" -> "08746 716283",
        "telephoneNumber2" -> "08746 716383",
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
        "telephoneNumber" -> "01387 563748",
        "telephoneNumber2" -> "",
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
        "telephoneNumber" -> "",
        "telephoneNumber2" -> "",
        "email" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 2 form errors" in {
        form.errors.length shouldBe 2
        form.errors.head.key shouldBe "telephoneNumber"
        form.errors(1).key shouldBe "email"
      }
      "associate the correct error message to the error" in {
        form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
        form.error("email").get.message shouldBe Messages("validation.error.email")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for firstName, lastName or telephone number" should {
      lazy val form = contactDetailsSubscriptionForm.bind(Map(
        "firstName" -> "",
        "lastName" -> "",
        "telephoneNumber" -> "",
        "telephoneNumber2" -> "",
        "email" -> "james.helix@hmrcaspire.com")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 3 form errors" in {
        form.errors.length shouldBe 3
        form.errors.head.key shouldBe "firstName"
        form.errors(1).key shouldBe "lastName"
        form.errors(2).key shouldBe "telephoneNumber"
      }
      "associate the correct error message to the error" in {
        form.error("firstName").get.message shouldBe Messages("error.required")
        form.error("lastName").get.message shouldBe Messages("error.required")
        form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
      }
    }
  }

  "supplied with empty space for firstName" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "    ",
      "lastName" -> "Pivot",
      "telephoneNumber" -> "02635 789374",
      "telephoneNumber2" -> "02635 789374",
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
      "telephoneNumber" -> "02635 789374",
      "telephoneNumber2" -> "02635 789374",
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

  "supplied with empty space for telephoneNumber" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "telephoneNumber" -> "     ",
      "telephoneNumber2" -> "",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }

  }

  "supplied with empty space for telephoneNumber2" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "telephoneNumber" -> "01788777627",
      "telephoneNumber2" -> "     ",
      "email" -> "Matt.Divet@hmrcaspire.com")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber2").get.message shouldBe Messages("validation.error.telephoneNumber")
    }

  }

  "supplied with empty space for email" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Matt",
      "lastName" -> "Pivot",
      "telephoneNumber" -> "02635 789374",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "03782 098372",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "03782 098372",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "OI782 O98372",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "supplied with alphanumeric input for telephone number2" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "01782333829",
      "telephoneNumber2" -> "OI782 O98372",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber2"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber2").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "supplied with alphanumeric input for email" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "01782 098372",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "01375 869472",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "01375 869472",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with the minimum allowed" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "0",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "01375 869472",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "01375 869472",
      "telephoneNumber2" -> "",
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
      "telephoneNumber" -> "01375 869472",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "467846328764987832176776",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber2 value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "01788625142",
      "telephoneNumber2" -> "467846328764987832176776",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "1234567890123456789012345",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber2 value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "01782555627",
      "telephoneNumber2" -> "1234567890123456789012345",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber2"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber2").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "123456789012345 789 01245",
      "telephoneNumber2" -> "",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber2 value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Doug",
      "lastName" -> "Perry",
      "telephoneNumber" -> "01728385968",
      "telephoneNumber2" -> "123456789012345 789 01245",
      "email" -> "Doug.Perry@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber2"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber2").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  //Telephone Number Regex

  "telephoneNumber value supplied with multiple white space" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "McShane",
      "telephoneNumber" -> "0 13 8 4 5 5 5 8 6 9",
      "telephoneNumber2" -> "",
      "email" -> "jules.mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with brackets" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "(01548) 665599",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with +44" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "+447567728337",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with /" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "0/13/84/55/33/82",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with #" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "#06534879542",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with *" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "*06534879542",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with :" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "06534:879542",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with - (American)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "+1 855-953-3597",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with - (France)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "+33(0)644444444",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with ext (extensions)" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "+44 1611234567 ext 123",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with . " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "00336.44.44.44.44",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  "telephoneNumber value supplied with a leading space " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> " 01384 512364",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "telephoneNumber value supplied with a trailing space " should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Jules",
      "lastName" -> "Mcshane",
      "telephoneNumber" -> "01384 512364 ",
      "telephoneNumber2" -> "",
      "email" -> "Jules.Mcshane@digital.hmrc.gov.uk.")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "telephoneNumber"
    }
    "associate the correct error message to the error" in {
      form.error("telephoneNumber").get.message shouldBe Messages("validation.error.telephoneNumber")
    }
  }

  //Email Regex

  "email supplied with multiple white spaces" should {
    lazy val form = contactDetailsSubscriptionForm.bind(Map(
      "firstName" -> "Pat",
      "lastName" -> "Butcher",
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "07675 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08785 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08876 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "09827 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 826273",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "098766 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08176 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 284432",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 872816",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08425 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 843375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "01475 849375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 849395",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 149375",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 849335",
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
      "telephoneNumber" -> "08475 849375",
      "telephoneNumber2" -> "08475 841375",
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
