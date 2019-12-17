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
import io.vertx.kotlin.ext.mongo.findOptionsOf

class MoviesDataStoreImpl(private val dbClient: MongoClient) : MoviesDataStore {
  override fun add(form: MovieForm, handler: Handler<AsyncResult<IdObject>>) {
    dbClient.insert(moviesCollection, MoviesMapper.toNewMovieDocument(form)) { ar ->
      handler.handle(ar.map { IdObject(it) })
    }
  }

  override fun list(status: String, limit: Long, page: Long, handler: Handler<AsyncResult<PaginatedList>>) {
    val query = if (status == "all")
      JsonObject()
    else
      JsonObject().put("\$where","this.inventory.copies > this.inventory.rented")

    val pagination = findOptionsOf(limit = limit.toInt(), skip = ((page - 1) * limit).toInt())

    val queryFuture = Future.future<List<JsonObject>> {
      dbClient.findWithOptions(moviesCollection, query, pagination, it)
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
}
