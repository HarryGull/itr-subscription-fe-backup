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

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class EmailVerificationServiceImpl @Inject()(keystoreConnector: KeystoreConnector,
                                             emailVerificationConnector: EmailVerificationConnector) extends EmailVerificationService {

  def verifyEmailAddress(address: String)
                        (implicit hc: HeaderCarrier): Future[Option[Boolean]] = {
    emailVerificationConnector.checkVerifiedEmail(address) flatMap {
        case true => Future.successful(Some(true))
        case _ =>
          Future.successful(Some(false))
    }
  }

  def sendVerificationLink(address: String, returnUrl: String, template: String)
                          (implicit hc: HeaderCarrier): Future[Boolean] = {
    Logger.debug(s"EmailVerificationServiceImpl -- sendVerificationLink ::: $returnUrl :::::: $address")
    emailVerificationConnector.requestVerificationEmail(generateEmailRequest(address, returnUrl, template)) map {
      sent =>
        val verified = sent // if not sent the it's because the email address was already verified
        verified
    }
  }


  private[services] def generateEmailRequest(address: String, returnUrl: String, template: String): EmailVerificationRequest = {
    EmailVerificationRequest(
      email = address,
      templateId = template,
      templateParameters = Map(),
      linkExpiryDuration = "P1D",
      continueUrl = s"$returnUrl"
    )
  }

}

trait EmailVerificationService {
  def verifyEmailAddress(address: String)
                        (implicit hc: HeaderCarrier): Future[Option[Boolean]]
  def sendVerificationLink(address: String, returnUrl: String, template: String)
                          (implicit hc: HeaderCarrier): Future[Boolean]
}
