Slicker
======

Slicker allows to abstract over well-known [Slick](http://slick.lightbend.com/) library to decrease the
number of boilerplate using repository pattern and standart out-of-the-box
CRUD operations when dealing with SQL databases.

Quick start
======

First add following lines to your build.sbt:
```
libraryDependencies ++= Seq(
    "com.github.imliar" %% "slicker-core" % "0.3",
    "com.github.imliar" %% "slicker-postgres" % "0.3"
)
```

Then define table and entity that we're going to use:

```scala
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.{SimpleRecordTable, TableWithId}
import io.slicker.core.sort.Sort
import slick.lifted.ProvenShape

case class User(id: Option[Long], name: String, email: String)

class UserTable extends SimpleRecordTable[Long, User, Users] {

  override val tableQuery: TableQuery[Users] = TableQuery[Users]
  
  override val sort: Sort[Users] = Sort.auto[Users]

}

class Users(tag: Tag) extends TableWithId[Long, User](tag, "users") {

  override def id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def name: Rep[String] = column[String]("name")
  
  def email: Rep[String] = column[String]("email")

  override def * : ProvenShape[User] = (id.?, name, email) <> (User.tupled, User.unapply)

}
```

Now define repository interface as a next step. It's not required but just a good practice
to split intention and implementation.

```scala
import io.slicker.core.Repository
import io.slicker.PageRequest

trait UsersRepository extends Repository[Long, User] {
  
  def findAllByName(name: String, pageRequest: PageRequest): ReadAction[Seq[User]]
  
  def findOneByEmail(email: String): ReadAction[Option[User]]

}
```

Finally implement that interface

```scala
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.PostgreSQLRepository

import scala.concurrent.ExecutionContext

class UsersRepositoryImpl extends PostgreSQLRepository(new UserTable) with UsersRepository {

  //just an example. you could provide your own EC
  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  override def findAllByName(name: String, pageRequest: PageRequest): ReadAction[Seq[User]] = findAllBy((_.name === name), pageRequest)
  
  override def findOneByEmail(email: String): ReadAction[Option[User]] = findOneBy(_.email === email)
  
}
```

Repository itself __returns DBIOAction__ so it's up to user to execute this action using Slick `Database`.
It's also possible to compose multiple actions together and execute them in a single transaction i.e.
More documentation about Slick could be found on [official website](http://slick.lightbend.com/doc/3.1.1/gettingstarted.html#querying).

More
======

* [Entity ID](entity.md)
* [Pagination and sorting](pagerequest.md)
* [Slick-pg Driver](slickpg.md)