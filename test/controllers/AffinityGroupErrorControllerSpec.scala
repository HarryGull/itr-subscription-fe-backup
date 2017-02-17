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
import common.KeystoreKeys
import config.FrontendAppConfig
import connectors.KeystoreConnector
import helpers.FakeRequestHelper
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.Upstream5xxResponse

import scala.concurrent.Future

class AffinityGroupErrorControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite with FakeRequestHelper {

  val mockKeystoreConnector = mock[KeystoreConnector]

  object TestController extends AffinityGroupErrorController {
    override val keystoreConnector = mockKeystoreConnector
    override val applicationConfig = MockConfig
  }

  "AffinityGroupErrorController" should {

    "Use KeystoreConnector" in {
      AffinityGroupErrorController.keystoreConnector shouldBe KeystoreConnector
    }

    "Use FrontendAppConfig" in {
      AffinityGroupErrorController.applicationConfig shouldBe FrontendAppConfig
    }

  }

  "show" should {

    "return OK when keystoreConnector returns an otacToken" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(Some("test")))
      showWithSessionAndAuth(TestController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

    "return OK when keystoreConnector returns nothing" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(None))
      showWithSessionAndAuth(TestController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

    "return OK when keystoreConnector returns a failed future" in {
      when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.otacToken))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.failed(Upstream5xxResponse("", INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR)))
      showWithSessionAndAuth(TestController.show())(
        result =>
          status(result) shouldBe OK
      )
    }

  }

}
