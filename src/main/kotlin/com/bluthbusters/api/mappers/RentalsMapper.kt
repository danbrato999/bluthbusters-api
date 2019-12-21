package com.bluthbusters.api.mappers

import com.bluthbusters.api.models.RentalForm
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter

object RentalsMapper {
  fun toNewRentalDocument(rentalForm: RentalForm) : JsonObject = json {
    obj(
      "movieId" to rentalForm.movieId,
      "customerId" to rentalForm.customerId,
      "rentedAt" to currentDate(),
      "rentUntil" to obj(
        "\$date" to LocalDate.parse(rentalForm.rentUntil)
          .atTime(OffsetTime.MAX)
          .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      )
    )
  }

  fun fromNewRentalDocument(newId: String, rentalForm: RentalForm) : JsonObject = json {
    obj(
      "id" to newId,
      "movieId" to rentalForm.movieId,
      "rentedAt" to OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
      "rentUntil" to rentalForm.rentUntil
    )
  }

  fun fromRentalDocument(document: JsonObject) : JsonObject {
    val rentedAt: String = document.getJsonObject("rentedAt")
      .getString("\$date")
    val rentUntil: String = OffsetDateTime.parse(document.getJsonObject("rentUntil").getString("\$date"))
      .toLocalDate()
      .toString()
    val returnedAt: String? = document.getJsonObject("returnedAt", null)
      ?.getString("\$date")

    val docResponse = json {
      obj(
        "id" to document.getString("_id"),
        "movieId" to document.getString("movieId"),
        "rentedAt" to rentedAt,
        "rentUntil" to rentUntil
      )
    }

    return if (returnedAt == null) docResponse else docResponse.put("returnedAt", returnedAt)
  }

  fun fromDetailedMovieRentalDocument(document: JsonObject) : JsonObject {
    val movieData =  (document.getJsonArray("movie").first() as JsonObject)
      .getJsonObject("externalData")

    return fromRentalDocument(document)
      .put("movie", movieData)
  }

  fun currentDate() : JsonObject =
    JsonObject().put("\$date", OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
}
