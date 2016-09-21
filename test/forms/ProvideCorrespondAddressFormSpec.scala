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

import forms.ProvideCorrespondAddressForm._
import models.ProvideCorrespondAddressModel
import play.api.i18n.Messages
import uk.gov.hmrc.play.test.UnitSpec

class provideCorrespondAddressFormSpec extends UnitSpec {

  "Creating a form using an empty model" should {
    lazy val form = provideCorrespondAddressForm
    "return an empty string for addressline1, addressline2, telephone number and email" in {
      form.data.isEmpty shouldBe true
    }
  }

  "Creating a form using a valid model" should {
    "return a form with the data specified in the model" in {
      val model = ProvideCorrespondAddressModel("Akina Speed Stars","Mt. Akina","","","","Japan")
      val form = provideCorrespondAddressForm.fill(model)

      form.data("addressline1") shouldBe "Akina Speed Stars"
      form.data("addressline2") shouldBe "Mt. Akina"
      form.data("addressline3") shouldBe ""
      form.data("addressline4") shouldBe ""
      form.data("postcode") shouldBe ""
      form.data("country") shouldBe "Japan"
      form.errors.length shouldBe 0
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for addressline1" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "",
        "addressline2" -> "Mt. Akina",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "addressline1"
      }
      "associate the correct error message to the error" in {
        form.error("addressline1").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for addressline2" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "Akina Speed Stars",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "addressline2"
      }
      "associate the correct error message to the error" in {
        form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for country" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "Akina Speed Stars",
        "addressline2" -> "Mt. Akina",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 1 form error" in {
        form.errors.length shouldBe 1
        form.errors.head.key shouldBe "country"
      }
      "associate the correct error message to the error" in {
        form.error("country").get.message shouldBe Messages("validation.error.country")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for addressline1 and addressline2" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "Japan")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 2 form errors" in {
        form.errors.length shouldBe 2
        form.errors.head.key shouldBe "addressline1"
        form.errors(1).key shouldBe "addressline2"
      }
      "associate the correct error message to the error" in {
        form.error("addressline1").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
        form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for addressline2 and country" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "Akina Speed Stars",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 2 form errors" in {
        form.errors.length shouldBe 2
        form.errors.head.key shouldBe "addressline2"
        form.errors(1).key shouldBe "country"
      }
      "associate the correct error message to the error" in {
        form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
        form.error("country").get.message shouldBe Messages("validation.error.country")
      }
    }
  }

  "Creating a form using an invalid post" when {
    "supplied with no data for addressline1, addressline2 or country" should {
      lazy val form = provideCorrespondAddressForm.bind(Map(
        "addressline1" -> "",
        "addressline2" -> "",
        "addressline3" -> "",
        "addressline4" -> "",
        "postcode" -> "",
        "country" -> "")
      )
      "raise form error" in {
        form.hasErrors shouldBe true
      }
      "raise 3 form errors" in {
        form.errors.length shouldBe 3
        form.errors.head.key shouldBe "addressline1"
        form.errors(1).key shouldBe "addressline2"
        form.errors(2).key shouldBe "country"
      }
      "associate the correct error message to the error" in {
        form.error("addressline1").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
        form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
        form.error("country").get.message shouldBe Messages("validation.error.country")
      }
    }
  }

  "supplied with empty space for addressline1" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "   ",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline1"
    }
    "associate the correct error message to the error " in {
      form.error("addressline1").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
    }
  }

  "supplied with empty space for addressline2" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "   ",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline2"
    }
    "associate the correct error message to the error " in {
      form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
    }
  }

  "supplied with empty space for addressline3" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "   ",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline3"
    }
    "associate the correct error message to the error " in {
      form.error("addressline3").get.message shouldBe Messages("validation.error.optionaladdresssline")
    }
  }

  "supplied with empty space for addressline4" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "   ",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline4"
    }
    "associate the correct error message to the error " in {
      form.error("addressline4").get.message shouldBe Messages("validation.error.linefouraddresssline")
    }
  }

  "supplied with empty space for country" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "   ")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("country").get.message shouldBe Messages("validation.error.country")
    }
  }

  "supplied with empty space for postcode" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "   ",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form errors" in {
      form.errors.length shouldBe 1
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "supplied with numeric input for addressline1" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars 86",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for addressline2" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina 86",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for addressline3" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "86",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for addressline4" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "86",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "supplied with numeric input for postcode" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "86",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "supplied with alphanumeric input for country" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "J4pan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "country"
    }
    "associate the correct error message to the error" in {
      form.error("country").get.message shouldBe Messages("validation.error.country")
    }
  }

  //  BVA

  "addressline1 value supplied with the minimum allowed" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "A",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline2 value supplied with the minimum allowed" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "M",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline3 value supplied with the minimum allowed" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "A",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline4 value supplied with the minimum allowed" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "A",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with the minimum allowed" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "JP")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline1 value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars          ",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline2 value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina                  ",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline3 value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "A                          ",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "addressline4 value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "A                 ",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "postcode value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS98 1TL",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with the maximum allowed (on the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Trinidad and Tobago ")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Trinidad and Tobagooo")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "country"
    }
    "associate the correct error message to the error" in {
      form.error("country").get.message shouldBe Messages("validation.error.country")
    }
  }

  "addressline1 value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars           ",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline1"
    }
    "associate the correct error message to the error" in {
      form.error("addressline1").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
    }
  }

  "addressline2 value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina                   ",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline2"
    }
    "associate the correct error message to the error" in {
      form.error("addressline2").get.message shouldBe Messages("validation.error.mandatoryaddresssline")
    }
  }

  "addressline3 value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "A                           ",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline3"
    }
    "associate the correct error message to the error" in {
      form.error("addressline3").get.message shouldBe Messages("validation.error.optionaladdresssline")
    }
  }

  "addressline4 value supplied over the maximum allowed (over the boundary)" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "A                  ",
      "postcode" -> "",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "addressline4"
    }
    "associate the correct error message to the error" in {
      form.error("addressline4").get.message shouldBe Messages("validation.error.linefouraddresssline")
    }
  }

  "postcode value supplied over the maximum allowed (over the boundary) incluses whitespace in the count" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS98 1TL ",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "country value supplied over the maximum allowed (over the boundary) includes whitespace in the count" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "United Republic of Tanzania")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "country"
    }
    "associate the correct error message to the error" in {
      form.error("country").get.message shouldBe Messages("validation.error.country")
    }
  }

  //Postcode Regex

  "postcode value supplied with multiple white space" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS98  1TL",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "postcode value supplied with brackets" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS98 (1TL)",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "postcode value supplied with /" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS98/9 1TL",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "postcode value supplied with lowercase" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "bs98 1tl",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "postcode value supplied with no spaces" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "BS981TL",
      "country" -> "Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "postcode"
    }
    "associate the correct error message to the error" in {
      form.error("postcode").get.message shouldBe Messages("validation.error.postcode")
    }
  }

  "country value supplied with '" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Cote d'Ivoire")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with -" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "Timor-Leste")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with ." should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "St. Lucia")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with a trailing space" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "St. Lucia ")
    )
    "raise form error" in {
      form.hasErrors shouldBe false
    }
    "raise 0 form errors" in {
      form.errors.length shouldBe 0
    }
  }

  "country value supplied with #" should {
    lazy val form = provideCorrespondAddressForm.bind(Map(
      "addressline1" -> "Akina Speed Stars",
      "addressline2" -> "Mt. Akina",
      "addressline3" -> "",
      "addressline4" -> "",
      "postcode" -> "",
      "country" -> "#Japan")
    )
    "raise form error" in {
      form.hasErrors shouldBe true
    }
    "raise 1 form error" in {
      form.errors.length shouldBe 1
      form.errors.head.key shouldBe "country"
    }
    "associate the correct error message to the error" in {
      form.error("country").get.message shouldBe Messages("validation.error.country")
    }
  }
}