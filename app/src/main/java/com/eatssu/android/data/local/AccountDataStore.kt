package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.accountDataStore by preferencesDataStore(name = "account")

@Singleton
class AccountDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object Keys {
        val EMAIL = stringPreferencesKey("MY_EMAIL")
        val NAME = stringPreferencesKey("MY_NAME")
        val COLLEGE_ID = intPreferencesKey("MY_COLLEGE_ID")
        val COLLEGE_NAME = stringPreferencesKey("MY_COLLEGE")
        val DEPT_ID = intPreferencesKey("MY_DEPARTMENT_ID")
        val DEPT_NAME = stringPreferencesKey("MY_DEPARTMENT")
    }

    val email: Flow<String> = context.accountDataStore.data.map { it[EMAIL].orEmpty() }
    suspend fun setEmail(v: String) = context.accountDataStore.edit { it[EMAIL] = v }

    val name: Flow<String> = context.accountDataStore.data.map { it[NAME].orEmpty() }
    suspend fun setName(v: String) = context.accountDataStore.edit { it[NAME] = v }

    val college: Flow<College?> = context.accountDataStore.data.map {
        val id = it[COLLEGE_ID] ?: -1
        val name = it[COLLEGE_NAME]
        if (id >= 0 && !name.isNullOrBlank()) College(id, name) else null
    }

    suspend fun setCollege(v: College) = context.accountDataStore.edit {
        it[COLLEGE_ID] = v.collegeId
        it[COLLEGE_NAME] = v.collegeName
    }

    val department: Flow<Department?> = context.accountDataStore.data.map {
        val id = it[DEPT_ID] ?: -1
        val name = it[DEPT_NAME]
        if (id >= 0 && !name.isNullOrBlank()) Department(id, name) else null
    }

    suspend fun setDepartment(v: Department) = context.accountDataStore.edit {
        it[DEPT_ID] = v.departmentId
        it[DEPT_NAME] = v.departmentName
    }

    suspend fun clear() = context.accountDataStore.edit { it.clear() }
}
