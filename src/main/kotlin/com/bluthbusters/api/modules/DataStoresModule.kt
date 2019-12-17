package com.bluthbusters.api.modules

import com.bluthbusters.api.services.MoviesDataStore
import com.bluthbusters.api.services.MoviesDataStoreImpl
import com.bluthbusters.api.services.RentalsDataStore
import com.bluthbusters.api.services.RentalsDataStoreImpl
import io.vertx.core.AbstractVerticle
import io.vertx.ext.mongo.MongoClient

class DataStoresModule : AbstractVerticle() {
  override fun start() {
    val moviesDataStore = MoviesDataStoreImpl(MongoClient.createShared(vertx, config().getJsonObject("mongodb")))
    val rentalsDataStore = RentalsDataStoreImpl(MongoClient.createShared(vertx, config().getJsonObject("mongodb")))

    bindService(MOVIES_DATA_STORE, MoviesDataStore::class.java, moviesDataStore)
    bindService(RENTALS_DATA_STORE, RentalsDataStore::class.java, rentalsDataStore)
  }
}
