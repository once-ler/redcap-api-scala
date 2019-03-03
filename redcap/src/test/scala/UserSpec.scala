package com.eztier.redcap.test

import io.circe.{Decoder, Encoder}
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import io.circe.generic.extras.semiauto.{deriveDecoder, deriveEncoder}

// https://jsonplaceholder.typicode.com/users
case class Geo
(
  lat: String,
  lng: String
)

case class Address
(
  street: String,
  suite: String,
  city: String,
  zipcode: String,
  geo: Geo
)

case class Company
(
  name: String,
  catchPhrase: String,
  bs: String
)

case class User
(
  id: Int,
  name: String,
  username: String,
  email: String,
  address: Address,
  phone: String,
  website: String,
  company: Company
)

case class UserSlim
(
  id: Int,
  username: String,
  email: String
)


object UserImplicits {
  val renameKeys = (name: String) => name.charAt(0).toLower.toString + name.substring(1)

  implicit val customConfig: Configuration = Configuration.default.copy(transformMemberNames = renameKeys)

  implicit val userEncoder: Encoder[User] = deriveEncoder
  implicit val userDecoder: Decoder[User] = deriveDecoder

  implicit val userSlimEncoder: Encoder[UserSlim] = deriveEncoder
  implicit val userSlimDecoder: Decoder[UserSlim] = deriveDecoder
}
