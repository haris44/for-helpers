# For-helpers

#### Using Cats EitherT Monad to make Scala for/yield Great Again ! 

--

Add [Cats](https://mvnrepository.com/artifact/org.typelevel/cats-core_2.11)
 on your dependencies 

Copy and import forHelpers object (It uses implicits) 

```scala
import cats.implicits._
import helpers.forHelpers._
```
Use for/yield with : 

* Option[A], 
* Future[Option[A]], 
* Future[Either[A,B]] 
* JsResult[A]


And play with for comprehension !

```scala
def insert = Action.async(bodyParser.json) {
    request => (for {
        json <- request.body.validate[Contact] |? BadRequest
        dao <- contactDao.add(json) |? InternalServerError
      } yield Created(Json.toJson(dao))).merge
  }
```

<i> You can specify Result type on forHelpers</i>

#### Have fun ! 

--

<i> For-helpers is actually not on Maven Repository, you must copy/paste the helper class. But if you like and need (or prefer), I can make the repo on the day</i>


