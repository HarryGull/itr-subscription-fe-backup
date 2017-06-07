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
      Logger.info(s"[EmailVerificationConnector] [checkVerifiedEmail] request to check verified email returned a $status - email not found / not verified")
      false
    }
    http.GET[HttpResponse](s"$checkVerifiedEmailURL/$email") map {
      _.status match {
        case OK => {
          print(" IN THE checkVerifiedEmailURL CASE OK ======================== :::: ")
          true
        }
        case _ => {
          print(" IN THE checkVerifiedEmailURL CASE ELSE ALL FALSE ======================== :::: ")
          false
        }
      }
    }recover {
      case ex: NotFoundException => errorMsg("404")
      case ex: InternalServerException => errorMsg("500")
      case ex: BadGatewayException => errorMsg("502")
    }
  }

  def requestVerificationEmail(emailRequest : EmailVerificationRequest)(implicit hc : HeaderCarrier) : Future[Boolean] = {
    def errorMsg(status: String, ex: HttpException) = {
      Logger.error(s"[EmailVerificationConnector] [requestVerificationEmail] request to send verification email returned a $status - email not sent - reason = ${ex.getMessage}")
      throw new EmailErrorResponse(status)
    }

    http.POST[EmailVerificationRequest, HttpResponse](s"$sendVerificationEmailURL", emailRequest)map { r =>
      r.status match {
        case CREATED => {
          Logger.debug("[EmailVerificationConnector] [requestVerificationEmail] request to verification service successful")
          true
        }
        case CONFLICT => {
          Logger.warn("[EmailVerificationConnector] [requestVerificationEmail] request to send verification email returned a 409 - email already verified")
          false
        }
      }
    }recover {
      case ex: BadRequestException => errorMsg("400", ex)
      case ex: NotFoundException => errorMsg("404", ex)
      case ex: InternalServerException => errorMsg("500", ex)
      case ex: BadGatewayException => errorMsg("502", ex)
    }
  }

  def customRead(http: String, url: String, response: HttpResponse) =
    response.status match {
      case 400 => {
        print(s" IN THE customRead CATCH 400 ======================== :::: ${response.status}")
        throw new BadRequestException("Provided incorrect data to Email Verification")
      }
      case 404 => {
        print(" IN THE customRead CATCH 404 ======================== :::: ")
        throw new NotFoundException("Email not found")
      }
      case 409 => {
        print(s" IN THE customRead CATCH 409 ======================== :::: ${response.status}")
        response
      }
      case 500 => {
        print(s" IN THE customRead CATCH 500 ======================== ::::  ${response.status} ")
        throw new InternalServerException("Email service returned an error")
      }
      case 502 => {
        print(s" IN THE customRead CATCH 502 ======================== :::: ${response.status} ")
        throw new BadGatewayException("Email service returned an upstream error")
      }
      case _ => {
        print(s" IN THE customRead CATCH ELSE ALLL ======================== :::: ${response.status}")
        handleResponse(http, url)(response)
      }
    }
}

trait EmailVerificationConnector {
  def checkVerifiedEmail(email : String)(implicit hc : HeaderCarrier) : Future[Boolean]
  def requestVerificationEmail(emailRequest : EmailVerificationRequest)(implicit hc : HeaderCarrier) : Future[Boolean]
}
