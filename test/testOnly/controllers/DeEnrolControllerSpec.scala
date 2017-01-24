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

package testOnly.controllers

import helpers.FakeRequestHelper
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mock.MockitoSugar
import testOnly.connectors.{AuthenticatorConnector, DeEnrolmentConnector}
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.mvc.Http.Status._

import scala.concurrent.Future

class DeEnrolControllerSpec extends UnitSpec with FakeRequestHelper with WithFakeApplication with MockitoSugar {

  object TestController extends DeEnrolController {
    override val deEnrolmentConnector = mock[DeEnrolmentConnector]
    override val authenticatorConnector = mock[AuthenticatorConnector]
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("session1234")))

  def mockDeEnrolmentResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(TestController.deEnrolmentConnector.deEnrol()(Matchers.any[HeaderCarrier]()))
      .thenReturn(Future.successful(response))

  def mockAuthenticatorResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(TestController.authenticatorConnector.refreshProfile()(Matchers.any[HeaderCarrier]()))
      .thenReturn(Future.successful(response))

  "DeEnrolController" should {
    "use the correct de-enrolment connector" in {
      DeEnrolController.deEnrolmentConnector shouldBe DeEnrolmentConnector
    }
    "use the correct Authenticator connector" in {
      DeEnrolController.authenticatorConnector shouldBe AuthenticatorConnector
    }
  }

  "Calling DeEnrolController.deEnrol" when {

    "An OK response is returned from Tax Enrolments and an OK response is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockDeEnrolmentResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestController.deEnrol(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockDeEnrolmentResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestController.deEnrol(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully De-enrolled"
      }
    }

    "An OK response is returned from Tax Enrolments and a response other than OK is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockDeEnrolmentResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestController.deEnrol(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockDeEnrolmentResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestController.deEnrol(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully De-enrolled"
      }
    }

    "A response other than OK is returned from Tax Enrolments and Authenticator" should {

      "Return status BAD_REQUEST (400)" in {
        mockDeEnrolmentResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestController.deEnrol(fakeRequest)
        status(await(result)) shouldBe BAD_REQUEST
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockDeEnrolmentResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestController.deEnrol(fakeRequest)
        bodyOf(await(result)) shouldBe "Failed to De-enrol"
      }
    }
  }

}
