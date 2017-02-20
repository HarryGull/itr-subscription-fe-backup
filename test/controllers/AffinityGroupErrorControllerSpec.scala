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
import common.{BaseTestSpec, KeystoreKeys}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.Upstream5xxResponse

import scala.concurrent.Future

class AffinityGroupErrorControllerSpec extends BaseTestSpec {

  val testController = new AffinityGroupErrorController(mockKeystoreConnector, messagesApi, MockConfig)

  "show" should {

    "return OK when keystoreConnector returns an otacToken" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some("test")))
      showWithSessionAndAuth(testController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

    "return OK when keystoreConnector returns nothing" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(testController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

    "return OK when keystoreConnector returns a failed future" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.failed(Upstream5xxResponse("", INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR)))
      showWithSessionAndAuth(testController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

  }

}
