package com.eatssu.android.presentation.widget.we

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream


object MealInfoStateDefinition : GlanceStateDefinition<MealInfo> {

    private const val DATA_STORE_FILENAME = "MealInfo"

    val Context.datastore by dataStore(DATA_STORE_FILENAME, MealInfoSerializer)
    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<MealInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object MealInfoSerializer : Serializer<MealInfo> {
        override val defaultValue = MealInfo.Loading

        override suspend fun readFrom(input: InputStream): MealInfo = try {
            val jsonRaw = input.readBytes().decodeToString()
            Timber.d("🔍 readFrom: raw = '$jsonRaw'")
            Json.decodeFromString(MealInfo.serializer(), jsonRaw)
        } catch (exception: SerializationException) {
            Timber.e("❌ Serialization error: ${exception.message}")
            throw CorruptionException("Could not read data: ${exception.message}")
        }
        override suspend fun writeTo(t: MealInfo, output: OutputStream) {
            val json = Json.encodeToString(MealInfo.serializer(), t)
            Timber.d("💾 [writeTo] json = $json") // 이게 반드시 출력되어야 함
            output.use {
                it.write(json.encodeToByteArray())
            }
        }
    }
}