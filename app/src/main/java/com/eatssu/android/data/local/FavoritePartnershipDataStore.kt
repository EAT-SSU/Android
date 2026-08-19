package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private val Context.favoritePartnershipDataStore by preferencesDataStore(
    name = "favorite_partnership",
)

class FavoritePartnershipDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        val FAVORITE_ORDER = stringPreferencesKey("favorite_order")
    }

    val favoriteOrder: Flow<List<Int>> = context.favoritePartnershipDataStore.data
        .map { preferences ->
            preferences[FAVORITE_ORDER].decodeFavoriteOrder()
        }

    suspend fun markLiked(partnershipId: Int) {
        updateOrder { current -> listOf(partnershipId) + current.filterNot { it == partnershipId } }
    }

    suspend fun markUnliked(partnershipId: Int) {
        updateOrder { current -> current.filterNot { it == partnershipId } }
    }

    suspend fun reconcile(serverIds: List<Int>): List<Int> {
        var reconciled = emptyList<Int>()
        context.favoritePartnershipDataStore.edit { preferences ->
            reconciled = reconcileFavoriteOrder(
                current = preferences[FAVORITE_ORDER].decodeFavoriteOrder(),
                serverIds = serverIds,
            )
            preferences[FAVORITE_ORDER] = Json.encodeToString(reconciled)
        }
        return reconciled
    }

    suspend fun clear() {
        context.favoritePartnershipDataStore.edit { it.clear() }
    }

    private suspend fun updateOrder(transform: (List<Int>) -> List<Int>) {
        context.favoritePartnershipDataStore.edit { preferences ->
            val current = preferences[FAVORITE_ORDER].decodeFavoriteOrder()
            preferences[FAVORITE_ORDER] = Json.encodeToString(transform(current))
        }
    }
}

internal fun reconcileFavoriteOrder(
    current: List<Int>,
    serverIds: List<Int>,
): List<Int> {
    val serverIdSet = serverIds.toSet()
    return buildList {
        // 저장된 정렬 정보에 중복 ID가 있어도 화면 목록에는 한 번만 노출한다.
        addAll(current.filter { it in serverIdSet }.distinct())
        addAll(serverIds.filterNot { it in this })
    }
}

private fun String?.decodeFavoriteOrder(): List<Int> =
    this?.let { value -> runCatching { Json.decodeFromString<List<Int>>(value) }.getOrNull() }
        .orEmpty()
