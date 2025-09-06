package com.eatssu.common

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

enum class Screen(val value: String) {
    MAIN("main_page"),
    NOTICE("notice_page"),
    SETTING("setting_page"),
    SCHOOL_SETTING("school_setting_page"),
    MODIFY_TIME_TABLE("modify_time_table_page"),
}

enum class LaunchPath(val value: String) {
    ICON("icon"),
    LOCAL_NOTIFICATION("local_notification"),
    WIDGET("widget"),
}



enum class InquiryType(val value: String) {
    MAIL("mail"),
    GITHUB("github"),
}

enum class SelectedType(val value: String) {
    SWIPED("swiped"),
    TAPPED("tapped"),
}

enum class SchoolSettingStep(val value: String) {
    SCHOOL("school"),
    GRADE("grade"),
    CLASS("class"),
    MAJOR("major"),
}

enum class Weekday(val value: String) {
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일")
}



object EventLogger {

    fun setUserProperties(vararg properties: Pair<String, String>) {
        properties.forEach { property ->
            firebaseAnalytics.setUserProperty(property.first, property.second)
        }
    }

    fun appLaunch(launchPath: LaunchPath) {
        firebaseAnalytics.logEvent("app_launch") {
            param("launch_path", launchPath.value)
        }
    }


    fun pageShowed(screen: Screen) {
        firebaseAnalytics.logEvent("page_showed") {
            param("page_name", screen.value)
        }
    }

    fun selectInquiryType(inquiryType: InquiryType) {
        firebaseAnalytics.logEvent("select_inquiry_type") {
            param("type", inquiryType.value)
        }
    }

    fun clickNoticeButton() {
        firebaseAnalytics.logEvent("click_notice_button", null)
    }

    fun clickIsSkipWeekendToggle(isSkipWeekend: Boolean) {
        firebaseAnalytics.logEvent("click_is_skip_weekend_toggle") {
            param("is_skip_weekend", isSkipWeekend.toString())
        }
    }


    fun clickIsOnModifiedTimeTableToggle(isOnModifiedTimeTable: Boolean) {
        firebaseAnalytics.logEvent("click_is_on_modified_time_table_toggle") {
            param("is_on_modified", isOnModifiedTimeTable.toString())
        }
    }

    fun selectedMealTab(selectedType: SelectedType) {
        firebaseAnalytics.logEvent("selected_meal_tab") {
            param("type", selectedType.value)
        }
    }

    fun clickIsSkipAfterDinnerToggle(isSkipAfterDinner: Boolean) {
        firebaseAnalytics.logEvent("click_is_skip_after_dinner_toggle") {
            param("is_skip_after_dinner", isSkipAfterDinner.toString())
        }
    }

    fun clickTutorialButton() {
        firebaseAnalytics.logEvent("click_tutorial_button", null)
    }

    fun clickSchoolSettingButton() {
        firebaseAnalytics.logEvent("click_school_setting_button", null)
    }

    fun selectTimeTableTab(selectedType: SelectedType) {
        firebaseAnalytics.logEvent("select_time_table_tab") {
            param("type", selectedType.value)
        }
    }

    fun clickAllergySettingButton() {
        firebaseAnalytics.logEvent("click_allergy_setting_button", null)
    }

    fun clickInquiryButton() {
        firebaseAnalytics.logEvent("click_inquiry_button", null)
    }

    fun clickSettingButton() {
        firebaseAnalytics.logEvent("click_setting_button", null)
    }

    fun clickModifyTimeTableButton() {
        firebaseAnalytics.logEvent("click_modify_time_table_button", null)
    }

    fun completeSchoolSettingStep(step: SchoolSettingStep) {
        firebaseAnalytics.logEvent("complete_school_setting_step") {
            param("step", step.value)
        }
    }

    fun completeModifyTimeTable(weekday: Weekday) {
        firebaseAnalytics.logEvent("complete_modify_time_table") {
            param("week", weekday.value)
        }
    }

    fun addWidget() {

    }
}