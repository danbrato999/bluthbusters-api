package com.bluthbusters.api.modules

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler

class OpenApiServer : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>) {
    Future.future<OpenAPI3RouterFactory> {
      OpenAPI3RouterFactory.create(vertx, API_CONTRACT, it)
    }.map { routerFactory ->
      routerFactory.addGlobalHandler(corsHandler())
      routerFactory.mountServiceFromTag("movies", MOVIES_CONTROLLER)
      routerFactory.mountServiceFromTag("customers", CUSTOMERS_CONTROLLER)

      routerFactory.router
    }.compose { router ->
      router.route("/*").handler(StaticHandler.create())
      val port: Int = config().getString("port").toInt()

      Future.future<HttpServer> {
        vertx.createHttpServer()
          .requestHandler(router)
          .listen(port, it)
      }.setHandler {
        if (it.succeeded())
          startPromise.complete()
        else
          startPromise.fail(it.cause())
      }
    }
  }

  private fun corsHandler() = CorsHandler.create(config().getString("cors"))
    .allowCredentials(true)
    .allowedMethods(setOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT))
    .allowedHeaders(setOf("authorization", "origin", "Content-Type", "accept", "Access-Control-Allow-Origin"))
}
