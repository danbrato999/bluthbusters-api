package com.bluthbusters.api.controllers

import com.bluthbusters.api.models.BasicRentalDetail
import com.bluthbusters.api.models.Inventory
import com.bluthbusters.api.models.Movie
import com.bluthbusters.api.models.MovieData
import java.time.LocalDate
import java.time.LocalDateTime

object Data {
  val movies = listOf(
    Movie(
      id = "tt0088763",
      externalData = MovieData(
        "tt0088763",
        "Back to the Future",
        "Adventure, Comedy, Sci-Fi",
        1985,
        "Robert Zemeckis",
        "116 min",
        "https://m.media-amazon.com/images/M/MV5BZmU0M2Y1OGUtZjIxNi00ZjBkLTg1MjgtOWIyNThiZWIwYjRiXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg",
        "Marty McFly, a 17-year-old high school student, is accidentally sent thirty years into the past in a time-traveling DeLorean invented by his close friend, the eccentric scientist Doc Brown."
      ),
      trailer = "https://www.youtube.com/embed/qvsgGtivCgs",
      inventory = Inventory( 10, 9 )
    ),
    Movie(
      id = "tt0110912",
      externalData = MovieData(
        "tt0110912",
        "Pulp Fiction",
        "Crime, Drama",
        1994,
        "Quentin Tarantino",
        "154 min",
        "https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg",
        "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."
      ),
      trailer = "https://www.youtube.com/embed/s7EdQ4FqbhY",
      inventory = Inventory(5, 5)
    )
  )

  val movieRentals = listOf(
    BasicRentalDetail(
      id = "lending0001",
      movieId = "tt0088763",
      rentedAt = LocalDateTime.of(2019, 10, 12, 10, 0,0).toString(),
      rentUntil = LocalDate.of(2019, 11, 5).toString(),
      returnedAt = LocalDateTime.of(2019, 10, 30, 16, 0, 0).toString()
    ),
    BasicRentalDetail(
      id = "lending0002",
      movieId = "tt0088763",
      rentedAt = LocalDateTime.of(2019, 12, 15, 12, 0, 0).toString(),
      rentUntil = LocalDate.of(2019, 12, 20).toString()
    )
  )
}
