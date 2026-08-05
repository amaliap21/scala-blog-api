package com.nolimit.blog.domain.repository

import com.nolimit.blog.domain.model.Post
import java.util.UUID

trait PostRepository[F[_]] {
  def create(post: Post): F[Post]
  def findAll(): F[List[Post]]
  def findById(id: UUID): F[Option[Post]]
  def update(post: Post): F[Option[Post]]
  def delete(id: UUID): F[Boolean]
}