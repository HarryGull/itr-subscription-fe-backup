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

package utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import common.Constants
import models.ProvideCorrespondAddressModel
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data.Mapping
import play.api.data.validation._
import play.api.i18n.Messages

import scala.util.{Failure, Success, Try}

object Validation {

  def mandatoryAddressLineCheck: Mapping[String] = {
    val validAddressLine = """[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,26}""".r
    val addresssLineCheckConstraint: Constraint[String] =
      Constraint("contraints.mandatoryAddressLine")({
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
    val validAddressLine = """^$|[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,26}""".r
    val addresssLineCheckConstraint: Constraint[String] =
      Constraint("contraints.optionalAddressLine")({
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
    val validAddressLine = """^$|[a-zA-Z0-9,.\(\)/&'"\-]{1}[a-zA-Z0-9, .\(\)/&'"\-]{0,17}""".r
    val addressLineFourCheckConstraint: Constraint[String] =
      Constraint("contraints.addressLineFour")({
        text =>
          val error = text match {
            case validAddressLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.linefouraddresssline")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(addressLineFourCheckConstraint)
  }

  def optionalPostcodeCheck: Mapping[String] = {
    val validPostcodeLine = "^$|[A-Z]{1,2}[0-9][0-9A-Z]? [0-9][A-Z]{2}".r
    val postcodeCheckConstraint: Constraint[String] =
      Constraint("contraints.postcode")({
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
    val validEmailLine = """[A-Z]{2}""".r
    val countryCodeCheckConstraint: Constraint[String] =
      Constraint("contraints.countryCode")({
        text =>
          val error = text match {
            case validEmailLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.countryCode")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(countryCodeCheckConstraint)
  }

  def emailCheck: Mapping[String] = {
    val validEmailLine = """[A-Za-z0-9\-​_.]{1,64}@[A-Za-z0-9\-_​.]{1,64}""".r
    val emailCheckConstraint: Constraint[String] =
      Constraint("contraints.email")({
        text =>
          val error = text match {
            case validEmailLine() => Nil
            case _ => Seq(ValidationError(Messages("validation.error.email")))
          }
          if (error.isEmpty) Valid else Invalid(error)
      })
    text().verifying(emailCheckConstraint)
  }

  def telephoneNumberCheck: Mapping[String] = {
    val validTelephoneNumberLine = """^[0-9\(\)\+ ]{0,23}\S$""".r
    val telephoneNumberCheckConstraint: Constraint[String] =
      Constraint("contraints.telephoneNumber")({
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
    val validTelephoneNumberLine = """^[0-9\(\)\+ ]{0,23}\S$""".r
    val telephoneNumberCheckConstraint: Constraint[String] =
      Constraint("contraints.telephoneNumber")({
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
