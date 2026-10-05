package com.pemmob.schedulyy.data.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

interface CryptoEngine {
    fun encrypt(plaintext: String): String
    fun decrypt(ciphertext: String): String
}

@Singleton
class TinkCryptoEngine @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CryptoEngine {

    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(Aead::class.java)
    }

    override fun encrypt(plaintext: String): String {
        if (plaintext.isEmpty()) return ""
        return try {
            val ciphertext = aead.encrypt(plaintext.toByteArray(StandardCharsets.UTF_8), ASSOCIATED_DATA)
            Base64.getEncoder().encodeToString(ciphertext)
        } catch (e: Exception) {
            ""
        }
    }

    override fun decrypt(ciphertext: String): String {
        if (ciphertext.isEmpty()) return ""
        return try {
            val decoded = Base64.getDecoder().decode(ciphertext)
            val decryptedBytes = aead.decrypt(decoded, ASSOCIATED_DATA)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    companion object {
        private const val KEYSET_NAME = "schedulyy_keyset"
        private const val PREF_FILE_NAME = "schedulyy_tink_prefs"
        private const val MASTER_KEY_URI = "android-keystore://schedulyy_master_key"
        private val ASSOCIATED_DATA = "schedulyy_gemini_api_key".toByteArray(StandardCharsets.UTF_8)
    }
}
