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

package common
object KeystoreKeys extends KeystoreKeys

trait KeystoreKeys {
  // form keys

  val companyDetails: String = "registrationInformation:companyDetails"
  val confirmContactAddress: String = "registrationInformation:confirmCorrespondAddress"
  val provideCorrespondAddress: String = "registrationInformation:provideCorrespondAddress"
  val contactDetailsSubscription: String = "registrationInformation:contactDetailsSubscription"

  // processing Keys

  // backlink keys
  val backLinkConfirmCorrespondAddress: String = "backLink:confirmCorrespondAddress"
  val backLinkProvideCorrespondAddress: String = "backLink:provideCorrespondAddress"
  val backLinkContactDetailsSubscription: String = "backLink:contactDetailsSubscription"

}
