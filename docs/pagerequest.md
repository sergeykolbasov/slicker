Pagination and sorting
======

Slicker provides pagination (limit and offset) and sorting with `io.slicker.core.PageRequest` class as
part of Repository interface.

### Pagination

Public method `findAll` and protected method `findAllBy` take `PageRequest`
as an additional optional argument equal to `PageRequest.ALL` by default.

`PageRequest` class has following signature:

```scala
case class PageRequest(page: Int, perPage: Int, sort: Fields = Fields.empty)
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