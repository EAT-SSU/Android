# Agent Guide (EAT-SSU Android)
This repo is an Android app (Kotlin, MVVM, Clean-ish layering) built with Gradle Kotlin DSL.

## Rules Files (Cursor/Copilot)
- Cursor rules: none found (`.cursor/rules/` and `.cursorrules` do not exist).
- Copilot rules: none found (`.github/copilot-instructions.md` does not exist).

## Quick Context
- Modules: `:app`, `:core:common`, `:core:design-system` (see `settings.gradle`).
- Tooling: Gradle wrapper (`./gradlew`), JDK 17 (CI uses Temurin 17; app compiles with Java 17).
- Kotlin style: `kotlin.code.style=official` in `gradle.properties`.

## Local Setup Requirements
Build reads secrets from `local.properties` in `app/build.gradle.kts`.
- Required keys in `local.properties`:
  - `DEV_BASE_URL="..."`
  - `PROD_BASE_URL="..."`
  - `KAKAO_NATIVE_APP_KEY=...`
  - `NAVER_MAPS_CLIENT_ID=...`
- Firebase file is gitignored: `app/google-services.json`.
If these are missing, Gradle may crash during configuration (properties are read as non-null `String`).
Never commit secrets: `local.properties`, `app/google-services.json`, keystores (`*.jks`/`*.keystore`), service account JSON, `.env`.

## Build / Lint / Test Commands
All commands should be run from repo root.

### Build
- Full build (CI-style):
  - `./gradlew build`
- Debug APK:
  - `./gradlew :app:assembleDebug`

### Lint
Android Lint is available; app module has `lint { abortOnError = false }`.
- Lint app Debug:
  - `./gradlew :app:lintDebug`
Common report locations:
- `app/build/reports/lint-results-debug.html`
- `app/build/reports/lint-results-debug.xml`

### Unit Tests (JVM)
- Run all unit tests:
  - `./gradlew test`
- App unit tests (Debug):
  - `./gradlew :app:testDebugUnitTest`

Run a single unit test class/method (JUnit4):
- Class:
  - `./gradlew :app:testDebugUnitTest --tests "com.eatssu.android.SomeTest"`
- Method:
  - `./gradlew :app:testDebugUnitTest --tests "com.eatssu.android.SomeTest.someMethod"`

### Instrumentation Tests (Device/Emulator)
- Run connected tests (Debug):
  - `./gradlew :app:connectedDebugAndroidTest`

Run a single instrumentation test:
- Class:
  - `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.eatssu.android.ExampleInstrumentedTest`
- Method:
  - `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.eatssu.android.ExampleInstrumentedTest#useAppContext`

### CI Notes (GitHub Actions)
- Workflow: `.github/workflows/android.yml` runs `./gradlew build`.
- CI generates:
  - `local.properties` from GitHub Secrets
  - `app/google-services.json` from base64 secret

## Code Style & Conventions
Follow existing patterns first. Repo has minimal automated formatting/lint beyond Android/IDE defaults.

### Formatting
- Kotlin official style (Android Studio: "Kotlin style guide").
- Indent: 4 spaces; keep lines readable; prefer trailing commas in multiline params/args.
- Avoid reformat churn: change only what you touch.

### Imports
- No wildcard imports.
- Order: standard Kotlin/Java, then Android/AndroidX, then project (`com.eatssu...`), then third-party.
- Keep imports minimal; prefer explicit imports over fully-qualified usage in code.

### Naming
- Packages: `lowercase` (already: `com.eatssu.android.*`, `com.eatssu.common.*`).
- Classes/objects: `PascalCase`.
- Functions/vars: `camelCase`.
- Constants: `UPPER_SNAKE_CASE` (`const val`).
- Android components:
  - `*Activity`, `*Fragment`, `*ViewModel`, `*Adapter`, `*ViewHolder`.
- Domain:
  - `*UseCase` for usecases (`com.eatssu.android.domain.usecase.*`).
- Network/DTO:
  - `*Request`, `*Response` for DTOs; keep mapping out of UI layer.

### Types, Nullability, and State
- Prefer immutable types (`val`, read-only `List`/`Map`) unless mutation is necessary.
- Avoid `!!` in new code; use safe calls, early returns, or explicit error states.
- UI state uses `UiState<T>` from `core/common`:
  - `Init`, `Loading`, `Success(data)`, `Error`.
- ViewModels typically expose:
  - `private val _uiState = MutableStateFlow<UiState<...>>(UiState.Init)`
  - `val uiState: StateFlow<UiState<...>> = _uiState.asStateFlow()`
- One-off UI actions use `UiEvent` via `MutableSharedFlow` + `asSharedFlow()`.

### Coroutines / Flow
- Use `viewModelScope` for UI-driven work; keep long work off main.
- Prefer `Dispatchers.IO` for network/disk.
- If doing parallel work, use `async/awaitAll()` like `MenuViewModel.loadMenus()`.
- Keep Flow collection lifecycle-aware (e.g., `lifecycleScope.launch { ...collect... }`).

### Error Handling & Networking
- Network layer wraps calls with `ApiResult` (`app/src/main/java/com/eatssu/android/data/model/ApiResult.kt`).
- Prefer returning `ApiResult`/domain results instead of throwing.
- Handle failures explicitly:
  - `ApiResult.Failure(responseCode, message)` for server errors
  - `ApiResult.NetworkError(IOException)` for connectivity
  - `ApiResult.UnknownError(Throwable)` for unexpected
- Global behaviors:
  - `TokenStateManager` emits token state; `App` forwards to `TokenEventBus`.
  - `ApiResultCall` triggers `NetworkErrorEventBus` for `IOException`.

### Logging
- Use `Timber` (`Timber.d/e/w`) for logs; avoid `println`.
- Never log secrets (tokens, API keys, URLs with credentials).

### Dependency Injection
- Hilt is used (`@HiltAndroidApp`, `@HiltViewModel`, `@Inject`).
- Prefer constructor injection; keep modules under `app/src/main/java/com/eatssu/android/di`.
- Use qualifiers (e.g., `@ApplicationContext`) when required.

### Gradle / Dependencies
- Use Version Catalog (`gradle/libs.versions.toml`) via `libs.*` aliases.
- Prefer adding dependencies to the smallest module that needs them.
- Keep build config/secrets in `local.properties` (never hardcode keys in code).

## Project-Specific Docs
- Project overview: `README.md`.
- Project overview: `README.md`; troubleshooting: `.github/TROUBLESHOOTING.md`.
- Team conventions (external):
  - https://github.com/EAT-SSU/Android/wiki/Android-convention
  - https://github.com/EAT-SSU/Android/wiki/Git-convention

## Commit Message Convention
Format: `<type>: <Korean description>`
Examples: `fix: 00 고침`, `feat: 이력서 섹션 추가`, `style: 타이포 조정`
Keep the type in English (Conventional Commits style), and write the description in Korean.
간결하지만 자세한 작업 내용을 한글로 커밋한다.
커밋은 한 커밋에 하나의 일만 포함한다. 여러가지 일은 커밋을 쪼갠다.
