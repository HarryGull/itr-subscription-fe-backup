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

package controllers

import auth.MockConfig
import common.BaseTestSpec
import play.api.test.Helpers._


class SignOutControllerSpec extends BaseTestSpec {

  val testController = new SignOutController(mockAuthorisedActions, MockConfig, configuration, messagesApi)

  "SignOutController.signout" should {

    "Redirect to sign-out" in {
      showWithSessionAndAuth(testController.signout())(
        result => {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe
            Some(s"${testController.applicationConfig.ggSignOutUrl}?continue=${testController.applicationConfig.signOutPageUrl}")
        }
      )
    }

  }

  "SignOutController.show" should {

    "Show the signed out page" in {
      showWithoutSession(testController.show())(
        result => {
          status(result) shouldBe OK
        }
      )
    }

  }

}
