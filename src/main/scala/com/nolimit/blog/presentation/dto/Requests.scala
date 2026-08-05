package com.nolimit.blog.presentation.dto

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class RegisterRequest(name: String, email: String, password: String)
case class LoginRequest(email: String, password: String)
case class PostRequest(content: String)

object Requests {
  implicit val registerDecoder: Decoder[RegisterRequest] = deriveDecoder[RegisterRequest]
  implicit val loginDecoder: Decoder[LoginRequest] = deriveDecoder[LoginRequest]
  implicit val postDecoder: Decoder[PostRequest] = deriveDecoder[PostRequest]
}