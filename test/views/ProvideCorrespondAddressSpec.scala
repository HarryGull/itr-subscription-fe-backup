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

package views

import auth.MockConfig
import common.BaseTestSpec
import models.ProvideCorrespondAddressModel
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.registrationInformation.ProvideCorrespondAddress

class ProvideCorrespondAddressSpec extends BaseTestSpec {

  val provideCorrespondAddressModel = new ProvideCorrespondAddressModel("Line 1","Line 2",countryCode = "JP")
  val form = provideCorrespondAddressForm.form.bind(Map("addressline1" -> "Line 1", "addressline2" -> "Line 2",
    "addressline3" -> "", "addressline4" -> "", "postcode" -> "", "countryCode" -> "JP"))
  val emptyForm = provideCorrespondAddressForm.form.bind(Map("addressline1" -> "", "addressline2" -> "", "addressline3" -> "",
    "addressline4" -> "", "postcode" -> "", "countryCode" -> ""))
  val errorForm = provideCorrespondAddressForm.form.bind(Map("addressline1" -> "Line 1", "addressline2" -> "Line 2",
    "addressline3" -> "", "addressline4" -> "", "postcode" -> "", "countryCode" -> ""))
  val countriesList : List[(String, String)] = List(("JP","Japan"),("GB","United Kingdom"))
  lazy val page = ProvideCorrespondAddress(form, countriesList)(authenticatedFakeRequest(), request2Messages(authenticatedFakeRequest()), MockConfig)
  lazy val emptyPage = ProvideCorrespondAddress(emptyForm, countriesList)(authenticatedFakeRequest(), request2Messages(authenticatedFakeRequest()), MockConfig)
  lazy val errorPage = ProvideCorrespondAddress(errorForm, countriesList)(authenticatedFakeRequest(), request2Messages(authenticatedFakeRequest()), MockConfig)

  "The Provide Correspondence Address page" should {

    "Verify that the Provide Correspondence Address page contains the correct elements when a valid ProvideCorrespondAddressModel is passed" in {
      val document = Jsoup.parse(page.body)

      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.body.getElementById("addressline1").`val`() shouldBe provideCorrespondAddressModel.addressline1
      document.body.getElementById("addressline2").`val`() shouldBe provideCorrespondAddressModel.addressline2
      document.body.getElementById("addressline3").`val`() shouldBe ""
      document.body.getElementById("addressline4").`val`() shouldBe ""
      document.body.getElementById("postcode").`val`() shouldBe ""
      document.body.select("select[name=countryCode] option[selected]").`val`() shouldBe provideCorrespondAddressModel.countryCode
      document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
    }

    "Verify that the Provide Correspondence Address page contains the correct elements when an empty ProvideCorrespondAddressModel is passed" in {
      val document = Jsoup.parse(emptyPage.body)

      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("get-help-action").text shouldBe  Messages("common.error.help.text")
      document.getElementById("error-summary-display").hasClass("error-summary--show")
      document.getElementById("countryCode-error-summary").text should include(Messages("validation.error.countryCode"))
      document.getElementById("addressline1-error-summary").text should include(Messages("validation.error.mandatoryaddresssline"))
      document.getElementById("addressline2-error-summary").text should include(Messages("validation.error.mandatoryaddresssline"))
    }

    "Verify that the Provide Correspondence Address page contains the correct elements when an invalid ProvideCorrespondAddressModel is passed" in {
      val document = Jsoup.parse(errorPage.body)

      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("get-help-action").text shouldBe  Messages("common.error.help.text")
      document.getElementById("error-summary-display").hasClass("error-summary--show")
     document.getElementById("countryCode-error-summary").text should include(Messages("validation.error.countryCode"))
    }
  }
}
