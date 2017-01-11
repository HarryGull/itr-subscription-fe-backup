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

import akka.stream.Materializer
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import testOnly.connectors.{AuthenticatorConnector, GgStubsConnector}
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec
import play.mvc.Http.Status._
import utils.FakeRequestHelper

import scala.concurrent.Future

class ResetEnrolmentsControllerSpec extends UnitSpec with FakeRequestHelper with OneAppPerSuite with MockitoSugar {

  object TestResetEnrolmentsController extends ResetEnrolmentsController {
    override val ggStubsConnector = mock[GgStubsConnector]
    override val authenticatorConnector = mock[AuthenticatorConnector]
  }

  implicit lazy val materializer: Materializer = app.materializer

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("session1234")))

  def mockGgStubsResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(TestResetEnrolmentsController.ggStubsConnector.resetEnrolments()(Matchers.any[HeaderCarrier]()))
    .thenReturn(Future.successful(response))

  def mockAuthenticatorResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(TestResetEnrolmentsController.authenticatorConnector.refreshProfile()(Matchers.any[HeaderCarrier]()))
      .thenReturn(Future.successful(response))

  "ResetEnrolmentsController" should {
    "use the correct GG stubs connector" in {
      ResetEnrolmentsController.ggStubsConnector shouldBe GgStubsConnector
    }
    "use the correct Authenticator connector" in {
      ResetEnrolmentsController.authenticatorConnector shouldBe AuthenticatorConnector
    }
  }

  "Calling ResetEnrolmentsController.resetEnrolments" when {

    "An OK response is returned from GG Stubs and an OK response is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully Reset Enrolments"
      }
    }

    "An OK response is returned from GG Stubs and a response other than OK is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully Reset Enrolments"
      }
    }

    "A response other than OK is returned from GG Stubs and Authenticator" should {

      "Return status BAD_REQUEST (400)" in {
        mockGgStubsResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe BAD_REQUEST
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = TestResetEnrolmentsController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Failed to Reset Enrolments"
      }
    }
  }

}
