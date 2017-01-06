Entity
======

To provide methods such as `findById`, `save`, `remove` and so on, Slicker
has to determine `id` field of model that used in repository.


There is a type-class `io.slick.core.Entity` that should return
for some type `E` its `id` field value of type `Option[Id]`.

```scala
trait Entity[E, Id] {
  def id(e: E): Option[Id]
}
```

Basically, for each model with field named `id` of type `Option[A]` Slicker is able
to derive this type class automatically using Shapeless library.
So there is no need to inherit some trait or use annotations.

But in case if there is no such optional field or `id` field has another name, it's
possible to implement `Entity` type-class manually with companion object and implicit value:

```scala

case class Catalog(cid: Long, name: String)

object Catalog {

    implicit val entity: Entity[Catalog, Long] = {
        new Entity[Catalog, Long] {
          def id(e: Catalog): Option[Long] = Some(catalog.cid) 
        }
    }

}
```

Compiler will automatically resolve this `Entity` instance to use with repository.

More
======

* [Quick start](index.md)
* [Pagination and sorting](pagerequest.md)
* [Slick-PG Driver](slickpg.md)