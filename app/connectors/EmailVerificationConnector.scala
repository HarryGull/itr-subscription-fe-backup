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
import config.AppConfig
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

  def checkVerifiedEmail(email: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    def errorMsg(status: String) = {
      Logger.info(s"[EmailVerificationConnector] [checkVerifiedEmail] request to check verified email returned a " +
        s"$status - email not found / not verified")
      false
    }

    http.GET[HttpResponse](s"$checkVerifiedEmailURL/$email") map {
      _.status match {
        case OK => true
        case _ => false
      }
    } recover {
      case ex: NotFoundException => errorMsg("404")
      case ex: InternalServerException => errorMsg("500")
      case ex: BadGatewayException => errorMsg("502")
    }
  }

  def requestVerificationEmail(emailRequest: EmailVerificationRequest)(implicit hc: HeaderCarrier): Future[Boolean] = {
    def errorMsg(status: String, ex: HttpException) = {
      Logger.error(s"[EmailVerificationConnector] [requestVerificationEmail] request to send verification " +
        s"email returned a $status - email not sent - reason = ${ex.getMessage}")
      throw new EmailErrorResponse(status)
    }

    http.POST[EmailVerificationRequest, HttpResponse](s"$sendVerificationEmailURL", emailRequest) map { r =>
      r.status match {
        case CREATED => {
          Logger.debug("[EmailVerificationConnector] [requestVerificationEmail] request to verification service successful")
          true
        }
        case CONFLICT => {
          Logger.debug("[EmailVerificationConnector] [requestVerificationEmail] request to send verification email returned " +
            " 409 - email already verified")
          false
        }
      }
    } recover {
      case ex: BadRequestException => errorMsg("400", ex)
      case ex: NotFoundException => errorMsg("404", ex)
      case ex: InternalServerException => errorMsg("500", ex)
      case ex: BadGatewayException => errorMsg("502", ex)
    }
  }

  private def customRead(http: String, url: String, response: HttpResponse) =
    response.status match {
      case BAD_REQUEST => throw new BadRequestException("Provided incorrect data to Email Verification")
      case NOT_FOUND => throw new NotFoundException("Email not found")
      case CONFLICT => response
      case INTERNAL_SERVER_ERROR => throw new InternalServerException("Email service returned an error")
      case BAD_GATEWAY => throw new BadGatewayException("Email service returned an upstream error")
      case _ => handleResponse(http, url)(response)
    }
}

trait EmailVerificationConnector {
  def checkVerifiedEmail(email : String)(implicit hc : HeaderCarrier) : Future[Boolean]
  def requestVerificationEmail(emailRequest : EmailVerificationRequest)(implicit hc : HeaderCarrier) : Future[Boolean]
}
