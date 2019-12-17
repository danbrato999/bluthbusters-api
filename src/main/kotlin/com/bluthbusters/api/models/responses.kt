package com.bluthbusters.api.models

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

@DataObject
data class IdObject(val id: String) {
  constructor(json: JsonObject) : this(json.getString("id"))
  fun toJson() : JsonObject = JsonObject().put("id", id)
}

@DataObject
data class PaginatedList(val totalCount: Long = 0, val data: JsonArray = JsonArray()) {
  constructor(json: JsonObject) : this(json.getLong("totalCount"), json.getJsonArray("data"))
  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}

data class Inventory(val copies: Long, val available: Long)

data class Movie(val id: String, val externalData: MovieData, val trailer: String, val inventory: Inventory)

data class BasicRentalDetail(val id: String, val movieId: String, val rentedAt: String, val rentUntil: String,
                             val returnedAt: String? = null)

data class MovieRentalDetail(val id: String, val movieId: String, val rentedAt: String, val rentUntil: String,
                             val returnedAt: String?, val movie: MovieData) {
  constructor(rentalDetail: BasicRentalDetail, movie: MovieData) :
    this(rentalDetail.id, rentalDetail.movieId, rentalDetail.rentedAt, rentalDetail.rentUntil, rentalDetail.returnedAt, movie)
}
