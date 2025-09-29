package com.eatssu.common.enums

import com.google.firebase.analytics.FirebaseAnalytics

enum class EventType(val value: String) {
    APP_LAUNCH("app_launch"),
    CLICK_RESTAURANT_INFO("click_restaurant_info"),
    SELECT_MEALTIME("select_mealtime"),
    CLICK_DAY("click_day"),
    CLICK_MENU("click_menu"),
    WRITE_REVIEW_V1("write_review_v1"),
    COMPLETE_REVIEW_V1("complete_review_v1"),
    COMPLETE_REVIEW_V2("complete_review_v2"),
    CLICK_MAP("click_map"),
    CLICK_MAP_MINE("click_map_mine"),
    CLICK_PARTNER_RESTAURANT("click_partner_restaurant"),
    ADD_WIDGET("add_widget"),
    REMOVE_WIDGET("remove_widget"),
    CHANGE_WIDGET("change_widget"),
    SCREEN_VIEW(FirebaseAnalytics.Event.SCREEN_VIEW),
}