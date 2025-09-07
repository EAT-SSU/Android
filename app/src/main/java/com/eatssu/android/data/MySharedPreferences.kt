package com.eatssu.android.data


import android.content.Context
import android.content.SharedPreferences
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department

//자동 로그인을 위한 SharedPreferences

// TODO MySharedPreferences 자체에서 context를 주입받 받아야 하는데, 현재는 각 UseCase에서 context를 주입받아 사용하고 있음
object MySharedPreferences {
    private val MY_ACCOUNT: String = "account"

    fun setUserEmail(context: Context, input: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("MY_EMAIL", input)
        editor.commit()
    }

    fun getUserEmail(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("MY_EMAIL", "").toString()
    }

    fun setUserPlatform(context: Context, input: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("MY_PLATFORM", input)
        editor.commit()
    }

    fun getUserPlatform(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("MY_PLATFORM", "").toString()
    }

    fun setUserName(context: Context, input: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("MY_NAME", input)
        editor.commit()
    }

    fun getUserName(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("MY_NAME", "").toString()
    }

    fun setUserCollege(context: Context, input: College) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt("MY_COLLEGE_ID", input.collegeId)
        editor.putString("MY_COLLEGE", input.collegeName)
        editor.commit()
    }

    fun getUserCollegeId(context: Context): Int {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getInt("MY_COLLEGE_ID", -1)
    }

    fun getUserCollegeName(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("MY_COLLEGE", "").toString()
    }

    fun setUserDepartment(context: Context, input: Department) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt("MY_DEPARTMENT_ID", input.departmentId)
        editor.putString("MY_DEPARTMENT", input.departmentName)
        editor.commit()
    }

    fun getUserDepartmentId(context: Context): Int {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getInt("MY_DEPARTMENT_ID", -1)
    }

    fun getUserDepartmentName(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("MY_DEPARTMENT", "").toString()
    }

    fun setAccessToken(context: Context, input: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("ACCESS_TOKEN", input)
        editor.commit()
    }

    fun getAccessToken(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("ACCESS_TOKEN", "").toString()
    }

    fun setRefreshToken(context: Context, input: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("REFRESH_TOKEN", input)
        editor.apply()
    }

    fun getRefreshToken(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getString("REFRESH_TOKEN", "").toString()
    }

    fun clearUser(context: Context) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun setDailyNotification(context: Context, input: Boolean) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()

        editor.putBoolean("ALARM_ON", input)
        editor.apply()
    }

    fun getDailyNotification(context: Context): Boolean {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_ACCOUNT, Context.MODE_PRIVATE)
        return prefs.getBoolean("ALARM_ON", false)
    }
}