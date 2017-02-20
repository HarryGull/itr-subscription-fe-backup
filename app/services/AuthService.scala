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

import javax.inject.{Inject, Singleton}

import connectors.AuthConnector
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthServiceImpl @Inject()(authConnector: AuthConnector) extends AuthService {

  def getAffinityGroup()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[String]] = {
    authConnector.getAuthority().map {
      response => response.status match {
        case OK => Some((response.json \ "affinityGroup").as[String])
        case _ => None
      }
    }.recover {
      case e: Exception => Logger.warn(s"[AuthConnector][getAffinityGroup] Exception occurred when calling auth: ${e.getMessage}")
        None
    }
  }
}

trait AuthService {

  def getAffinityGroup()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[String]]

}
