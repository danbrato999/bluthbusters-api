package com.bluthbusters.api.models

import io.vertx.codegen.annotations.DataObject
import io.vertx.core.json.JsonObject

@DataObject
data class MovieData(val imdbId: String, val title: String, val genre: String, val year: Int, val director: String,
                     val runtime: String, val poster: String, val description: String) {
  constructor(json: JsonObject) : this(json.getString("imdbId"), json.getString("title"),
    json.getString("genre"), json.getInteger("year"), json.getString("director"),
    json.getString("runtime"), json.getString("poster"), json.getString("description"))

  fun toJson() : JsonObject = JsonObject.mapFrom(this)
}

