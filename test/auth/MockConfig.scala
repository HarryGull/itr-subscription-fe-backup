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

import config.AppConfig

object MockConfig extends AppConfig {
  override val analyticsToken: String = ""
  override val analyticsHost: String = ""
  override val reportAProblemPartialUrl: String = ""
  override val reportAProblemNonJSUrl: String = ""
  override val notAuthorisedRedirectUrl: String = "/investment-tax-relief-subscription/not-authorised"
  override val ggSignInUrl: String = "/gg/sign-in"
  override val introductionUrl: String = "http://localhost:9637/investment-tax-relief-subscription/"
  override val businessCustomerUrl: String = "http://localhost:9923/business-customer/investment-tax-relief"
  override val subscriptionUrl: String = ""
  override val submissionUrl: String = "/investment-tax-relief/"
  override val contactFrontendService: String = "/contact"
  override val contactFormServiceIdentifier: String = ""
  override val ggSignOutUrl: String = "/gg/sign-out"
  override val signOutPageUrl: String = "/signout"
  override val authUrl: String = "/auth"
  override val createAccountUrl: String = "/create-account"
}
