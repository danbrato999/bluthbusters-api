package com.bluthbusters.api.modules

import com.bluthbusters.api.controllers.MovieRentalsController
import com.bluthbusters.api.controllers.MovieRentalsControllerImpl
import com.bluthbusters.api.controllers.MoviesController
import com.bluthbusters.api.controllers.MoviesControllerImpl
import io.vertx.core.AbstractVerticle

class ControllersModule : AbstractVerticle() {
  override fun start() {
    val moviesController = MoviesControllerImpl(buildProxy(MOVIES_DATA_STORE))
    val rentalsController = MovieRentalsControllerImpl(buildProxy(RENTALS_DATA_STORE))

    bindService(MOVIES_CONTROLLER, MoviesController::class.java, moviesController)
    bindService(MOVIE_RENTALS_CONTROLLER, MovieRentalsController::class.java, rentalsController)
  }
}
