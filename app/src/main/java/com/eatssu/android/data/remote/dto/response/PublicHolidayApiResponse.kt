package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

@Serializable
data class PublicHolidayApiResponse(
    @SerialName("response") val response: Response? = null,
) {
    @Serializable
    data class Response(
        @SerialName("header") val header: Header? = null,
        @SerialName("body") val body: Body? = null,
    )

    @Serializable
    data class Header(
        @SerialName("resultCode") val resultCode: String? = null,
        @SerialName("resultMsg") val resultMsg: String? = null,
    )

    @Serializable
    data class Body(
        @SerialName("items") val items: Items? = null,
        @SerialName("numOfRows") val numOfRows: Int? = null,
        @SerialName("pageNo") val pageNo: Int? = null,
        @SerialName("totalCount") val totalCount: Int? = null,
    )

    @Serializable
    data class Items(
        @Serializable(with = PublicHolidayItemListSerializer::class)
        @SerialName("item") val item: List<Item> = emptyList(),
    )

    @OptIn(ExperimentalSerializationApi::class)
    object PublicHolidayItemListSerializer : JsonTransformingSerializer<List<Item>>(
        ListSerializer(Item.serializer())
    ) {
        override fun transformDeserialize(element: JsonElement): JsonElement {
            return when (element) {
                is JsonObject -> JsonArray(listOf(element))
                else -> element
            }
        }
    }

    @Serializable
    data class Item(
        @SerialName("locdate") val locdate: Long? = null,
        @SerialName("isHoliday") val isHoliday: String? = null,
        @SerialName("dateName") val dateName: String? = null,
    )
}
