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

package views

import connectors.KeystoreConnector
import controllers.helpers.FakeRequestHelper
import controllers.{ProvideCorrespondAddressController, routes}
import forms.ProvideCorrespondAddressForm._
import models.ProvideCorrespondAddressModel
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.html.registrationInformation.ProvideCorrespondAddress

class ProvideCorrespondAddressSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper{

  val mockKeystoreConnector = mock[KeystoreConnector]

  val provideCorrespondAddressModel = new ProvideCorrespondAddressModel("Akina Speed Stars","Mt. Akina","","","","Japan")
  val emptyProvideCorrespondAddressModel = new ProvideCorrespondAddressModel("","","","","","")

  lazy val form = provideCorrespondAddressForm.bind(Map("addressline1" -> "Akina Speed Stars",
                                                        "addressline2" -> "Mt. Akina",
                                                        "addressline3" -> "",
                                                        "addressline4" -> "",
                                                        "postcode" -> "",
                                                        "country" -> "Japan"))

  lazy val emptyForm = provideCorrespondAddressForm.bind(Map("addressline1" -> "",
                                                        "addressline2" -> "",
                                                        "addressline3" -> "",
                                                        "addressline4" -> "",
                                                        "postcode" -> "",
                                                        "country" -> ""))

  lazy val errorForm = provideCorrespondAddressForm.bind(Map("addressline1" -> "Akina Speed Stars",
    "addressline2" -> "Mt. Akina",
    "addressline3" -> "",
    "addressline4" -> "",
    "postcode" -> "",
    "country" -> "J"))

  lazy val page = ProvideCorrespondAddress(form)(authorisedFakeRequest)
  lazy val emptyPage = ProvideCorrespondAddress(emptyForm)(authorisedFakeRequest)
  lazy val errorPage = ProvideCorrespondAddress(errorForm)(authorisedFakeRequest)

  "The Provide Correspondence Address page" should {

    "Verify that the Provide Correspondence Address page contains the correct elements when a valid ProvideCorrespondAddressModel is passed" in {

      lazy val document = {
        Jsoup.parse(contentAsString(page))
      }

      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.ConfirmCorrespondAddressController.show().toString()
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.registration")
      document.body.getElementById("addressline1").`val`() shouldBe provideCorrespondAddressModel.addressline1
      document.body.getElementById("addressline2").`val`() shouldBe provideCorrespondAddressModel.addressline2
      document.body.getElementById("addressline3").`val`() shouldBe provideCorrespondAddressModel.addressline3
      document.body.getElementById("addressline4").`val`() shouldBe provideCorrespondAddressModel.addressline4
      document.body.getElementById("postcode").`val`() shouldBe provideCorrespondAddressModel.postcode
      document.body.getElementById("country").`val`() shouldBe provideCorrespondAddressModel.country
      document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
    }

    "Verify that the Provide Correspondence Address page contains the correct elements " +
      "when an empty ProvideCorrespondAddressModel is passed" in {

      lazy val document = {
        Jsoup.parse(contentAsString(emptyPage))
      }

      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.ConfirmCorrespondAddressController.show().toString()
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.registration")
      document.body.getElementById("get-help-action").text shouldBe  Messages("common.error.help.text")
      document.getElementById("error-summary-display").hasClass("error-summary--show")
      document.getElementById("country-error-summary").text should include(Messages("validation.error.country"))
      document.getElementById("addressline1-error-summary").text should include(Messages("validation.error.mandatoryaddresssline"))
      document.getElementById("addressline2-error-summary").text should include(Messages("validation.error.mandatoryaddresssline"))
    }

    "Verify that the Provide Correspondence Address page contains the correct elements " +
      "when an invalid ProvideCorrespondAddressModel is passed" in {

      lazy val document = {
        Jsoup.parse(contentAsString(errorPage))
      }
      document.title() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ProvideCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.ConfirmCorrespondAddressController.show().toString()
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.registration")
      document.body.getElementById("get-help-action").text shouldBe  Messages("common.error.help.text")
      document.getElementById("error-summary-display").hasClass("error-summary--show")
      document.getElementById("country-error-summary").text should include(Messages("validation.error.country"))
    }
  }
}
