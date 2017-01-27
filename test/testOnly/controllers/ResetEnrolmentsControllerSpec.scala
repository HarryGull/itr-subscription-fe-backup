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
import common.BaseTestSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import testOnly.connectors.{AuthenticatorConnector, GgStubsConnector}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import play.mvc.Http.Status._

import scala.concurrent.Future

class ResetEnrolmentsControllerSpec extends BaseTestSpec {

  val mockGgStubsConnector = mock[GgStubsConnector]
  val mockAuthenticatorConnector = mock[AuthenticatorConnector]
  val testController = new ResetEnrolmentsController(mockGgStubsConnector, mockAuthenticatorConnector)

  implicit lazy val materializer: Materializer = app.materializer

  def mockGgStubsResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(mockGgStubsConnector.resetEnrolments()(Matchers.any[HeaderCarrier]()))
    .thenReturn(Future.successful(response))

  def mockAuthenticatorResponse(response: HttpResponse): OngoingStubbing[Future[HttpResponse]] =
    when(mockAuthenticatorConnector.refreshProfile()(Matchers.any[HeaderCarrier]()))
      .thenReturn(Future.successful(response))

  "Calling ResetEnrolmentsController.resetEnrolments" when {

    "An OK response is returned from GG Stubs and an OK response is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = testController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = testController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully Reset Enrolments"
      }
    }

    "An OK response is returned from GG Stubs and a response other than OK is returned from Authenticator" should {

      "Return status OK (200)" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = testController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe OK
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(OK))
        mockAuthenticatorResponse(HttpResponse(OK))
        val result = testController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Successfully Reset Enrolments"
      }
    }

    "A response other than OK is returned from GG Stubs and Authenticator" should {

      "Return status BAD_REQUEST (400)" in {
        mockGgStubsResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = testController.resetEnrolments(fakeRequest)
        status(await(result)) shouldBe BAD_REQUEST
      }

      "return a message saying 'Successfully Reset Enrolments'" in {
        mockGgStubsResponse(HttpResponse(BAD_REQUEST))
        mockAuthenticatorResponse(HttpResponse(BAD_REQUEST))
        val result = testController.resetEnrolments(fakeRequest)
        bodyOf(await(result)) shouldBe "Failed to Reset Enrolments"
      }
    }
  }

}
