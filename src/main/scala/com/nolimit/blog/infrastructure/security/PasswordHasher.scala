package com.nolimit.blog.infrastructure.security

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
  def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }

  def checkPassword(plainPassword: String, hashedPassword: String): Boolean = {
    BCrypt.checkpw(plainPassword, hashedPassword)
  }
}