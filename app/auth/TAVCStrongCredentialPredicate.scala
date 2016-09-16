/*
 * Copyright 2016 HM Revenue & Customs
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

//package auth
//
//import java.net.{URI, URLEncoder}
//
//import play.api.mvc.Results._
//import play.api.mvc.{AnyContent, Request}
//import uk.gov.hmrc.play.frontend.auth._
//import uk.gov.hmrc.play.frontend.auth.connectors.domain.CredentialStrength
//
//import scala.concurrent.Future
//
//class TAVCStrongCredentialPredicate(twoFactorUrl: String,postSignInRedirectUrl: String,
//                                    notAuthorisedRedirectUrl: String
//                                   ) extends PageVisibilityPredicate {
//
//  private val twoFactorURI: URI =
//    new URI(s"${twoFactorUrl}?" +
//      s"continue=${URLEncoder.encode(postSignInRedirectUrl, "UTF-8")}&" +
//      s"failure=${URLEncoder.encode(notAuthorisedRedirectUrl, "UTF-8")}")
//
//  override def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] =
//    Future.successful(authContext.user.credentialStrength match {
//      case CredentialStrength.Strong => PageIsVisible
//      //case _ => PageBlocked(needsTwofactorAuthentication)
//      case _ => PageIsVisible
//    })
//
//
//  private val needsTwofactorAuthentication= Future.successful(Redirect(twoFactorURI.toString))
//}