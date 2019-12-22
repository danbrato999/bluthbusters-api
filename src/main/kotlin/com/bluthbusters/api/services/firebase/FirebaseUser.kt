package com.bluthbusters.api.services.firebase

import com.auth0.jwt.interfaces.DecodedJWT
import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class FirebaseUser(jwt: DecodedJWT) : AbstractUser() {
  private val data = json {
    obj(
      "sub" to jwt.subject
    )
  }

  override fun doIsPermitted(permission: String, resultHandler: Handler<AsyncResult<Boolean>>) {
    resultHandler.handle(Future.succeededFuture(true))
  }

  override fun setAuthProvider(authProvider: AuthProvider) {
  }

  override fun principal(): JsonObject = data
}
