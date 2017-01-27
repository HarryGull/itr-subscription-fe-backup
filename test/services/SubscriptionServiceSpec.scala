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
import connectors.SubscriptionConnector
import models.etmp.{IntermediateCorrespondenceDetailsModel, IntermediateSubscriptionTypeModel, SubscriptionTypeModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SubscriptionServiceSpec extends BaseTestSpec {

  val mockSubscriptionConnector = mock[SubscriptionConnector]
  val subscriptionModel = Json.parse(
    Json.toJson(
      IntermediateSubscriptionTypeModel(IntermediateCorrespondenceDetailsModel(provideModel,contactDetailsModel))
    ).toString()
  ).as[SubscriptionTypeModel]

  val testService = new SubscriptionServiceImpl(mockSubscriptionConnector, mockKeystoreConnector, mockRegisteredBusinessCustomerService)

  "subscribe" when {

    "All details can be retrieved from keystore" should {

      lazy val result = testService.subscribe

      "Make a successful request to the subscription backend" in {
        allDetails()
        when(mockSubscriptionConnector.subscribe(Matchers.eq(subscriptionModel),Matchers.eq(validModel.safeId),
          Matchers.eq(validModel.businessAddress.postcode.get.replace(" ","")))(Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(OK)))
        await(result).status shouldBe OK
      }
    }

    "Not all details can be retrieved from keystore" should {

      lazy val result = testService.subscribe

      "Make no request to the subscription backend" in {
        notAllDetails()
        await(result).status shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "No details can be retrieved from keystore" should {

      lazy val result = testService.subscribe

      "Make no request to the subscription backend" in {
        noDetails()
        await(result).status shouldBe INTERNAL_SERVER_ERROR
      }
    }

  }

}
