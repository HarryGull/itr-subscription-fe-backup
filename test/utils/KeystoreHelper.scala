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

package utils

import common.KeystoreKeys
import connectors.KeystoreConnector
import models.{ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import org.mockito.Matchers
import org.mockito.Mockito._
import scala.concurrent.Future

trait KeystoreHelper extends AuthHelper {

  lazy val mockKeystoreConnector = mock[KeystoreConnector]
  val provideModel = ProvideCorrespondAddressModel("test1","test2",Some("test3"),Some("test4"),Some("test5"),"test6")
  val contactDetailsModel = ContactDetailsSubscriptionModel("test1","test2",Some("test3"),Some("test4"),"test5")

  def allDetails(): Unit = {
    withRegDetails()
    when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel]
      (Matchers.contains(KeystoreKeys.provideCorrespondAddress))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(Some(provideModel)))
    when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel]
      (Matchers.contains(KeystoreKeys.contactDetailsSubscription))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(Some(contactDetailsModel)))
  }

  def notAllDetails(): Unit = {
    withRegDetails()
    when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel]
      (Matchers.contains(KeystoreKeys.provideCorrespondAddress))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(Some(provideModel)))
    when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel]
      (Matchers.contains(KeystoreKeys.contactDetailsSubscription))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(None))
  }

  def noDetails(): Unit = {
    noRegDetails()
    when(mockKeystoreConnector.fetchAndGetFormData[ProvideCorrespondAddressModel]
      (Matchers.contains(KeystoreKeys.provideCorrespondAddress))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(Some(provideModel)))
    when(mockKeystoreConnector.fetchAndGetFormData[ContactDetailsSubscriptionModel]
      (Matchers.contains(KeystoreKeys.contactDetailsSubscription))(Matchers.any(),Matchers.any()))
      .thenReturn(Future.successful(None))
  }

}
