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

import _root_.helpers.FakeRequestHelper
import common.Constants
import helpers.AuthHelper._
import connectors.KeystoreConnector
import controllers.{routes}
import forms.ConfirmCorrespondAddressForm._
import models.{ConfirmCorrespondAddressModel}
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.html.registrationInformation.ConfirmCorrespondAddress


class ConfirmCorrespondAddressSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper{

  val mockKeystoreConnector = mock[KeystoreConnector]

  val confirmCorrespondAddressModel = new ConfirmCorrespondAddressModel(Constants.StandardRadioButtonYesValue)
  val emptyConfirmCorrespondAddressModel = new ConfirmCorrespondAddressModel("")

  lazy val form = confirmCorrespondAddressForm.bind(Map("contactAddressUse" -> Constants.StandardRadioButtonYesValue))
  lazy val emptyForm = confirmCorrespondAddressForm.bind(Map("contactAddressUse" -> ""))
  lazy val pageMax = ConfirmCorrespondAddress(form,validModel)(authorisedFakeRequest)
  lazy val pageMin = ConfirmCorrespondAddress(form,validModelMin)(authorisedFakeRequest)
  lazy val emptyPage = ConfirmCorrespondAddress(emptyForm,validModel)(authorisedFakeRequest)

  "The Confirm Correspondence Address page" should {

    "Verify that the Confirm Correspondence Address page contains the correct elements when a valid ConfirmCorrespondAddressModel " +
      "and a maximum Company details review model is passed" in {
      lazy val document = {
        Jsoup.parse(contentAsString(pageMax))
      }

      document.title() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.IntroductionController.show().url
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.registration")
      document.body.getElementById("contactAddressUse-yesLabel").text shouldBe  Messages("common.radioYesLabel")
      document.body.getElementById("contactAddressUse-noLabel").text shouldBe  Messages("common.radioNoLabel")
      document.body.select("#contactAddressUse-yes").size() shouldBe 1
      document.body.select("#contactAddressUse-no").size() shouldBe 1
      document.body.getElementById("businessName").text shouldBe validModel.businessName
      document.body.getElementById("businessAddress1").text shouldBe validModel.businessAddress.line_1
      document.body.getElementById("businessAddress2").text shouldBe validModel.businessAddress.line_2
      document.body.getElementById("businessAddress3").text shouldBe validModel.businessAddress.line_3.get
      document.body.getElementById("businessAddress4").text shouldBe validModel.businessAddress.line_4.get
      document.body.getElementById("postcode").text shouldBe validModel.businessAddress.postcode.get
      document.body.getElementById("country").text shouldBe validModel.businessAddress.country
      document.body.getElementById("storedAddressDiv").children().size() shouldBe 7
      document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
    }

    "Verify that the Confirm Correspondence Address page contains the correct elements when a valid ConfirmCorrespondAddressModel " +
      "and a minimal Company details review model is passed" in {
      lazy val document = {
        Jsoup.parse(contentAsString(pageMin))
      }

      document.title() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.IntroductionController.show().url
      document.body.getElementById("progress-section").text shouldBe Messages("common.section.progress.registration")
      document.body.getElementById("contactAddressUse-yesLabel").text shouldBe  Messages("common.radioYesLabel")
      document.body.getElementById("contactAddressUse-noLabel").text shouldBe  Messages("common.radioNoLabel")
      document.body.select("#contactAddressUse-yes").size() shouldBe 1
      document.body.select("#contactAddressUse-no").size() shouldBe 1
      document.body.getElementById("storedAddressDiv").children().size() shouldBe 4
      document.body.getElementById("country").text shouldBe validModelMin.businessAddress.country
      document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
    }

    "Verify that the Confirm Correspondence Address page contains the correct elements " +
      "when an invalid ConfirmCorrespondAddressModel is passed" in {
      lazy val document = {
        Jsoup.parse(contentAsString(emptyPage))
      }

      document.title() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ConfirmCorrespondAddress.heading")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
      document.body.getElementById("back-link").attr("href") shouldEqual routes.IntroductionController.show().url
      document.body.getElementById("progress-section").text shouldBe  Messages("common.section.progress.registration")
      document.body.getElementById("contactAddressUse-yesLabel").text shouldBe  Messages("common.radioYesLabel")
      document.body.getElementById("contactAddressUse-noLabel").text shouldBe  Messages("common.radioNoLabel")
      document.body.select("#contactAddressUse-yes").size() shouldBe 1
      document.body.select("#contactAddressUse-no").size() shouldBe 1
      document.body.getElementById("businessName").text shouldBe validModel.businessName
      document.body.getElementById("businessAddress1").text shouldBe validModel.businessAddress.line_1
      document.body.getElementById("businessAddress2").text shouldBe validModel.businessAddress.line_2
      document.body.getElementById("businessAddress3").text shouldBe validModel.businessAddress.line_3.get
      document.body.getElementById("businessAddress4").text shouldBe validModel.businessAddress.line_4.get
      document.body.getElementById("postcode").text shouldBe validModel.businessAddress.postcode.get
      document.body.getElementById("country").text shouldBe validModel.businessAddress.country
      document.body.getElementById("get-help-action").text shouldBe  Messages("common.error.help.text")
      document.getElementById("error-summary-display").hasClass("error-summary--show")
    }
  }
}