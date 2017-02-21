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
import org.jsoup.Jsoup
import views.html.warnings.AffinityGroupError
import play.api.i18n.Messages

class AffinityGroupErrorSpec extends BaseTestSpec {

  val signInURL = "sign-in"
  val createAccountURL = "register"
  lazy val page = AffinityGroupError(signInURL, createAccountURL)(authenticatedFakeRequest(), request2Messages(authenticatedFakeRequest()), MockConfig)

  "AffinityGroupError" should {

    "Display the correct content" in {
      val document = Jsoup.parse(page.body)

      document.title() shouldBe Messages("page.error.AffinityGroupError.title")
      document.getElementById("not-a-company-header").text() shouldBe Messages("page.error.AffinityGroupError.header")
      document.getElementById("not-a-company").text() shouldBe Messages("page.error.AffinityGroupError.description.one")
      document.getElementById("sign-in").text() shouldBe Messages("page.error.AffinityGroupError.description.two.one") + " " +
        Messages("page.error.AffinityGroupError.description.two.link") + " " + Messages("page.error.AffinityGroupError.description.two.two")
      document.getElementById("sign-in-link").attr("href") shouldBe signInURL
      document.getElementById("create-account").text() shouldBe Messages("page.error.AffinityGroupError.description.three.one") + " " +
        Messages("page.error.AffinityGroupError.description.three.link") + " " + Messages("page.error.AffinityGroupError.description.three.two")
      document.getElementById("create-account-link").attr("href") shouldBe createAccountURL
    }

  }

}
