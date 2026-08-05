package com.nolimit.blog.presentation.dto

import cats.effect.IO
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

final case class ApiResponse[A](
  status: String,
  message: String,
  data: Option[A] = None
)

object ApiResponse {
  implicit def apiResponseEncoder[A: Encoder]: Encoder[ApiResponse[A]] = 
    deriveEncoder[ApiResponse[A]]

  implicit def apiResponseEntityEncoder[A: Encoder]: EntityEncoder[IO, ApiResponse[A]] =
    jsonEncoderOf[IO, ApiResponse[A]]

  def success[A](message: String, data: A): ApiResponse[A] =
    ApiResponse("success", message, Some(data))

  def success[A](message: String): ApiResponse[A] =
    ApiResponse("success", message, None)

  def error[A](message: String): ApiResponse[A] =
    ApiResponse("error", message, None)
}