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

package config

import com.google.inject.Inject
import play.api.Configuration
import play.api.Play.{configuration, current}
import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val notAuthorisedRedirectUrl: String
  val ggSignInUrl: String
  val introductionUrl: String
  val businessCustomerUrl: String
  val subscriptionUrl: String
  val submissionUrl: String
  val submissionBackendUrl: String
  val contactFormServiceIdentifier: String
  val contactFrontendPartialBaseUrl: String
  val ggSignOutUrl: String
  val signOutPageUrl: String
  val authUrl: String
  val createAccountUrl: String
  val emailVerificationReturnUrlOne: String
  val sendVerificationEmailURL: String
  val checkVerifiedEmailURL: String
  val emailVerificationTemplate: String
}

class FrontendAppConfig @Inject()(configuration: Configuration) extends AppConfig with ServicesConfig {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  override lazy val analyticsToken = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost = loadConfig(s"google-analytics.host")
  override lazy val notAuthorisedRedirectUrl = configuration.getString("not-authorised-callback.url").getOrElse("")
  override lazy val ggSignInUrl = configuration.getString("government-gateway-sign-in.host").getOrElse("")
  override lazy val introductionUrl = configuration.getString("introduction.url").getOrElse("")
  override lazy val businessCustomerUrl = configuration.getString("business-customer.url").getOrElse("")
  override lazy val subscriptionUrl = baseUrl("investment-tax-relief-subscription")
  override lazy val submissionUrl = configuration.getString("submission.url").getOrElse("")
  override lazy val submissionBackendUrl = baseUrl("investment-tax-relief-submission")

  //Contact Frontend Config
  protected lazy val contactFrontendService = baseUrl("contact-frontend")
  protected lazy val contactHost = loadConfig("contact-frontend.host")
  override lazy val contactFormServiceIdentifier = "TAVC"
  override lazy val contactFrontendPartialBaseUrl = s"$contactFrontendService"
  override lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  override lazy val ggSignOutUrl = loadConfig("government-gateway-sign-out.host")
  override lazy val signOutPageUrl: String = loadConfig(s"sign-out-page.url")
  override lazy val authUrl = baseUrl("auth")
  override lazy val createAccountUrl = loadConfig("create-account.url")

  override lazy val emailVerificationReturnUrlOne = loadConfig(s"email.returnUrlOne")
  override lazy val sendVerificationEmailURL = loadConfig("email-vs.sendVerificationEmailURL")
  override lazy val checkVerifiedEmailURL = loadConfig("email-vs.checkVerifiedEmailURL")
  override lazy val emailVerificationTemplate = loadConfig("email.emailVerificationTemplate")
}
