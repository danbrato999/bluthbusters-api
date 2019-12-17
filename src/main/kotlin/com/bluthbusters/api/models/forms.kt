package com.bluthbusters.api.models

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject

@DataObject
data class MovieForm(val externalData: MovieData, val trailer: String, val copies: Long) {
  constructor(json: JsonObject) : this(MovieData(json.getJsonObject("externalData")), json.getString("trailer"),
    json.getLong("copies"))

  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}
