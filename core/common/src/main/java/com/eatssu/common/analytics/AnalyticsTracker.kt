package com.eatssu.common.analytics

/**
 * AnalyticsTracker: 앱 코드가 의존하는 단일 진입점.
 * 화면/뷰모델은 전송 SDK를 모르고 typed event만 기록한다.
 * 실제 전송 대상(Firebase, PostHog 등)은 내부 구현이 fan-out 한다.
 */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)

    fun identify(identity: AnalyticsIdentity)

    fun resetIdentity()
}
