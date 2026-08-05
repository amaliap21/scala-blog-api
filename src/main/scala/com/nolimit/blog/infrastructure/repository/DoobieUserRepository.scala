package com.nolimit.blog.infrastructure.repository

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import com.nolimit.blog.domain.model.User
import com.nolimit.blog.domain.repository.UserRepository
import java.util.UUID

class DoobieUserRepository(xa: Transactor[IO]) extends UserRepository[IO] {

  override def create(user: User): IO[User] = {
    sql"""
      INSERT INTO users (id, name, email, password)
      VALUES (${user.id}, ${user.name}, ${user.email}, ${user.password})
    """.update.run
      .transact(xa)
      .map(_ => user)
  }

  override def findByEmail(email: String): IO[Option[User]] = {
    sql"""
      SELECT id, name, email, password
      FROM users
      WHERE email = $email
    """.query[User].option.transact(xa)
  }

  override def findById(id: UUID): IO[Option[User]] = {
    sql"""
      SELECT id, name, email, password
      FROM users
      WHERE id = $id
    """.query[User].option.transact(xa)
  }
}