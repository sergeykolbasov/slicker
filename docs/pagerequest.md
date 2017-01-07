Pagination and sorting
======

Slicker provides pagination (limit and offset) and sorting with `io.slicker.core.PageRequest` class as
part of Repository interface.

## Pagination

Public method `findAll` and protected method `findAllBy` take `PageRequest`
as an additional optional argument equal to `PageRequest.ALL` by default.

`PageRequest` class has following signature:

```scala
case class PageRequest(page: Int, perPage: Int, sort: Seq[(String, SortDirection)] = Seq.empty)
```

So it's sufficient to pass current page (starting from __1__) and number of items per page.
In addition there are additional values in companion object:

```scala
object PageRequest {

  /**
    * Requesting all pages
    */
  val ALL = new PageRequest(1, Int.MaxValue)

  /**
    * Requesting first page
    */
  val FIRSTPAGE = new PageRequest(1, 10)

} 
```

## Sorting

Beside pagination support `PageRequest` also provides sorting ability
using `sort` argument of type `Seq[(String, SortDirection)]`, where
sort direction enum could be `Asc` or `Desc`.

`RecordTable` defines `def sort: Sort` method that should return `Sort` object
for current table. By default it's always empty, so sorting wouldn't work without
overriding this method. This behaviour is based on assumption that it could be dangerous
to provide full sorting to user without indices on corresponding SQL table because 
of possible performance issues.

In case if there is a need in sorting using `PageRequest`, first it's required to
override this method in descendant class:

```scala
class UserTable extends SimpleRecordTable[Long, User, Users] {

  override val tableQuery: TableQuery[Users] = TableQuery[Users]

  override def sort: Sort[Users] = Sort.auto[Users]

}

class Users(tag: Tag) extends TableWithId[Long, User](tag, "users") {

  override def id: Rep[Long] = column[Long]("user_id", O.AutoInc, O.PrimaryKey)

  def name: Rep[String] = column[String]("user_name")

  override def * : ProvenShape[User] = (id.?, name) <> (User.tupled, User.unapply)

}
```

### Autosorting

`Sort.auto[Users]` generates `Sort` object using macros. It builds following pattern matching:
```
field match {
  case "id" => table.id
  case "name" => table.name
}
```
So inside of `sort` collection of `PageRequest` should be a key with "id" or "name".
Basically, every "key" should be a __name__ of method of column definition in `Table`. It's not a 
database column name, but method name of `Users` class. It's fair only for auto/semiauto cases.
Macros builds cases for __all__ columns.

### Semiauto sorting

```scala
override def sort: Sort[Users] = Sort.semiauto[Users](users => Seq(users.id))
```

`Sort.semiauto` generates `Sort` object using macros and list of provided columns. 
But in this case it builds cases only for columns in returning list.

### Manual sorting

```scala
override def sort: Sort[Users] = Sort.manual[Users](users => {
  case "user_id" => users.id
  case "user_name" => users.name
})
```

This time no macros involved and it's also possible to define own keys for sorting, but requires
more code to write.

More
======

* [Quick start](index.md)
* [Entity ID](entity.md)
* [Slick-pg Driver](slickpg.md)