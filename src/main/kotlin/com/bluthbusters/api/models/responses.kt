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
