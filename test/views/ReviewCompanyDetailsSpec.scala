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

import helpers.FakeRequestHelper
import helpers.AuthHelper._
import controllers.routes
import models.{ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel, ReviewCompanyDetailsModel}
import org.jsoup.Jsoup
import org.scalatest.mock.MockitoSugar
import play.api.i18n.Messages
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.html.registrationInformation.ReviewCompanyDetails

class ReviewCompanyDetailsSpec extends UnitSpec with MockitoSugar with WithFakeApplication with FakeRequestHelper {

  val maxAddress = ProvideCorrespondAddressModel("addressline1","addressline2",Some("addressline3"),Some("addressline4"),Some("AA1 1AA"),"GB")
  val maxContact = ContactDetailsSubscriptionModel("firstname","lastname",Some("00000000000"),Some("00000000000"),"test@test.com")
  val maxDetails = ReviewCompanyDetailsModel(validModel,maxAddress,maxContact)
  val minAddress = ProvideCorrespondAddressModel("addressline1","addressline2",None,None,None,"AB")
  val minContact = ContactDetailsSubscriptionModel("firstname","lastname",None,None,"test@test.com")
  val minDetails = ReviewCompanyDetailsModel(validModel,minAddress,minContact)
  lazy val pageMax = ReviewCompanyDetails(maxDetails)(authorisedFakeRequest)
  lazy val pageMin = ReviewCompanyDetails(minDetails)(authorisedFakeRequest)

  "Review Company Details page" when {

    "Passed details with every optional field filled in" should {

      "Display all optional fields on the page" in {
        lazy val document = {
          Jsoup.parse(contentAsString(pageMax))
        }
        document.title() shouldBe Messages("page.registrationInformation.ReviewCompanyDetails.title")
        document.getElementById("main-heading").text() shouldBe
          s"${Messages("page.registrationInformation.ReviewCompanyDetails.heading")}"
        document.body.getElementById("correspondenceAddress-question").text shouldBe
          Messages("page.registrationInformation.ReviewCompanyDetails.correspondenceAddress")
        document.body.getElementById("correspondenceAddress-answer").children().size() shouldBe 6
        document.body.getElementById("addressline1").text shouldBe maxAddress.addressline1
        document.body.getElementById("addressline2").text shouldBe maxAddress.addressline2
        document.body.getElementById("addressline3").text shouldBe maxAddress.addressline3.get
        document.body.getElementById("addressline4").text shouldBe maxAddress.addressline4.get
        document.body.getElementById("postcode").text shouldBe maxAddress.postcode.get
        document.body.getElementById("countrycode").text shouldBe maxAddress.countryCode
        document.body.getElementById("companyContact-question").text shouldBe
          Messages("page.registrationInformation.ReviewCompanyDetails.companyContact")
        document.body.getElementById("companyContact-answer").children().size() shouldBe 4
        document.body.getElementById("name").text shouldBe s"${maxContact.firstName} ${maxContact.lastName}"
        document.body.getElementById("landline").text shouldBe maxContact.telephoneNumber.get
        document.body.getElementById("mobile").text shouldBe maxContact.telephoneNumber2.get
        document.body.getElementById("email").text shouldBe maxContact.email
        document.getElementById("submit").text() shouldBe Messages("page.registrationInformation.ReviewCompanyDetails.button.continue")
        document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
      }

    }

    "Passed details with no optional fields filled in" should {

      "Display no optional fields on the page" in {
        lazy val document = {
          Jsoup.parse(contentAsString(pageMin))
        }
        document.title() shouldBe Messages("page.registrationInformation.ReviewCompanyDetails.title")
        document.getElementById("main-heading").text() shouldBe
          s"${Messages("page.registrationInformation.ReviewCompanyDetails.heading")}"
        document.body.getElementById("correspondenceAddress-question").text shouldBe
          Messages("page.registrationInformation.ReviewCompanyDetails.correspondenceAddress")
        document.body.getElementById("correspondenceAddress-answer").children().size() shouldBe 3
        document.body.getElementById("addressline1").text shouldBe minAddress.addressline1
        document.body.getElementById("addressline2").text shouldBe minAddress.addressline2
        document.body.getElementById("countrycode").text shouldBe minAddress.countryCode
        document.body.getElementById("companyContact-question").text shouldBe
          Messages("page.registrationInformation.ReviewCompanyDetails.companyContact")
        document.body.getElementById("companyContact-answer").children().size() shouldBe 2
        document.body.getElementById("name").text shouldBe s"${minContact.firstName} ${minContact.lastName}"
        document.body.getElementById("email").text shouldBe minContact.email
        document.getElementById("submit").text() shouldBe Messages("page.registrationInformation.ReviewCompanyDetails.button.continue")
        document.body.getElementById("get-help-action").text shouldBe Messages("common.error.help.text")
      }

    }
  }


}
