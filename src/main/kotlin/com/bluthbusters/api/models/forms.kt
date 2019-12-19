package com.bluthbusters.api.models

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject

@DataObject
data class MovieForm(val externalData: MovieData, val trailer: String, val copies: Long) {
  constructor(json: JsonObject) : this(MovieData(json.getJsonObject("externalData")), json.getString("trailer"),
    json.getLong("copies"))

  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}

@DataObject
data class RentalForm(val movieId: String, val customerId: String, val rentUntil: String) {
  constructor(json: JsonObject) : this(json.getString("movieId"), json.getString("customerId"),
    json.getString("rentUntil"))

  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}

@DataObject
data class MovieDataSearchForm(val type: String, val value: String) {
  constructor(json: JsonObject) : this(json.getString("type"), json.getString("value"))

  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}
