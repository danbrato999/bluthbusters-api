package com.bluthbusters.api.modules

import com.bluthbusters.api.controllers.CustomersController
import com.bluthbusters.api.controllers.CustomersControllerImpl
import com.bluthbusters.api.controllers.MoviesController
import com.bluthbusters.api.controllers.MoviesControllerImpl
import io.vertx.core.AbstractVerticle

class ControllersModule : AbstractVerticle() {
  override fun start() {
    val moviesController = MoviesControllerImpl(buildProxy(MOVIES_DATA_STORE))
    val customersController = CustomersControllerImpl()

    bindService(MOVIES_CONTROLLER, MoviesController::class.java, moviesController)
    bindService(CUSTOMERS_CONTROLLER, CustomersController::class.java, customersController)
  }
}
