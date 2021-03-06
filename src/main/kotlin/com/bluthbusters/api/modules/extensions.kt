package com.bluthbusters.api.modules

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.core.json.JsonObject
import io.vertx.serviceproxy.ServiceBinder
import io.vertx.serviceproxy.ServiceProxyBuilder

const val API_CONTRACT = "webroot/docs/openapi.yaml"
const val MOVIES_CONTROLLER = "controllers.movies"
const val MOVIE_RENTALS_CONTROLLER = "controllers.movie-rentals"
const val MOVIES_DATA_STORE = "services.data.movies"
const val RENTALS_DATA_STORE = "services.data.rentals"
const val OMDB_API_CLIENT_SERVICE = "services.apis.omdb"
const val YOUTUBE_API_CLIENT_SERVICE = "services.apis.youtube"

fun <T> AbstractVerticle.bindService(address: String, clazz: Class<T>, impl: T): MessageConsumer<JsonObject> =
  ServiceBinder(vertx)
    .setAddress(address)
    .register(clazz, impl)

inline fun <reified T> AbstractVerticle.buildProxy(address: String): T =
  ServiceProxyBuilder(vertx)
    .setAddress(address)
    .build(T::class.java)

