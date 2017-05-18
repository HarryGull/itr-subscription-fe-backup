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
import common.KeystoreKeys
import connectors.ValidateTokenConnector
import play.api.Logger
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future


@Singleton
class ValidateTokenServiceImpl @Inject()(validateTokenConnector: ValidateTokenConnector) extends ValidateTokenService{

  def validateTemporaryToken(tokenId: Option[String])(implicit hc: HeaderCarrier): Future[Boolean] = {

    def hasValidToken(token: Option[String]): Future[Boolean] = {
        validateTokenConnector.validateToken(tokenId).map {
          case Some(validationResult) if validationResult => {
            true
          }
          case _ =>
            false
        }.recover {
          case _ => Logger.warn(s"[ValidateTokenService][validateTemporaryToken] - Call to validate token failed")
            false
        }
    }

    (for {
      validated <- hasValidToken(tokenId)
    } yield validated).recover {
      case _ => {
        Logger.warn(s"[TokenService][validateTemporaryToken] - Call to validate token failed")
      }
        false
    }
  }
}


trait ValidateTokenService {
  def validateTemporaryToken(tokenId: Option[String])(implicit hc: HeaderCarrier): Future[Boolean]
}
