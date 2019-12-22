package com.bluthbusters.api.services.firebase

import com.auth0.jwt.interfaces.RSAKeyProvider
import io.vertx.core.json.JsonObject
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import javax.security.cert.X509Certificate

class JsonObjectProvider(private val keyStore: JsonObject) : RSAKeyProvider {
  val createdAt = System.currentTimeMillis()

  override fun getPrivateKeyId(): String {
    throw NotImplementedError("Private key failure")
  }

  override fun getPrivateKey(): RSAPrivateKey {
    throw NotImplementedError("Private key failure")
  }

  override fun getPublicKeyById(keyId: String): RSAPublicKey {
    val publicKey = keyStore.getString(keyId)
    val cert = X509Certificate.getInstance(publicKey.byteInputStream())
    return cert.publicKey as RSAPublicKey
  }
}
