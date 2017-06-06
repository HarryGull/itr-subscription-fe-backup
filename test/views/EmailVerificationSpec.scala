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
import models.{ContactDetailsSubscriptionModel, EmailVerificationModel, ProvideCorrespondAddressModel, ReviewCompanyDetailsModel}
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.registrationInformation.{EmailVerification, ReviewCompanyDetails}

class EmailVerificationSpec extends BaseTestSpec {

  val email = "test@test.com"
  lazy val pageMax = EmailVerification(EmailVerificationModel.apply(email))(authenticatedFakeRequest(),
    request2Messages(authenticatedFakeRequest()), MockConfig)

  "Email verification page" should {

    "show when contact details passed" in {
        val document = Jsoup.parse(pageMax.body)

        document.title() shouldBe Messages("page.registrationInformation.EmailVerification.title")
        document.getElementById("main-heading").text() shouldBe Messages("page.registrationInformation.EmailVerification.heading")
        document.body.getElementById("email-one").text shouldBe Messages("page.registrationInformation.EmailVerification.info.one") +
          s" ${email}" + Messages("page.registrationInformation.EmailVerification.info.two")
        document.body.getElementById("help").text shouldBe Messages("page.registrationInformation.EmailVerification.help.link")

        document.body.getElementById("help-text-one").text shouldBe Messages("page.registrationInformation.EmailVerification.help.text.one") +
          " " + Messages("page.registrationInformation.EmailVerification.help.text.two") +
          " " + Messages("page.registrationInformation.EmailVerification.help.text.three") +
          " " + Messages("page.registrationInformation.EmailVerification.help.text.four")

        document.body.getElementById("email-help-link-one").text shouldBe Messages("page.registrationInformation.EmailVerification.help.text.two")
        document.body.getElementById("email-help-link-two").text shouldBe Messages("page.registrationInformation.EmailVerification.help.text.four")
      }

    }

}
