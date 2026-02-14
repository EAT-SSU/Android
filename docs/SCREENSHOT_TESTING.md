# Screenshot Regression Testing (Roborazzi + Robolectric)

## 목적
- JVM 단위 테스트 환경(`app/src/test`)에서 전체 화면 스크린샷 회귀 테스트를 실행합니다.
- 대상 범위는 `Manifest Activity + Navigation Fragment + Compose Route`입니다.
- 누락 방지는 `ScreenCoverageGuardTest`로 강제합니다.

## 실행 명령
아래 명령은 저장소 루트(`/Users/perasite/.codex/worktrees/048f/EATSSU-Android`)에서 실행합니다.

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:recordRoborazziDebug
./gradlew :app:verifyRoborazziDebug
```

## 결과물 경로
- Baseline 이미지: `/Users/perasite/.codex/worktrees/048f/EATSSU-Android/app/src/test/screenshots`
- 비교 산출물: `/Users/perasite/.codex/worktrees/048f/EATSSU-Android/app/build/outputs/roborazzi`
- HTML 리포트: `/Users/perasite/.codex/worktrees/048f/EATSSU-Android/app/build/reports/roborazzi/index.html`

## 실패 시 확인 방법
1. `verifyRoborazziDebug` 실패 후 HTML 리포트를 엽니다.
2. `app/build/outputs/roborazzi`에서 `actual/expected/diff` 이미지를 확인합니다.
3. 의도된 UI 변경이면 `recordRoborazziDebug`로 baseline을 갱신하고 다시 `verifyRoborazziDebug`를 실행합니다.

## 결정성(Deterministic) 규칙
- Locale: `ko-KR`
- Timezone: `Asia/Seoul`
- fontScale: `1.0`
- qualifier: `ko-rKR-w411dp-h891dp-xxhdpi`
- 애니메이션 scale: `0`
- 테스트 seam(`ScreenshotTestSeam`)을 통해 Map/WebView/Analytics 비결정 요소를 고정 렌더링/스킵 처리합니다.

## 커버리지 가드
- 스캐너: `ScreenTargetScanner`
  - `AndroidManifest.xml` Activity
  - `eatssu_navigation.xml` Fragment
  - `ReviewNav`, `MyReviewNav` Route
- 레지스트리: `ScreenCoverageRegistry`
- 가드 테스트: `ScreenCoverageGuardTest`

스캐너 대상이 레지스트리에서 누락되거나, 레지스트리에 오래된 대상이 남아 있으면 테스트가 실패합니다.

## 시크릿 파일 정책
아래 파일은 로컬에서만 사용하고 절대 커밋하지 않습니다.
- `local.properties`
- `app/google-services.json`
- keystore / service account / env 파일
