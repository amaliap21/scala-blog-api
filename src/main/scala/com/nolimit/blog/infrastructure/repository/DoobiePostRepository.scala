package com.nolimit.blog.infrastructure.repository

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._
import com.nolimit.blog.domain.model.Post
import com.nolimit.blog.domain.repository.PostRepository
import java.util.UUID

class DoobiePostRepository(xa: Transactor[IO]) extends PostRepository[IO] {

  override def create(post: Post): IO[Post] = {
    sql"""
      INSERT INTO posts (id, content, created_at, updated_at, author_id)
      VALUES (${post.id}, ${post.content}, ${post.createdAt}, ${post.updatedAt}, ${post.authorId})
    """.update.run
      .transact(xa)
      .map(_ => post)
  }

  override def findAll(): IO[List[Post]] = {
    sql"""
      SELECT id, content, created_at, updated_at, author_id
      FROM posts
      ORDER BY created_at DESC
    """.query[Post].to[List].transact(xa)
  }

  override def findById(id: UUID): IO[Option[Post]] = {
    sql"""
      SELECT id, content, created_at, updated_at, author_id
      FROM posts
      WHERE id = $id
    """.query[Post].option.transact(xa)
  }

  override def update(post: Post): IO[Option[Post]] = {
    sql"""
      UPDATE posts
      SET content = ${post.content}, updated_at = ${post.updatedAt}
      WHERE id = ${post.id}
    """.update.run
      .transact(xa)
      .map(count => if (count > 0) Some(post) else None)
  }

  override def delete(id: UUID): IO[Boolean] = {
    sql"""
      DELETE FROM posts WHERE id = $id
    """.update.run
      .transact(xa)
      .map(_ > 0)
  }
}