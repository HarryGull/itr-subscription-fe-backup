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

package common

import java.util.UUID

import auth.{AuthorisedActions, TAVCUser}
import connectors.{EmailVerificationConnector, KeystoreConnector, ValidateTokenConnector}
import forms.{ConfirmCorrespondAddressForm, ContactDetailsSubscriptionForm, ProvideCorrespondAddressForm}
import models.{AddressModel, CompanyRegistrationReviewDetailsModel, ContactDetailsSubscriptionModel, ProvideCorrespondAddressModel}
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.{Configuration, Environment}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.test.FakeRequest
import services._
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.frontend.auth.{AuthContext, AuthenticationProviderIds}
import uk.gov.hmrc.play.frontend.auth.connectors.domain
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{ConfidenceLevel, CredentialStrength}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.UnitSpec
import uk.gov.hmrc.time.DateTimeUtils
import utils.{CountriesHelper, Validation}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import uk.gov.hmrc.http.{ HeaderCarrier, SessionKeys }
import uk.gov.hmrc.http.logging.SessionId

trait BaseTestSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfterEach with I18nSupport with MockitoSugar {

  override def beforeEach() {
    reset(mockKeystoreConnector)
    reset(mockSessionCache)
    reset(mockAuthService)
    reset(mockRegisteredBusinessCustomerService)
    reset(mockHttp)
    reset(mockValidateTokenService)
    reset(mockValidateTokenConnector)
    reset(mockEmailVerificationService)
  }

  val sessionId = UUID.randomUUID.toString
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))
  implicit val ec = global.prepare()
  val fakeRequest = FakeRequest()

  val mockSessionCache = mock[SessionCache]
  val mockHttp = mock[WSHttp]
  val mockRegisteredBusinessCustomerService = mock[RegisteredBusinessCustomerService]
  val mockKeystoreConnector = mock[KeystoreConnector]
  val mockAuthService = mock[AuthService]
  val mockValidateTokenService= mock[ValidateTokenService]
  val mockValidateTokenConnector = mock[ValidateTokenConnector]
  val mockEmailVerificationService = mock[EmailVerificationService]
  val mockEmailVerificationConnector = mock[EmailVerificationConnector]

  val provideModel = ProvideCorrespondAddressModel("test1","test2",Some("test3"),Some("test4"),Some("test5"),"test6")
  val contactDetailsModel = ContactDetailsSubscriptionModel("test1","test2",Some("test3"),Some("test4"),"test5@test.com")

  val tokenId = "123456789"

  val validation = new Validation(messagesApi)
  val countriesHelper = new CountriesHelper(Environment.simple())
  val confirmCorrespondAddressForm = new ConfirmCorrespondAddressForm
  val contactDetailsSubscriptionForm = new ContactDetailsSubscriptionForm(validation)
  val provideCorrespondAddressForm = new ProvideCorrespondAddressForm(validation)

  val mockUsername = "mockuser"
  val mockUserId = "/auth/oid/" + mockUsername

  val loggedInAt = Some(new DateTime(2015, 11, 22, 11, 33, 15, 234, DateTimeZone.UTC))
  val previouslyLoggedInAt = Some(new DateTime(2014, 8, 3, 9, 25, 44, 342, DateTimeZone.UTC))

  val sufficientAuthority = domain.Authority(
    uri = "/auth/oid/1234567890",
    accounts = domain.Accounts(),
    loggedInAt = loggedInAt,
    previouslyLoggedInAt = previouslyLoggedInAt,
    credentialStrength = CredentialStrength.Weak,
    confidenceLevel = ConfidenceLevel.L50,
    userDetailsLink = None,
    enrolments = None,
    ids = None,
    legacyOid = "0000000000"
  )

  val allowedAuthContext = AuthContext(
    authority = sufficientAuthority,
    governmentGatewayToken = Some("token"),
    nameFromSession = Some("Test Name")
  )

  val mockAuthorisedActions = new AuthorisedActions {
    override def async(action: (TAVCUser) => (Request[AnyContent]) => Future[Result]) = Action.async {
      implicit request => action(TAVCUser(allowedAuthContext))(request)
    }
  }

  val validModel = new CompanyRegistrationReviewDetailsModel("Test Name",
    Some("Corporate Body"),
    new AddressModel("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), Some("AA1 1AA"), "GB"),
    "1234567890",
    "XE0001234567890",
    false,
    false,
    Some("JARN1234567")
  )

  val validModelMin = new CompanyRegistrationReviewDetailsModel("Test Name",
    None,
    new AddressModel("Line 1", "Line 2", None, None, None, "GB"),
    "1234567890",
    "XE0001234567890",
    false,
    false,
    None
  )

  def messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]
  def configuration: Configuration = app.configuration

  def authenticatedFakeRequest(provider: String = AuthenticationProviderIds.GovernmentGatewayId,
                               userId: String = mockUserId): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest().withSession(
      SessionKeys.userId -> userId,
      SessionKeys.sessionId -> s"session-${UUID.randomUUID()}",
      SessionKeys.lastRequestTimestamp -> DateTimeUtils.now.getMillis.toString,
      SessionKeys.token -> "ANYOLDTOKEN",
      SessionKeys.authProvider -> provider
    )

  def showWithSessionAndAuth(action: Action[AnyContent])(test: Future[Result] => Any) {
    val result = action.apply(authenticatedFakeRequest())
    test(result)
  }

  def showWithoutSession(action: Action[AnyContent])(test: Future[Result] => Any){
    val result = action.apply(fakeRequest)
    test(result)
  }

  def submitWithSessionAndAuth(action: Action[AnyContent],input: (String, String)*)(test: Future[Result] => Any) {
    val result = action.apply(authenticatedFakeRequest().withFormUrlEncodedBody(input: _*))
    test(result)
  }

  def withRegDetails(): Unit = {
    when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any())).thenReturn(Future.successful(Some(validModel)))
    when(mockAuthService.getAffinityGroup()(Matchers.any(), Matchers.any())).thenReturn(Future.successful(Some("Organisation")))
  }

  def noRegDetails(): Unit = {
    when(mockRegisteredBusinessCustomerService.getReviewBusinessCustomerDetails(Matchers.any(), Matchers.any())).thenReturn(Future.successful(None))
    when(mockAuthService.getAffinityGroup()(Matchers.any(), Matchers.any())).thenReturn(Future.successful(Some("Organisation")))
  }

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


  def withValidToken(): Unit = {
    when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.tokenId))(Matchers.any(),Matchers.any())).
      thenReturn(Future.successful(Some(tokenId)))
    when(mockValidateTokenService.validateTemporaryToken(Matchers.any())(Matchers.any())).thenReturn(Future.successful(true))
  }
  def withInvalidToken(): Unit ={
    when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.tokenId))(Matchers.any(),Matchers.any())).
      thenReturn(Future.successful(Some(tokenId)))
    when(mockValidateTokenService.validateTemporaryToken(Matchers.any())(Matchers.any())).thenReturn(Future.successful(false))
  }

  def withoutToken(): Unit = {
    when(mockKeystoreConnector.fetchAndGetFormData[String](Matchers.eq(KeystoreKeys.tokenId))(Matchers.any(),Matchers.any())).
      thenReturn(Future.successful(None))
    when(mockValidateTokenService.validateTemporaryToken(Matchers.any())(Matchers.any())).thenReturn(Future.successful(false))
  }

}
