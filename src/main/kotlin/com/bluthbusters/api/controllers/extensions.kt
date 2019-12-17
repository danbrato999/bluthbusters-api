package com.bluthbusters.api.controllers

import com.bluthbusters.api.models.IdObject
import com.bluthbusters.api.models.PaginatedList
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.api.OperationResponse

fun Handler<AsyncResult<OperationResponse>>.ofId() = Handler<AsyncResult<IdObject>> { ar ->
  this.handle(
    ar.map {
      OperationResponse
        .completedWithJson(it.toJson())
        .setStatusCode(201)
    }
  )
}

fun Handler<AsyncResult<OperationResponse>>.ofPaginatedList() = Handler<AsyncResult<PaginatedList>> { ar ->
  this.handle(
    ar.map {
      OperationResponse.completedWithJson(it.toJson())
    }
  )
}

fun Handler<AsyncResult<OperationResponse>>.ofNullableJson() = Handler<AsyncResult<JsonObject?>> { ar ->
  this.handle(
    ar.map {
      if (it == null)
        OperationResponse().setStatusCode(404)
      else
        OperationResponse.completedWithJson(it)
    }
  )
}
