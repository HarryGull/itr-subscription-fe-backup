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

package controllers.feedback

import auth.MockConfig
import common.BaseTestSpec
import handlers.ErrorHandler
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.http.Status
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.play.http.HttpResponse
import uk.gov.hmrc.play.partials.FormPartialRetriever

import scala.concurrent.Future

class FeedbackControllerSpec extends BaseTestSpec {

  val testController = new FeedbackController(mockAuthorisedActions, mockHttp, MockConfig, messagesApi, mock[ErrorHandler]) {
    override implicit val formPartialRetriever: FormPartialRetriever = new FormPartialRetriever {
      override def crypto: (String) => String = ???
      override def httpGet = mockHttp
      override def getPartialContent(url: String, templateParameters: Map[String, String], errorMessage: Html)(implicit request: RequestHeader): Html = Html("")
    }
    override def errorPage(implicit request: Request[AnyContent]) = Html("")
  }

  "GET /feedback" should {
    "return feedback page" in {
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe Status.OK
      )
    }

    "capture the referer in the session on initial session on the feedback load" in {
      showWithSessionAndAuth(testController.show)(
        result => status(result) shouldBe Status.OK
      )
    }
  }

  "POST /feedback" should {
    "return form with thank you for valid selections" in {
      when(mockHttp.POSTForm[HttpResponse](Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))

      submitWithSessionAndAuth(testController.submit)(
        result => redirectLocation(result) shouldBe Some(routes.FeedbackController.thankyou().url)
      )
    }

    "return form with errors for invalid selections" in {
      when(mockHttp.POSTForm[HttpResponse](Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.BAD_REQUEST, responseString = Some("<p>:^(</p>"))))
      submitWithSessionAndAuth(testController.submit)(
        result => status(result) shouldBe Status.BAD_REQUEST
      )
    }

    "return error for other http code back from contact-frontend" in {
      when(mockHttp.POSTForm[HttpResponse](Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(
        Future.successful(HttpResponse(FORBIDDEN)))
      showWithSessionAndAuth(testController.submit)(
        result => status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      )
    }

    "return internal server error when there is an empty form" in {
      when(mockHttp.POSTForm[HttpResponse](Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))
      showWithSessionAndAuth(testController.submit)(
        result => status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      )
    }
  }

  "GET /feedback/thankyou" should {
    "should return the thank you page" in {
      showWithSessionAndAuth(testController.thankyou)(
        result => status(result) shouldBe Status.OK
      )
    }
  }
}
