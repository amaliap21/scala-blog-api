package com.nolimit.blog

import cats.effect.IO
import com.nolimit.blog.domain.model.{Post, User}
import com.nolimit.blog.domain.repository.{PostRepository, UserRepository}
import java.util.UUID
import scala.collection.mutable

class MockUserRepository extends UserRepository[IO] {
  private val users = mutable.Map[UUID, User]()

  override def create(user: User): IO[User] = IO {
    users.put(user.id, user)
    user
  }

  override def findByEmail(email: String): IO[Option[User]] = IO {
    users.values.find(_.email.equalsIgnoreCase(email))
  }

  override def findById(id: UUID): IO[Option[User]] = IO {
    users.get(id)
  }
}

class MockPostRepository extends PostRepository[IO] {
  private val posts = mutable.Map[UUID, Post]()

  override def create(post: Post): IO[Post] = IO {
    posts.put(post.id, post)
    post
  }

  override def findAll(): IO[List[Post]] = IO {
    posts.values.toList.sortBy(_.createdAt)(Ordering.by(_.toEpochMilli)).reverse
  }

  override def findById(id: UUID): IO[Option[Post]] = IO {
    posts.get(id)
  }

  override def update(post: Post): IO[Option[Post]] = IO {
    if (posts.contains(post.id)) {
      posts.put(post.id, post)
      Some(post)
    } else None
  }

  override def delete(id: UUID): IO[Boolean] = IO {
    posts.remove(id).isDefined
  }
}