package com.bluthbusters.api.services.firebase

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.RSAKeyProvider
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.User
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.jwt.JWTOptions
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import org.slf4j.LoggerFactory


class FirebaseJwtProvider(config: JsonObject, private val webClient: WebClient) : JWTAuth {
  private val projectId = config.getString("project_id")
  private val keysSource = config.getString("keys_source")
  private var cacheExpiration: Long = 0
  private var provider = JsonObjectProvider(JsonObject())

  override fun authenticate(authInfo: JsonObject?, resultHandler: Handler<AsyncResult<User>>) {
    withKeyProvider()
      .map<User> {
        val algorithm = Algorithm.RSA256(it)
        val verifier = JWT.require(algorithm)
          .withIssuer("https://securetoken.google.com/$projectId")
          .withAudience(projectId)
          .build()

        FirebaseUser(verifier.verify(authInfo?.getString("jwt")))
      }.setHandler(resultHandler)
  }

  override fun generateToken(claims: JsonObject?, options: JWTOptions?): String {
    throw NotImplementedError()
  }

  private fun withKeyProvider(): Future<RSAKeyProvider> {
    val current = System.currentTimeMillis()

    if (current - provider.createdAt < cacheExpiration)
      return Future.succeededFuture(provider)

    return Future.future<HttpResponse<Buffer>> {
      webClient.getAbs(keysSource)
        .send(it)
    }.map { response ->
      val cacheControl = response.getHeader("cache-control") ?: ""
      if (cacheRegex.containsMatchIn(cacheControl))
        cacheExpiration = (cacheRegex.find(cacheControl)
          ?.groupValues
          ?.get(1)
          ?.toLong() ?: 0) * 1000

      provider = JsonObjectProvider(response.bodyAsJsonObject())

      provider
    }
  }

  companion object {
    private val logger = LoggerFactory.getLogger(FirebaseJwtProvider::class.java)
    private val cacheRegex = Regex("max\\-age=(\\d+)")
  }
}
