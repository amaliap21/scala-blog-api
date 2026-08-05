package com.nolimit.blog.presentation.dto

import com.nolimit.blog.domain.model.User
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import java.util.UUID

final case class UserResponse(
  id: UUID,
  name: String,
  email: String
)

object UserResponse {
  implicit val userResponseEncoder: Encoder[UserResponse] = deriveEncoder[UserResponse]

  def fromUser(user: User): UserResponse =
    UserResponse(user.id, user.name, user.email)
}
