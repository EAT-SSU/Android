# Screenshot Regression Testing (Roborazzi + Robolectric)

## 목적
- JVM 단위 테스트(`app/src/test`)에서 화면 회귀를 검증합니다.
- 커버리지 범위:
  - `activity:` Manifest Activity
  - `fragment:` Fragment + BottomSheetFragment
  - `route:` Compose Navigation Route
  - `screen:` 화면급 Compose Screen(큐레이션)
- 누락 방지는 `ScreenCoverageGuardTest`에서 강제합니다.

## 왜 예전에는 `MainActivity Snapshot` 텍스트만 보였나?
기존 `ActivityScreenSnapshotsTest`는 `MainActivity`를 실제 레이아웃 대신 테스트용 플레이스홀더(`TextView("MainActivity Snapshot")`)로 렌더링했습니다.

개선 후에는 메인 홈 셸(상단 콘텐츠 + 하단 탭) 형태로 렌더되어 탭별 UI 회귀를 검증합니다.

## 실행 명령 (repo root 기준)
```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:recordRoborazziDebug
./gradlew :app:verifyRoborazziDebug
```

## 명령별 의미
- `:app:testDebugUnitTest`
  - 단위 테스트 실행(커버리지 가드 포함)
  - 스크린샷 이미지는 갱신하지 않음
- `:app:recordRoborazziDebug`
  - baseline 이미지 갱신
- `:app:verifyRoborazziDebug`
  - baseline 대비 회귀(diff) 검증

## 결과물 경로
- Baseline 이미지: `app/src/test/screenshots`
- 비교 산출물: `app/build/outputs/roborazzi`
- HTML 리포트: `app/build/reports/roborazzi/index.html`

## 실패 시 확인 방법
1. `verifyRoborazziDebug` 실패 후 HTML 리포트 확인
2. `app/build/outputs/roborazzi`의 `actual/expected/diff` 비교
3. 의도된 UI 변경이면 `recordRoborazziDebug`로 baseline 갱신 후 다시 `verifyRoborazziDebug`

## 결정성(Deterministic) 규칙
- Locale: `ko-KR`
- Timezone: `Asia/Seoul`
- fontScale: `1.0`
- qualifier: `ko-rKR-w411dp-h891dp-xxhdpi`
- animation scale: `0`

## 비결정 영역 정책
- Map / WebView는 테스트 seam(`ScreenshotTestSeam`)을 통해 결정적 렌더로 고정합니다.
- 목적은 외부 엔진/네트워크 변동으로 인한 false positive를 줄이고, 레이아웃/상태 회귀를 안정적으로 검증하는 것입니다.

## 상태 배지 정책
- 기본: 상태 배지 비노출
- 디버그 시에만 표시:
  - JVM 옵션: `-Deatssu.screenshot.badge=true`

## 커버리지 가드 구조
- 인벤토리: `app/src/test/java/com/eatssu/android/screenshot/inventory/ScreenCoverageRegistry.kt`
- 스캐너: `app/src/test/java/com/eatssu/android/screenshot/inventory/ScreenTargetScanner.kt`
- 가드 테스트: `app/src/test/java/com/eatssu/android/screenshot/inventory/ScreenCoverageGuardTest.kt`

가드는 아래 오류를 분리해서 보고합니다.
- `Missing coverage targets`
- `Stale coverage targets`
- `Missing compose screen coverage`
- `Stale compose screen coverage`
- `Uncategorized compose screens`

## 새 화면 추가 절차
1. 화면 구현 추가(Activity/Fragment/Route/Screen)
2. `ScreenCoverageRegistry.coverageItems`에 `targetId`, `states`, `testFile` 추가
3. 필요 시 `curatedComposeScreenTargets`, `excludedTargets`, `excludedComposeScreenTargets` 갱신
4. 테스트 렌더러(`ActivityScreenSnapshotsTest`, `FragmentScreenSnapshotsTest`, `ComposeRouteScreenshotsTest`)에 렌더 케이스 추가
5. `./gradlew :app:testDebugUnitTest --tests "com.eatssu.android.screenshot.inventory.ScreenCoverageGuardTest"` 확인
6. `./gradlew :app:recordRoborazziDebug`로 baseline 생성
7. `./gradlew :app:verifyRoborazziDebug`로 회귀 검증

## 시크릿 파일 정책
아래 파일은 로컬 전용이며 커밋 금지:
- `local.properties`
- `app/google-services.json`
- keystore / service account / `.env`
