package com.bluthbusters.api.models

data class MovieData(val imdbId: String, val title: String, val genre: String, val year: Int, val director: String,
                     val runtime: String, val poster: String, val description: String)

data class Inventory(val copies: Long, val available: Long)

data class Movie(val id: String, val externalData: MovieData, val trailer: String?, val inventory: Inventory)

data class PaginatedList<T>(val totalCount: Long = 0, val data: List<T> = emptyList())

data class BasicRentalDetail(val id: String, val movieId: String, val rentedAt: String, val rentUntil: String,
                             val returnedAt: String? = null)

data class MovieRentalDetail(val id: String, val movieId: String, val rentedAt: String, val rentUntil: String,
                             val returnedAt: String?, val movie: MovieData) {
  constructor(rentalDetail: BasicRentalDetail, movie: MovieData) :
    this(rentalDetail.id, rentalDetail.movieId, rentalDetail.rentedAt, rentalDetail.rentUntil, rentalDetail.returnedAt, movie)
}
