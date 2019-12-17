package com.bluthbusters.api.mappers

import com.bluthbusters.api.models.MovieForm
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

object MoviesMapper {
  fun toMovieDocument(form: MovieForm) : JsonObject = json {
    obj(
      "externalData" to form.externalData.toJson(),
      "trailer" to form.trailer,
      "inventory" to obj(
        "copies" to form.copies,
        "available" to form.copies
      )
    )
  }

  fun fromMovieDocument(document: JsonObject) : JsonObject {
    val id: String = document.remove("_id").toString()
    return document.put("id", id)
  }
}
