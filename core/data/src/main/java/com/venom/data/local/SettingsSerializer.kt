package com.venom.data.local

import androidx.datastore.core.Serializer
import com.venom.data.model.SettingsPreferences
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object SettingsSerializer : Serializer<SettingsPreferences> {
    override val defaultValue: SettingsPreferences = SettingsPreferences()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun readFrom(input: InputStream): SettingsPreferences = try {
        json.decodeFromString(
            SettingsPreferences.serializer(), input.readBytes().decodeToString()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        defaultValue
    }

    override suspend fun writeTo(t: SettingsPreferences, output: OutputStream) = try {
        output.write(
            json.encodeToString(SettingsPreferences.serializer(), t).encodeToByteArray()
        )
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}