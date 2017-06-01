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

package services

import javax.inject.Singleton

import com.google.inject.Inject
import connectors.{EmailVerificationConnector, KeystoreConnector}
import models._
import play.api.Logger
import play.api.mvc.{AnyContent, Request}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class EmailVerificationServiceImpl @Inject()(keystoreConnector: KeystoreConnector) extends EmailVerificationService {


  val emailVerificationTemplate = "verifyEmailAddress"

  def verifyEmailAddress(address: String)
                        (implicit hc: HeaderCarrier, req: Request[AnyContent]): Future[Option[Boolean]] = {
    EmailVerificationConnector.checkVerifiedEmail(address) flatMap {
        case true => Future.successful(Some(true))
        case _ =>
          Future.successful(Some(false))
    }
  }

  def sendVerificationLink(address: String, returnUrl: String)
                          (implicit hc: HeaderCarrier, req: Request[AnyContent]): Future[Option[Boolean]] = {
    Logger.warn(s"EmailVerificationServiceImpl -- sendVerificationLink ::: $returnUrl :::::: $address")
    EmailVerificationConnector.requestVerificationEmail(generateEmailRequest(address, returnUrl)) flatMap {
        case verified => Future.successful(Some(verified))
        case _ => Future.successful(Some(false))
    }
  }


  private[services] def generateEmailRequest(address: String, returnUrl: String): EmailVerificationRequest = {
    EmailVerificationRequest(
      email = address,
      templateId = emailVerificationTemplate,
      templateParameters = Map(),
      linkExpiryDuration = "P1D",
      continueUrl = s"$returnUrl"
    )
  }

}

trait EmailVerificationService {
  def verifyEmailAddress(address: String)
                        (implicit hc: HeaderCarrier, req: Request[AnyContent]): Future[Option[Boolean]]
  def sendVerificationLink(address: String, returnUrl: String)
                          (implicit hc: HeaderCarrier, req: Request[AnyContent]): Future[Option[Boolean]]
}
