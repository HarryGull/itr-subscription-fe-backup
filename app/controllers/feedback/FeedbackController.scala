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

import java.net.URLEncoder

import auth.AuthorisedActions
import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.{Status => HttpStatus}
import play.api.mvc.{Action, AnyContent, Request, RequestHeader}
import play.twirl.api.Html
import config.AppConfig
import handlers.ErrorHandler
import views.html.feedback.feedback_thankyou
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import uk.gov.hmrc.play.partials._
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.Future

@Singleton
class FeedbackController @Inject()(authorised: AuthorisedActions,
                                   http: WSHttp,
                                   implicit val applicationConfig: AppConfig,
                                   val messagesApi: MessagesApi,
                                   errorHandler: ErrorHandler) extends FrontendController with PartialRetriever with I18nSupport {

  def errorPage(implicit request: Request[AnyContent]): Html = errorHandler.impl.internalServerErrorTemplate

  implicit val cachedStaticHtmlPartialRetriever: CachedStaticHtmlPartialRetriever = new CachedStaticHtmlPartialRetriever {
    override lazy val httpGet = http
  }

  implicit val formPartialRetriever: FormPartialRetriever = new FormPartialRetriever {
    override def httpGet = http
    override def crypto: (String) => String = cookie => SessionCookieCryptoFilter.encrypt(cookie)
  }

  def contactFormReferer(implicit request: Request[AnyContent]): String = request.headers.get(REFERER).getOrElse("")
  val localSubmitUrl = routes.FeedbackController.submit().url

  private val TICKET_ID = "ticketId"
  private def feedbackFormPartialUrl(implicit request: Request[AnyContent]) =
    s"${applicationConfig.contactFrontendService}/beta-feedback/form/?submitUrl=${urlEncode(localSubmitUrl)}" +
      s"&service=${urlEncode(applicationConfig.contactFormServiceIdentifier)}&referer=${urlEncode(contactFormReferer)}"
  private def feedbackHmrcSubmitPartialUrl(implicit request: Request[AnyContent]) =
    s"${applicationConfig.contactFrontendService}/beta-feedback/form?resubmitUrl=${urlEncode(localSubmitUrl)}"
  private def feedbackThankYouPartialUrl(ticketId: String)(implicit request: Request[AnyContent]) =
    s"${applicationConfig.contactFrontendService}/beta-feedback/form/confirmation?ticketId=${urlEncode(ticketId)}"

  def show: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    (request.session.get(REFERER), request.headers.get(REFERER)) match {
      case (None, Some(ref)) => Future.successful(Ok(views.html.feedback.feedback(feedbackFormPartialUrl, None))
        .withSession(request.session + (REFERER -> ref)))
      case _ => Future.successful(Ok(views.html.feedback.feedback(feedbackFormPartialUrl, None)))
    }
  }

  def submit: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    request.body.asFormUrlEncoded.map { formData =>
      http.POSTForm[HttpResponse](feedbackHmrcSubmitPartialUrl, formData)(readPartialsForm, partialsReadyHeaderCarrier).map {
        resp =>
          resp.status match {
            case HttpStatus.OK => Redirect(routes.FeedbackController.thankyou()).withSession(request.session + (TICKET_ID -> resp.body))
            case HttpStatus.BAD_REQUEST => BadRequest(views.html.feedback.feedback(feedbackFormPartialUrl, Some(Html(resp.body))))
            case status =>
              Logger.warn(s"Unexpected status code from feedback form: $status")
              InternalServerError(errorPage)
          }
      }
    }.getOrElse {
      Logger.warn("Trying to submit an empty feedback form")
      Future.successful(InternalServerError(errorPage))
    }
  }

  def thankyou: Action[AnyContent] = authorised.async { implicit user => implicit request =>
    val ticketId = request.session.get(TICKET_ID).getOrElse("N/A")
    val referer = request.session.get(REFERER).getOrElse("/investment-tax-relief-subscription/")
    Future.successful(Ok(feedback_thankyou(feedbackThankYouPartialUrl(ticketId), referer)).withSession(request.session - REFERER))
  }

  private def urlEncode(value: String) = URLEncoder.encode(value, "UTF-8")

  private def partialsReadyHeaderCarrier(implicit request: Request[_]): HeaderCarrier = {
    val hc1 = TavcHeaderCarrierForPartialsConverter.headerCarrierEncryptingSessionCookieFromRequest(request)
    TavcHeaderCarrierForPartialsConverter.headerCarrierForPartialsToHeaderCarrier(hc1)
  }

  object TavcHeaderCarrierForPartialsConverter extends HeaderCarrierForPartialsConverter {
    override val crypto = encryptCookieString _

    def encryptCookieString(cookie: String) : String = {
      SessionCookieCryptoFilter.encrypt(cookie)
    }
  }

  implicit val readPartialsForm: HttpReads[HttpResponse] = new HttpReads[HttpResponse] {
    def read(method: String, url: String, response: HttpResponse): HttpResponse = response
  }

  override def httpGet: WSHttp = http

  override protected def loadPartial(url: String)(implicit request: RequestHeader) = ???
}
