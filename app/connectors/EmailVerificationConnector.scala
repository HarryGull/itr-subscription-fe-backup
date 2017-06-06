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

package connectors

import com.google.inject.{Inject, Singleton}
import config.{AppConfig, FrontendGlobal}
import models.EmailVerificationRequest
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.NoStackTrace

private[connectors] class EmailErrorResponse(s: String) extends NoStackTrace

@Singleton
class EmailVerificationConnectorImpl @Inject()(http: WSHttp, applicationConfig: AppConfig) extends EmailVerificationConnector with HttpErrorFunctions {

  lazy val sendVerificationEmailURL = applicationConfig.sendVerificationEmailURL
  lazy val checkVerifiedEmailURL = applicationConfig.checkVerifiedEmailURL

  implicit val reads = new HttpReads[HttpResponse] {
    def read(http: String, url: String, res: HttpResponse) = customRead(http, url, res)
  }

  def checkVerifiedEmail(email : String)(implicit hc : HeaderCarrier) : Future[Boolean] = {
    def errorMsg(status: String) = {
      Logger.debug(s"[EmailVerificationConnector] [checkVerifiedEmail] request to check verified email returned a $status - email not found / not verified")
      false
    }
    http.GET[HttpResponse](s"$checkVerifiedEmailURL/$email") map {
      _.status match {
        case OK => true
        case _ => false
      }
    }recover {
      case ex: NotFoundException => errorMsg("404")
      case ex: InternalServerException => errorMsg("500")
      case ex: BadGatewayException => errorMsg("502")
    }
  }

  def requestVerificationEmail(emailRequest : EmailVerificationRequest)(implicit hc : HeaderCarrier) : Future[HttpResponse] = {
    http.POST[EmailVerificationRequest, HttpResponse](s"$sendVerificationEmailURL", emailRequest)
  }

  def customRead(http: String, url: String, response: HttpResponse) =
    response.status match {
      case 400 => throw new BadRequestException("Provided incorrect data to Email Verification")
      case 404 => throw new NotFoundException("Email not found")
      case 409 => response
      case 500 => throw new InternalServerException("Email service returned an error")
      case 502 => throw new BadGatewayException("Email service returned an upstream error")
      case _ => handleResponse(http, url)(response)
    }
}

trait EmailVerificationConnector {
  def checkVerifiedEmail(email : String)(implicit hc : HeaderCarrier) : Future[Boolean]
  def requestVerificationEmail(emailRequest : EmailVerificationRequest)(implicit hc : HeaderCarrier) : Future[HttpResponse]
}
