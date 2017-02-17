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

import com.google.inject.Inject
import models.ProvideCorrespondAddressModel
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}

class Validation @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  val EmailThresholdLength = 132

  def mandatoryAddressLineCheck: Mapping[String] = {
    val validAddressLine = """[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,34}""".r
    val addresssLineCheckConstraint: Constraint[String] =
      Constraint("constraints.mandatoryAddressLine")({
        text =>
          val error = text match {
            case validAddressLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.mandatoryaddresssline")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text.verifying(addresssLineCheckConstraint)
  }

  def optionalAddressLineCheck: Mapping[String] = {
    val validAddressLine = """^$|[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,34}""".r
    val addresssLineCheckConstraint: Constraint[String] =
      Constraint("constraints.optionalAddressLine")({
        text =>
          val error = text match {
            case validAddressLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.optionaladdresssline")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(addresssLineCheckConstraint)
  }

  def addressLineFourCheck: Mapping[String] = {
    val validAddressLine = """^$|[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,34}""".r
    val addressLineFourCheckConstraint: Constraint[String] =
      Constraint("constraints.addressLineFour")({
        text =>
          val error = text match {
            case validAddressLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.linefouraddresssline")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(addressLineFourCheckConstraint)
  }

  def postcodeCheck: Mapping[String] = {
    val validPostcodeLine = "^[A-Z]{1,2}[0-9][0-9A-Z]? [0-9][A-Z]{2}$".r
    val postcodeCheckConstraint: Constraint[String] =
      Constraint("constraints.postcode")({
        text =>
          val error = text.toUpperCase match {
            case validPostcodeLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.postcode")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(postcodeCheckConstraint)
  }

  def countryCodeCheck: Mapping[String] = {
    val countryCode = """[A-Z]{2}""".r
    val countryCodeCheckConstraint: Constraint[String] =
      Constraint("constraints.countryCode")({
        text =>
          val error = text match {
            case countryCode() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.countryCode")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(countryCodeCheckConstraint)
  }

  def postcodeCountryCheckConstraint: Constraint[ProvideCorrespondAddressModel] = {
    Constraint("constraints.postcodeCountryCheck")({
      addressForm: ProvideCorrespondAddressModel =>
        if (addressForm.countryCode == "GB" && addressForm.postcode.fold(true)( _.isEmpty)) {
          Invalid(Seq(ValidationError(Messages("validation.error.countrypostcode"))))
        } else {
          Valid
        }
    })
  }

  def emailCheck(maxLength:Option[Int] = Some(EmailThresholdLength)): Mapping[String] = {
    val validEmailLine = """^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$""".r
    val emailCheckConstraint: Constraint[String] =
      Constraint("constraints.email")({
        text =>
          val error = text match {
            case validEmailLine() if text.length <= EmailThresholdLength => Nil
            case _ => Seq(ValidationError(Messages("validation.error.email")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(emailCheckConstraint)
  }

  def telephoneNumberCheck: Mapping[String] = {
    val validTelephoneNumberLine = """^[A-Z0-9 )/(*#-]{1,24}$""".r
    val telephoneNumberCheckConstraint: Constraint[String] =
      Constraint("constraints.telephoneNumber")({
        text =>
          val error = text match {
            case validTelephoneNumberLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.telephoneNumber")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(telephoneNumberCheckConstraint)
  }

  def optionalTelephoneNumberCheck: Mapping[String] = {
    val validTelephoneNumberLine = """^[A-Z0-9 )/(*#-]{1,24}$""".r
    val telephoneNumberCheckConstraint: Constraint[String] =
      Constraint("constraints.telephoneNumber")({
        text =>
          val error = text match {
            case validTelephoneNumberLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.telephoneNumber")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(telephoneNumberCheckConstraint)
  }

}
