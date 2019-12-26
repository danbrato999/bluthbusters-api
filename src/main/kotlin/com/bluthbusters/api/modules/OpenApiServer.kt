package com.bluthbusters.api.modules

import com.bluthbusters.api.services.firebase.FirebaseJwtProvider
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServer
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.handler.*
import org.slf4j.LoggerFactory

class OpenApiServer : AbstractVerticle() {
  private val logger = LoggerFactory.getLogger(OpenApiServer::class.java)

  override fun start(startPromise: Promise<Void>) {
    Future.future<OpenAPI3RouterFactory> {
      OpenAPI3RouterFactory.create(vertx, API_CONTRACT, it)
    }.map { routerFactory ->
      val provider = FirebaseJwtProvider(
        config().getJsonObject("firebase"),
        WebClient.create(vertx)
      )
      routerFactory.addSecurityHandler("firebase", JWTAuthHandler.create(provider))

      routerFactory.addGlobalHandler(corsHandler())
      routerFactory.mountServiceFromTag("movies", MOVIES_CONTROLLER)
      routerFactory.mountServiceFromTag("movie-rentals", MOVIE_RENTALS_CONTROLLER)

      routerFactory.router
    }.compose { router ->
      router.route("/*").handler(StaticHandler.create())
      router.errorHandler(500) {
        logger.error("Unexpected error in the app", it.failure())
      }

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
    .allowedMethods(setOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
    .allowedHeaders(setOf("authorization", "origin", "Content-Type", "accept", "Access-Control-Allow-Origin", "X-Host-Timezone"))
}
