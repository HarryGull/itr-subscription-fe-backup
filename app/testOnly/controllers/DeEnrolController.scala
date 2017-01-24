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
  * This is a Test only Controller to de-enrol a user. It is not used in production
  */
package testOnly.controllers

import play.api.mvc.Action
import testOnly.connectors.{AuthenticatorConnector, DeEnrolmentConnector}
import uk.gov.hmrc.play.frontend.controller.FrontendController

trait DeEnrolController extends FrontendController {

  val deEnrolmentConnector: DeEnrolmentConnector
  val authenticatorConnector: AuthenticatorConnector

  val deEnrol = Action.async { implicit request =>
    for {
      ggStubResponse <- deEnrolmentConnector.deEnrol()
      authRefreshed = authenticatorConnector.refreshProfile()
    } yield ggStubResponse.status match {
      case OK => Ok("Successfully De-enrolled")
      case _ => BadRequest("Failed to De-enrol")
    }
  }
}

object DeEnrolController extends DeEnrolController {
  override lazy val deEnrolmentConnector = DeEnrolmentConnector
  override lazy val authenticatorConnector = AuthenticatorConnector
}
