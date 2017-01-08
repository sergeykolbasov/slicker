Slicker [![Build Status](https://travis-ci.org/ImLiar/slicker.svg?branch=master)](https://travis-ci.org/ImLiar/slicker)
======
Using [Slick](http://slick.lightbend.com/) in repository manner.

Repository vs DAO
-----
DAO or _Data Access Object_ is a quick and nice solution to access and represent
database inside of your code. Close to the tables, persistence included. Sounds like a silver bullet.  
But what if inside of your business domain you want to abstract over data mapping and the way your
entity is built? May be you have multiple database sources and don't want to care managing sources on the 
business level.

Here comes the repository layer which allows to hide details of database access
and get rid of duplication of query code. 

You could read more about both patterns here:

- http://blog.sapiensworks.com/post/2012/11/01/Repository-vs-DAO.aspx
- https://thinkinginobjects.com/2012/08/26/dont-use-dao-use-repository/

Point of using Slicker
-----

Slick `TableQuery` in fact is an implementation of DAO staying close to SQL and
exposing SQL logic. Slicker doesn't reject this approach but hides usage
of Slick tables under the hood providing sufficient abstraction for simple CRUD operations but
still with possibility of doing manual work.

Example
------

First add following lines to your build.sbt:
```
libraryDependencies ++= Seq(
    "com.github.imliar" %% "slicker-core" % "0.1",
    "com.github.imliar" %% "slicker-postgres" % "0.1"
)
```

Currently only Scala 2.11 release is available because Slick wasn't released 
for 2.12 yet.

Then define table and entity that we're going to use:

```scala
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.{SimpleRecordTable, TableWithId}
import slick.lifted.ProvenShape

case class User(id: Option[Long], name: String)

class UserTable extends SimpleRecordTable[Long, User, Users] {

  override val tableQuery: TableQuery[Users] = TableQuery[Users]

}

class Users(tag: Tag) extends TableWithId[Long, User](tag, "users") {

  override def id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)

  def name: Rep[String] = column[String]("name")

  override def * : ProvenShape[User] = (id.?, name) <> (User.tupled, User.unapply)

}
```

`UserTable` will be used by Slicker `Repository` to convert business <> database entities and
operate with `TableQuery`. In this case business model is equal to database one, so it's enough
to use `SimpleRecordTable`.  
`TableWithId` defines `id` column with provided type so `Repository` could operate with entity id.

Now define repository interface as a next step. It's not required but just a good practice
to split intention and implementation.

```scala
import io.slicker.core.Repository

trait UsersRepository extends Repository[Long, User] {
  
  def findOneByName(name: String): ReadAction[Option[User]]

}
```

Except manually provided `findAllByName` method `Repository` interface also 
defines useful everyday [methods](https://github.com/ImLiar/slicker/blob/master/slicker-core/src/main/scala/io/slicker/core/Repository.scala).  
As a result you will get Slick `DBIOAction`. Running of action is up to user.

Finally define a repository implementation:

```scala
import io.slicker.postgres.PostgresDriver.api._
import io.slicker.postgres.PostgreSQLRepository

import scala.concurrent.ExecutionContext

class UsersRepositoryImpl extends PostgreSQLRepository(new UserTable) with UsersRepository {

  //just an example. you could provide your own EC
  override protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  override def findOneByName(name: String): ReadAction[Option[User]] = findOneBy(_.name === name)
  
}
```

`findOneBy` is one of methods that come with [PostgreSQLRepository](https://github.com/ImLiar/slicker/blob/master/slicker-postgres/src/main/scala/io/slicker/postgres/PostgreSQLRepository.scala)
and supposed to provide easy-to-use CRUD operations.

Limitations
------

- Only PostgreSQL implementation available. It'll be easy to support other DBs creating new subproject.