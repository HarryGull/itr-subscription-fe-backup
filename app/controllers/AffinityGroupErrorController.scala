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

package controllers

import javax.inject.Inject

import common.KeystoreKeys
import config.AppConfig
import connectors.KeystoreConnector
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.warnings.AffinityGroupError

class AffinityGroupErrorController @Inject()(keystoreConnector: KeystoreConnector,
                                             val messagesApi: MessagesApi,
                                             implicit val applicationConfig: AppConfig) extends FrontendController with I18nSupport with AppName {

  def show() : Action[AnyContent] = Action.async { implicit request =>
    def signInURL(params : String = "") =
      s"${applicationConfig.ggSignInUrl}?continue=${applicationConfig.introductionUrl}$params&origin=$appName&accountType=organisation"
    def createAccountURL(params : String = "") =
      s"${applicationConfig.createAccountUrl}?continue=${applicationConfig.introductionUrl}$params&origin=$appName&accountType=organisation"
    keystoreConnector.fetchAndGetFormData[String](KeystoreKeys.otacToken).map {
      case Some(token) => {
        val queryParams = s"?p=$token"
        Ok(AffinityGroupError(signInURL(queryParams), createAccountURL(queryParams)))
      }
      case None => Ok(AffinityGroupError(signInURL(), createAccountURL()))
    }.recover {
      case e : Exception => Logger.warn(s"[AffinityGroupErrorController][show] Error contacting keystore: ${e.getMessage}")
        Ok(AffinityGroupError(signInURL(), createAccountURL()))
    }
  }

}
