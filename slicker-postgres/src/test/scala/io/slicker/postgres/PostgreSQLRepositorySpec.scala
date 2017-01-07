package io.slicker.postgres

import io.slicker.core.{PageRequest, SortDirection}
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.models.{User, UserTable, UsersRepository}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.duration._

class PostgreSQLRepositorySpec extends FlatSpec with Matchers with AwaitHelper with BeforeAndAfter with BeforeAndAfterAll {

  private val userTable = new UserTable
  private val userRepo = new UsersRepository
  private implicit val timeout = 10.seconds

  override def afterAll(): Unit = {
    closeConnection()
  }

  before {
    userTable.tableQuery.schema.create.run.await
  }

  after {
    userTable.tableQuery.schema.drop.run.await
  }

  it should "return no rows for empty table" in {
    userRepo.findAll().run.await shouldBe Seq.empty[User]
  }

  it should "return zero as count result for empty table" in {
    userRepo.countAll.run.await shouldBe 0
  }

  it should "save entity, return ID and find it by ID" in {
    val user = userRepo.save(User(None, "name")).run.await
    userRepo.findById(user.id.get).run.await.get shouldBe user
  }

  it should "save multiple entities, return IDs and find them by ids" in {
    val users = userRepo.save(Seq(User(None, "name"), User(None, "name2"))).run.await
    users.flatMap(_.id).flatMap(userRepo.findById(_).run.await) shouldBe users
  }

  it should "save multiple entities and find them in descending order" in {
    val users = userRepo.save(Seq(User(None, "name"), User(None, "name2"))).run.await
    val sort = Seq("name" -> SortDirection.Desc)
    userRepo.findAll(PageRequest(sort)).run.await shouldBe users.sortBy(_.name).reverse
  }

  it should "update saved entity" in {
    val user = userRepo.save(User(None, "name")).run.await
    val updatedUser = userRepo.save(user.copy(name = "name2")).run.await
    updatedUser shouldBe user.copy(name = "name2")
    updatedUser shouldBe userRepo.findById(user.id.get).run.await.get
  }

  it should "update only single entity" in {
    val user = userRepo.save(User(None, "name")).run.await
    val user2 = userRepo.save(User(None, "name2")).run.await
    val updatedUser = userRepo.save(user.copy(name = "name3")).run.await

    updatedUser shouldBe user.copy(name = "name3")
    updatedUser shouldBe userRepo.findById(user.id.get).run.await.get

    user2 shouldBe userRepo.findById(user2.id.get).run.await.get
  }

  it should "remove entity by id" in {
    val user = userRepo.save(User(None, "name")).run.await
    userRepo.removeById(user.id.get).run.await

    userRepo.findById(user.id.get).run.await shouldBe None
  }

  it should "remove entity" in {
    val user = userRepo.save(User(None, "name")).run.await
    userRepo.remove(user).run.await

    userRepo.findById(user.id.get).run.await shouldBe None
  }

  it should "remove only single entity" in {
    val user = userRepo.save(User(None, "name")).run.await
    val user2 = userRepo.save(User(None, "name2")).run.await
    userRepo.remove(user).run.await

    user2 shouldBe userRepo.findById(user2.id.get).run.await.get
  }

  it should "remove all entities" in {
    userRepo.save(User(None, "name")).run.await
    userRepo.removeAll().run.await

    userRepo.countAll.run.await shouldBe 0
  }

}
