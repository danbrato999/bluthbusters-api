package com.bluthbusters.api.mappers

import com.bluthbusters.api.models.MovieForm
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

object MoviesMapper {
  fun toNewMovieDocument(form: MovieForm) : JsonObject = json {
    obj(
      "externalData" to form.externalData.toJson(),
      "trailer" to form.trailer,
      "inventory" to obj(
        "copies" to form.copies,
        "rented" to 0L
      )
    )
  }

  fun fromMovieDocument(document: JsonObject) : JsonObject = json {
    obj(
      "id" to document.getString("_id"),
      "externalData" to document.getJsonObject("externalData"),
      "trailer" to document.getString("trailer"),
      "inventory" to document.getJsonObject("inventory").let { inventory ->
        obj(
          "copies" to inventory.getLong("copies"),
          "available" to inventory.getLong("copies") - inventory.getLong("rented")
        )
      }
    )
  }

  fun toUpdatedMovie(form: MovieForm, currentMovie: JsonObject) : JsonObject {
    val rentedAmount = currentMovie.getJsonObject("inventory").getLong("copies") -
      currentMovie.getJsonObject("inventory").getLong("available")

    return json {
      obj(
        "_id" to currentMovie.getString("id"),
        "externalData" to form.externalData.toJson(),
        "trailer" to form.trailer,
        "inventory" to obj(
          "copies" to form.copies,
          "rented" to rentedAmount
        )
      )
    }
  }
}
