package com.bluthbusters.api.services

import com.bluthbusters.api.mappers.MoviesMapper
import com.bluthbusters.api.models.IdObject
import com.bluthbusters.api.models.MovieForm
import com.bluthbusters.api.models.PaginatedList
import com.bluthbusters.api.services.Collections.moviesCollection
import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.findOptionsOf

class MoviesDataStoreImpl(private val dbClient: MongoClient) : MoviesDataStore {
  override fun add(form: MovieForm, handler: Handler<AsyncResult<IdObject>>) {
    dbClient.insert(moviesCollection, MoviesMapper.toNewMovieDocument(form)) { ar ->
      handler.handle(ar.map { IdObject(it) })
    }
  }

  override fun list(
    name: String?,
    status: String,
    limit: Long,
    page: Long,
    handler: Handler<AsyncResult<PaginatedList>>
  ) {
    val statusQuery = if (status == "all")
      JsonObject()
    else
      JsonObject().put("\$where","this.inventory.copies > this.inventory.rented")

    val nameQuery = if (name.isNullOrBlank())
      JsonObject()
    else
      json {
        obj(
          "externalData.title" to obj(
            "\$regex" to name,
            "\$options" to "i"
          )
        )
      }

    val query = nameQuery.mergeIn(statusQuery)
    val options = findOptionsOf(
      limit = limit.toInt(),
      skip = ((page - 1) * limit).toInt(),
      sort = JsonObject().put("externalData.title", 1)
    )

    val queryFuture = Future.future<List<JsonObject>> {
      dbClient.findWithOptions(moviesCollection, query, options, it)
    }.map { JsonArray(it.map(MoviesMapper::fromMovieDocument)) }

    val countFuture = Future.future<Long> {
      dbClient.count(moviesCollection, query, it)
    }

    CompositeFuture.join(queryFuture, countFuture)
      .map { PaginatedList(countFuture.result(), queryFuture.result()) }
      .setHandler(handler)
  }

  override fun find(id: String, handler: Handler<AsyncResult<JsonObject?>>) {
    val query = JsonObject().put("_id", id)

    dbClient.findWithOptions(moviesCollection, query, findOptionsOf(limit = 1)) { ar ->
      handler.handle(ar.map { it.firstOrNull()?.let(MoviesMapper::fromMovieDocument) })
    }
  }

  override fun update(id: String, form: MovieForm, handler: Handler<AsyncResult<Long?>>) {
    Future.future<JsonObject?> { find(id, it) }
      .compose<Long> { movie ->
        if (movie == null)
          Future.succeededFuture(null)
        else
          diffUpdate(form, movie)
      }.setHandler(handler)
  }

  private fun diffUpdate(form: MovieForm, currentMovie: JsonObject) : Future<Long> {
    val rentedAmount = currentMovie.getJsonObject("inventory").getLong("copies") -
      currentMovie.getJsonObject("inventory").getLong("available")

    // If new amount less than the current copies rented return error
    if (form.copies < rentedAmount)
      return Future.succeededFuture(0L)

    return Future.future<String> {
      dbClient.save(moviesCollection, MoviesMapper.toUpdatedMovie(form, currentMovie), it)
    }.map(1L)
  }
}
