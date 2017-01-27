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

/**
  * Important:
  * This is a Test only Controller to reset the Enrolments. It is not used in production
  */
package testOnly.controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent}
import testOnly.connectors.{AuthenticatorConnector, GgStubsConnector}
import uk.gov.hmrc.play.frontend.controller.FrontendController

@Singleton
class ResetEnrolmentsController @Inject()(ggStubsConnector: GgStubsConnector,
                                          authenticatorConnector: AuthenticatorConnector) extends FrontendController {

  def resetEnrolments: Action[AnyContent] = Action.async { implicit request =>
    for {
      ggStubResponse <- ggStubsConnector.resetEnrolments()
      authRefreshed = authenticatorConnector.refreshProfile().isCompleted
    } yield ggStubResponse.status match {
      case OK => Ok("Successfully Reset Enrolments")
      case _ => BadRequest("Failed to Reset Enrolments")
    }
  }

}
