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

package auth

import common.KeystoreKeys
import play.api.mvc.{AnyContent, Request}
import play.api.mvc.Results._
import uk.gov.hmrc.play.frontend.auth._
import _root_.connectors.KeystoreConnector
import uk.gov.hmrc.play.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.{ExecutionContext, Future}

class SecondWhitelistPredicate(keystoreConnector: KeystoreConnector)(implicit ec: ExecutionContext) extends PageVisibilityPredicate {

  override def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] = {
    implicit val hc = HeaderCarrier.fromHeadersAndSession(request.headers, Some(request.session))
    request.getQueryString("p").fold(
      request.session.get(SessionKeys.otacToken).fold(
      keystoreConnector.fetchAndGetFormData[String](KeystoreKeys.otacToken).map {
        case Some(token) => PageBlocked(Future.successful(Redirect(request.uri, request.queryString + ("p" -> Seq(token)))))
        case _ => PageIsVisible
      }
      )(_ => Future.successful(PageIsVisible))
    )(_ => Future.successful(PageIsVisible))
  }

}
