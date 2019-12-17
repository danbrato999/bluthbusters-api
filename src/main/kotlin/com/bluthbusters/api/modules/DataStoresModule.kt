package com.bluthbusters.api.modules

import com.bluthbusters.api.services.MoviesDataStore
import com.bluthbusters.api.services.MoviesDataStoreImpl
import io.vertx.core.AbstractVerticle
import io.vertx.ext.mongo.MongoClient

class DataStoresModule : AbstractVerticle() {
  override fun start() {
    val client = MongoClient.createShared(vertx, config().getJsonObject("mongodb"))
    val moviesDataStore = MoviesDataStoreImpl(client)

    bindService(MOVIES_DATA_STORE, MoviesDataStore::class.java, moviesDataStore)
  }
}
