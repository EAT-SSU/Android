package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.eatssu.android.domain.model.WidgetMealInfo
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream


object MealInfoStateDefinition : GlanceStateDefinition<WidgetMealInfo> {

    private const val DATA_STORE_FILENAME = "MealInfo"

    val Context.datastore by dataStore(DATA_STORE_FILENAME, MealInfoSerializer)
    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetMealInfo> {
        return context.datastore
    }

    override fun getLocation(context: Context, fileKey: String): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    object MealInfoSerializer : Serializer<WidgetMealInfo> {
        override val defaultValue = WidgetMealInfo.Loading

        override suspend fun readFrom(input: InputStream): WidgetMealInfo = try {
            val jsonRaw = input.readBytes().decodeToString()
            Timber.d("readFrom: raw = '$jsonRaw'")
            Json.decodeFromString(WidgetMealInfo.serializer(), jsonRaw)
        } catch (exception: SerializationException) {
            Timber.e("Serialization error: ${exception.message}")
            throw CorruptionException("Could not read data: ${exception.message}")
        }

        override suspend fun writeTo(t: WidgetMealInfo, output: OutputStream) {
            val json = Json.encodeToString(WidgetMealInfo.serializer(), t)
            Timber.d("[writeTo] json = $json") // 이게 반드시 출력되어야 함
            output.use {
                it.write(json.encodeToByteArray())
            }
        }
    }
}