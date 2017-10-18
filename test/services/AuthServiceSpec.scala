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

import common.BaseTestSpec
import connectors.AuthConnector
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._

import scala.concurrent.Future
import uk.gov.hmrc.http.{ HttpResponse, Upstream5xxResponse }

class AuthServiceSpec extends BaseTestSpec {

  val mockAuthConnector = mock[AuthConnector]
  val affinityGroup = "Organisation"

  val authResponse = Json.parse(s"""{"affinityGroup":"$affinityGroup"}""")

  val testService =  new AuthServiceImpl(mockAuthConnector)

  "getAffinityGroup" should {

    "return the users affinity group if both auth calls are successful" in {
      when(mockAuthConnector.getAuthority()(Matchers.any())).thenReturn(Future.successful(HttpResponse(OK, Some(authResponse))))
      val result = testService.getAffinityGroup()
      await(result) shouldBe Some(affinityGroup)
    }

    "return None when authConnector.getAuthority returns non-OK" in {
      when(mockAuthConnector.getAuthority()(Matchers.any())).thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))
      val result = testService.getAffinityGroup()
      await(result) shouldBe None
    }

    "return None when authConnector.getAuthority returns failed future" in {
      when(mockAuthConnector.getAuthority()(Matchers.any())).thenReturn(Future.failed(Upstream5xxResponse("",INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR)))
      val result = testService.getAffinityGroup()
      await(result) shouldBe None
    }

  }

}
