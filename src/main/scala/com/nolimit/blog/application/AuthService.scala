package com.nolimit.blog.application

import cats.effect.IO
import com.nolimit.blog.domain.model.User
import com.nolimit.blog.domain.repository.UserRepository
import com.nolimit.blog.infrastructure.security.{JwtHelper, PasswordHasher}
import com.nolimit.blog.presentation.dto.{LoginRequest, RegisterRequest}
import java.util.UUID

class AuthService(userRepo: UserRepository[IO]) {

  // Regex sederhana untuk validasi email
  private val emailRegex = """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r

  def register(req: RegisterRequest): IO[Either[String, User]] = {
    if (req.name.trim.isEmpty || req.email.trim.isEmpty || req.password.trim.isEmpty) {
      IO.pure(Left("Nama, email, dan password tidak boleh kosong"))
    } else if (emailRegex.findFirstIn(req.email).isEmpty) {
      IO.pure(Left("Format email tidak valid"))
    } else {
      userRepo.findByEmail(req.email).flatMap {
        case Some(_) => IO.pure(Left("Email sudah terdaftar"))
        case None =>
          val newUser = User(
            id = UUID.randomUUID(),
            name = req.name.trim,
            email = req.email.trim.toLowerCase,
            password = PasswordHasher.hashPassword(req.password)
          )
          userRepo.create(newUser).map(Right(_))
      }
    }
  }

  def login(req: LoginRequest): IO[Either[String, String]] = {
    if (req.email.trim.isEmpty || req.password.trim.isEmpty) {
      IO.pure(Left("Email dan password tidak boleh kosong"))
    } else {
      userRepo.findByEmail(req.email.trim.toLowerCase).map {
        case Some(user) if PasswordHasher.checkPassword(req.password, user.password) =>
          val token = JwtHelper.generateToken(user.id)
          Right(token)
        case _ =>
          Left("Email atau password salah")
      }
    }
  }
}