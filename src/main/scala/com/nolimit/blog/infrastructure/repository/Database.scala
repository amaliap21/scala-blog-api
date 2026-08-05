package com.nolimit.blog.infrastructure.repository

import cats.effect.{IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts

object Database {
  def makeTransactor: Resource[IO, HikariTransactor[IO]] = {
    val host = Option(System.getenv("DB_HOST")).getOrElse("localhost")
    val port = Option(System.getenv("DB_PORT")).getOrElse("5432")
    val dbName = Option(System.getenv("DB_NAME")).getOrElse("blog_db")
    val user = Option(System.getenv("DB_USER")).getOrElse("postgres")
    val pass = Option(System.getenv("DB_PASSWORD")).getOrElse("postgrespassword")

    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      xa <- HikariTransactor.newHikariTransactor[IO](
        "org.postgresql.Driver",
        s"jdbc:postgresql://$host:$port/$dbName",
        user,
        pass,
        ce
      )
    } yield xa
  }
}