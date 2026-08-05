package com.nolimit.blog.domain.repository

import com.nolimit.blog.domain.model.User
import java.util.UUID

trait UserRepository[F[_]] {
  def create(user: User): F[User]
  def findByEmail(email: String): F[Option[User]]
  def findById(id: UUID): F[Option[User]]
}