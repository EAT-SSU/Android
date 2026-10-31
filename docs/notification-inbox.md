# 알림함 연동

현재 서버 알림 조회 API가 없어 기기에 수신한 학식 알림을 DataStore에 저장한다.
앱 재시작 후에도 읽음 상태를 유지한다. 최근 7 × 24시간의 알림만 노출하고 저장 시/화면 진입 시 만료 데이터를 정리한다.
화면을 열어둔 경우에도 1분마다 만료 여부와 상대 시간을 갱신한다.
기기 변경/앱 삭제 후 복구 및 여러 기기 간 읽음 상태 동기화는 서버 조회 API가 필요하다.

## 제안하는 FCM data 계약 (서버와 합의 전)

```json
{
  "message": {
    "token": "<device token>",
    "android": { "priority": "high" },
    "data": {
      "title": "🍽️ 찜한 메뉴가 나온대요!",
      "body": "오늘 점심, 도담식당에 파스타 등장 👀",
      "meal_date": "2026-08-10",
      "meal_time": "lunch"
    }
  }
}
```

- `meal_date`: ISO 날짜, 실제 이동할 식단 날짜.
- `meal_time`: `breakfast`, `lunch`, `dinner`.
- 수신 시각은 FCM의 `sentTime`, 중복 방지는 `messageId`를 사용한다.
- 제목/본문/날짜/식사 시간이 유효한 알림만 학식 알림함에 저장한다. 임의로 오늘 날짜를 추측하지 않는다.
- 기존 notification 본문 푸시 표시는 유지한다. `notification` 페이로드는 앱이 백그라운드일 때 시스템이 직접 표시하여 서비스 콜백이 실행되지 않으므로,
  알림함 저장에는 위처럼 **data 메시지**가 필요하다.
- 시스템 푸시를 누르면 Intro → Main → 해당 날짜·식사 시간의 학식 탭으로 연결한다.
- 알림함 항목 클릭은 읽음 처리 후 기존 학식 화면의 날짜와 식사 시간만 바꾼다. 상세 화면/바텀시트를 열지 않는다.
- 로컬 점심 리마인더는 찜 메뉴 알림이 아니므로 알림함에 추가하지 않는다.

참고: https://firebase.google.com/docs/cloud-messaging/android/receive-messages

## 디자인 확인

`NotificationScreen.kt`의 EmptyPreview / UnreadPreview / ReadPreview에서 세 상태를 확인할 수 있다.
Figma의 스피커 에셋은 원본 SVG를 Android Studio의 Svg2Vector 변환기로 VectorDrawable로 변환했고 24dp로 표시한다.
본문과 제목에는 줄 수 제한과 말줄임표를 적용하지 않는다.
