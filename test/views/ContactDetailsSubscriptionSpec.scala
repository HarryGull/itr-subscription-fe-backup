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

import controllers.routes
import models.ContactDetailsSubscriptionModel
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import forms.ContactDetailsSubscriptionForm._
import helpers.FakeRequestHelper
import views.html.registrationInformation.ContactDetailsSubscription


class ContactDetailsSubscriptionSpec extends UnitSpec with WithFakeApplication with MockitoSugar with FakeRequestHelper{

  "The Contact Details page" should {

    "Verify that the contact details page contains the correct elements when a valid ContactDetailsSubscriptionModel is passed" in {

      val contactDetailsSubscriptionModel = new ContactDetailsSubscriptionModel(
        "First","Last",Some("00000 000000"), Some("00000000000"), "test@test.com"
      )
      lazy val form = contactDetailsSubscriptionForm.fill(contactDetailsSubscriptionModel)
      lazy val page = ContactDetailsSubscription(form)(authorisedFakeRequest)
      lazy val document = Jsoup.parse(page.body)

      document.title() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.heading")
      document.getElementById("label-firstName").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.firstName.label")
      document.getElementById("label-lastName").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.lastName.label")
      document.getElementById("label-landline").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber.label")
      document.getElementById("label-mobile").text() shouldBe
        Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber2.label")
      document.getElementById("label-email").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.email.label")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
    }

    "Verify that the contact details page contains the correct elements when a valid (empty) ContactDetailsSubscriptionModel is passed" in {

      val emptyForm = contactDetailsSubscriptionForm.fill(new ContactDetailsSubscriptionModel("", "" , None, None, ""))
      lazy val page = ContactDetailsSubscription(emptyForm)(authorisedFakeRequest)
      lazy val document = Jsoup.parse(page.body)

      document.title() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.heading")
      document.getElementById("label-firstName").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.firstName.label")
      document.getElementById("label-lastName").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.lastName.label")
      document.getElementById("label-landline").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber.label")
      document.getElementById("label-mobile").text() shouldBe
        Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber2.label")
      document.getElementById("label-email").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.email.label")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
    }

    "Verify that the proposed investment page contains the correct elements (error elements) when an invalid ContactDetailsSubscriptionModel is submitted" in {

      val emptyForm = contactDetailsSubscriptionForm.bind(Map("firstname" -> "", "lastname" -> "", "telephone" -> "", "telephone2" -> "", "email" -> ""))
      lazy val emptyPage = ContactDetailsSubscription(emptyForm)(authorisedFakeRequest)
      lazy val document = Jsoup.parse(emptyPage.body)

      document.title() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.title")
      document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.ContactDetailsSubscription.heading")
      document.getElementById("label-firstName").text() contains Messages("page.registrationInformation.ContactDetailsSubscription.firstName.label")
      document.getElementById("label-lastName").text() contains Messages("page.registrationInformation.ContactDetailsSubscription.lastName.label")
      document.getElementById("label-landline").text() contains Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber.label")
      document.getElementById("label-mobile").text() contains
        Messages("page.registrationInformation.ContactDetailsSubscription.telephoneNumber2.label")
      document.getElementById("label-email").text() contains Messages("page.registrationInformation.ContactDetailsSubscription.email.label")
      document.getElementById("next").text() shouldBe Messages("common.button.continue")
//      document.getElementById("error-summary-display").hasClass("error-summary--show")

      document.getElementById("firstName-error-summary").text should include(Messages("common.error.missingField"))
      document.getElementById("lastName-error-summary").text should include(Messages("common.error.missingField"))
      document.getElementById("email-error-summary").text should include(Messages("validation.error.email"))


    }
  }
}
