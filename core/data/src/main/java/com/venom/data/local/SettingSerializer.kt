package com.venom.data.local

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.venom.data.model.SettingsPreferences
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

sealed class PreferencesSerializer<T> : Serializer<T> {
    protected val json = Json {
        ignoreUnknownKeys = true
    }

    protected abstract val defaultInstance: T
    protected abstract val serializer: KSerializer<T>

    override val defaultValue: T
        get() = defaultInstance

    override suspend fun readFrom(input: InputStream): T {
        return try {
            json.decodeFromString(
                deserializer = serializer,
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Error reading preferences", e)
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: T, output: OutputStream) {
        try {
            output.write(
                json.encodeToString(
                    serializer = serializer,
                    value = t
                ).toByteArray()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

object SettingsPreferencesSerializer : PreferencesSerializer<SettingsPreferences>() {
    override val defaultInstance: SettingsPreferences = SettingsPreferences()
    override val serializer: KSerializer<SettingsPreferences> = SettingsPreferences.serializer()
}