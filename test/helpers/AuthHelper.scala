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

package helpers

import auth._
import config.AppConfig
import models.{AddressModel, CompanyRegistrationReviewDetailsModel}
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.stubbing.OngoingStubbing
import org.mockito.Mockito._
import play.api.libs.json.Json
import services.RegisteredBusinessCustomerService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

object AuthHelper extends MockitoSugar {

  val mockConfig: AppConfig = MockConfig
  val mockAuthConnector = MockAuthConnector
  val mockRegisteredBusinessCustomerService = mock[RegisteredBusinessCustomerService]

  val validJson = Json.parse(
    """{
      |"businessName":"Test Name",
      |"businessType":"Corporate Body",
      |"businessAddress":{
      |"line_1":"Line 1",
      |"line_2":"Line 2",
      |"line_3":"Line 3",
      |"line_4":"Line 4",
      |"postcode":"AA1 1AA",
      |"country":"GB"
      |},
      |"sapNumber":"1234567890",
      |"safeId":"XE0001234567890",
      |"isAGroup":false,
      |"directMatch":false,
      |"agentReferenceNumber":"JARN1234567"
      |}""".stripMargin)

  val validModel = new CompanyRegistrationReviewDetailsModel(
    "Test Name",
    Some("Corporate Body"),
    new AddressModel(
      "Line 1",
      "Line 2",
      Some("Line 3"),
      Some("Line 4"),
      Some("AA1 1AA"),
      "GB"
    ),
    "1234567890",
    "XE0001234567890",
    false,
    false,
    Some("JARN1234567")
  )

  val validModelMin = new CompanyRegistrationReviewDetailsModel(
    "Test Name",
    None,
    new AddressModel(
      "Line 1",
      "Line 2",
      None,
      None,
      None,
      "GB"
    ),
    "1234567890",
    "XE0001234567890",
    false,
    false,
    None
  )

  def withRegDetails(): Unit = {
    when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(Some(validModel)))
  }

  def noRegDetails(): Unit = {
    when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any[HeaderCarrier]())).thenReturn(Future.successful(None))
  }
}
