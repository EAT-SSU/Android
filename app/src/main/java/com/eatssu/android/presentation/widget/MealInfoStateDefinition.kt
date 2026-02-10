package com.eatssu.android.presentation.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.eatssu.android.domain.model.WidgetMealInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ConcurrentHashMap


object MealInfoStateDefinition : GlanceStateDefinition<WidgetMealInfo> {

    private const val DATA_STORE_FILENAME_PREFIX = "MealInfo_"

    private val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val storeByPath = ConcurrentHashMap<String, DataStore<WidgetMealInfo>>()

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ): DataStore<WidgetMealInfo> {
        val file = getLocation(context, fileKey)
        val path = file.absolutePath
        return storeByPath.getOrPut(path) {
            Timber.d("Create DataStore for $path")
            DataStoreFactory.create(
                serializer = MealInfoSerializer,
                produceFile = { file },
                scope = dataStoreScope,
            )
        }
    }

    override fun getLocation(context: Context, fileKey: String): File {
        // fileKey를 그대로 사용하여 파일명 생성 (appWidget-25 -> MealInfo_appWidget-25)
        val filename = "${DATA_STORE_FILENAME_PREFIX}${fileKey}"
        val file = context.dataStoreFile(filename)
        Timber.d("Glance location by widget '$fileKey' -> ${file.absolutePath}")
        return file
    }

    object MealInfoSerializer : Serializer<WidgetMealInfo> {
        override val defaultValue: WidgetMealInfo = WidgetMealInfo.Loading

        override suspend fun readFrom(input: InputStream): WidgetMealInfo {
            return try {
                val jsonRaw = input.readBytes().decodeToString()
                Timber.d("readFrom: raw = '$jsonRaw'")
                if (jsonRaw.isBlank()) return defaultValue

                Json.decodeFromString(jsonRaw)
            } catch (e: Exception) {
                Timber.e("Serialization error: ${e.message}")
                throw CorruptionException("Could not read data: ${e.message}", e)
            }
        }

        override suspend fun writeTo(t: WidgetMealInfo, output: OutputStream) {
            val json = Json.encodeToString(t)
            Timber.d("[writeTo] json = $json")
            output.use {
                it.write(json.encodeToByteArray())
            }
        }
    }
}
