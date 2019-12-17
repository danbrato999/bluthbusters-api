package com.bluthbusters.api.services

import com.bluthbusters.api.mappers.MoviesMapper
import com.bluthbusters.api.models.IdObject
import com.bluthbusters.api.models.MovieForm
import com.bluthbusters.api.models.PaginatedList
import io.vertx.core.AsyncResult
import io.vertx.core.CompositeFuture
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.ext.mongo.findOptionsOf

class MoviesDataStoreImpl(val dbClient: MongoClient) : MoviesDataStore {
  private val collection = "movies"

  override fun add(form: MovieForm, handler: Handler<AsyncResult<IdObject>>) {
    dbClient.insert(collection, MoviesMapper.toMovieDocument(form)) { ar ->
      handler.handle(ar.map { IdObject(it) })
    }
  }

  override fun list(status: String, limit: Long, page: Long, handler: Handler<AsyncResult<PaginatedList>>) {
    val query = if (status == "all")
      JsonObject()
    else
      JsonObject().put("inventory.available", JsonObject().put("\$gt", 0))

    val pagination = findOptionsOf(limit = limit.toInt(), skip = ((page - 1) * limit).toInt())

    val queryFuture = Future.future<List<JsonObject>> {
      dbClient.findWithOptions(collection, query, pagination, it)
    }.map { JsonArray(it.map(MoviesMapper::fromMovieDocument)) }

    val countFuture = Future.future<Long> {
      dbClient.count(collection, query, it)
    }

    CompositeFuture.join(queryFuture, countFuture)
      .map { PaginatedList(countFuture.result(), queryFuture.result()) }
      .setHandler(handler)
  }

  override fun find(id: String, handler: Handler<AsyncResult<JsonObject?>>) {
    val query = JsonObject().put("_id", id)

    dbClient.findWithOptions(collection, query, findOptionsOf(limit = 1)) { ar ->
      handler.handle(ar.map { it.firstOrNull()?.let(MoviesMapper::fromMovieDocument) })
    }
  }
}
