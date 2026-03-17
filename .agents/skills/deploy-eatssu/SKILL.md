---
name: deploy-eatssu
description: EAT-SSU Android 앱 릴리즈 자동화. 버전 범프, 릴리즈 노트 작성, release 브랜치 생성, PR 생성까지 전체 배포 플로우를 실행한다. "배포", "릴리즈", "release", "deploy", "출시", "버전 올려" 등의 키워드에 반응.
version: 1.0.1
---

# Deploy EAT-SSU

EAT-SSU Android 앱의 Google Play Store 릴리즈를 자동화하는 스킬.

## 트리거

사용자가 다음과 같이 요청할 때 이 스킬을 사용한다:
- "배포해줘", "릴리즈 만들어줘", "출시해줘"
- "버전 올려줘", "X.Y.Z 버전 만들어줘"
- "release 브랜치 만들어줘"
- "deploy", "release", "publish"

## 전제 조건

- `develop` 브랜치가 최신 상태여야 한다
- 새 버전 번호가 필요하다 (사용자 지정 또는 자동 결정)

## 릴리즈 플로우

### Step 1: 버전 결정

사용자가 버전을 지정하지 않았으면, 현재 버전을 확인하고 적절한 다음 버전을 제안한다.

```bash
# 현재 버전 확인
grep 'versionName' app/build.gradle.kts | head -1
grep 'versionCode' app/build.gradle.kts | head -1

# 최신 태그 확인
git tag --list | sort -V | tail -5
```

버전 규칙:
- `versionName`: Semantic Versioning (`X.Y.Z`)
- `versionCode`: 정수, 항상 +1 증가
- 태그 형식: `X.Y.Z` (v 접두사 없음)
- 릴리즈 노트 파일명: `release-notes/v<버전>.yml` (v 접두사 포함)
→ 태그는 v 없이, 릴리즈 노트 파일명은 v 포함

### Step 2: 변경사항 파악

직전 릴리즈 태그 이후의 커밋을 분석한다.

```bash
# 이전 태그 이후 커밋 목록 (머지 커밋 제외)
git log <이전태그>..develop --oneline --no-merges

# 변경된 파일 통계
git diff <이전태그>..develop --stat
```

각 커밋의 의미를 파악하여:
- 사용자에게 보이는 변경 (Feat, Fix) → 릴리즈 노트에 포함
- 내부 변경 (Refactor, CI/CD, Chore) → 릴리즈 노트에서 제외하되 PR 본문에는 포함

### Step 3: develop 브랜치에서 release 브랜치 생성

```bash
git checkout develop
git pull origin develop
git checkout -b release/<버전>
```

### Step 4: 버전 범프

`app/build.gradle.kts`의 `defaultConfig` 블록에서:

```kotlin
versionCode = <기존 + 1>
versionName = "<새 버전>"
```

### Step 5: 릴리즈 노트 작성

`release-notes/v<버전>.yml` 파일을 생성한다.

형식:
```yaml
ko: |
  - 한국어 릴리즈 노트 항목
en: |
  - English release note item
```

규칙 (반드시 준수):
1. 각 항목은 `-`로 시작
2. **한국어와 영어 모두 작성 필수**
3. 개발자 용어 사용 금지 — 사용자 친화적 언어 사용
4. 최소 10자 이상의 내용
5. CI/CD, 리팩토링 등 사용자에게 보이지 않는 변경은 "앱의 안정성과 성능을 개선했어요" 류로 통합
6. 이 내용이 **Google Play Store에 그대로 업로드**됨 (ko → Korea, en → Global)
7. 각 항목은 **반드시 한 문장만 사용**하고, 두 문장 이상으로 나누거나 접속사로 이어 붙여 문장 두 개처럼 쓰지 않는다

### Step 6: 커밋

```bash
git add app/build.gradle.kts release-notes/v<버전>.yml
git commit -m "release: <버전>"
```

커밋 메시지는 반드시 `release: X.Y.Z` 형식을 사용한다.

### Step 7: 푸시 & PR 생성

```bash
git push -u origin release/<버전>
```

PR 생성:
- **base**: `develop`
- **head**: `release/<버전>`
- **title**: `release: <버전>`
- **body**: 아래 템플릿 사용

```markdown
## Release <버전>

### 변경사항 (<이전버전> → <새버전>)

| 커밋 | 설명 | PR |
|------|------|----|
| `<hash>` | <커밋 메시지> | #<PR번호> |
| ... | ... | ... |

### 릴리즈 노트 (Play Store)

**한국어**
- <릴리즈 노트 항목들>

**English**
- <릴리즈 노트 항목들>

### 버전 정보
- `versionCode`: <이전> → <새>
- `versionName`: <이전버전> → <새버전>
```

## PR 머지 후 자동 처리

PR이 `develop`에 머지되면 GitHub Actions (`release.yml`)가 자동으로:

1. Fastlane으로 Release AAB 빌드
2. Google Play Store `production` 트랙에 배포
3. GitHub Release 생성 (태그 + 자동 릴리즈 노트 + AAB 첨부)
4. Slack 알림 전송

## 수동 배포 (workflow_dispatch)

GitHub Actions 탭에서 직접 실행도 가능:
1. Actions → **Production Release** 워크플로우
2. **Run workflow** 클릭
3. `version`, `track` (internal/alpha/beta/production), `release_notes` 입력

## 주의사항

- **절대 `main`/`master`에 직접 푸시하지 않는다** — 항상 PR을 통해 머지
- 릴리즈 노트 YAML 파일이 없거나 내용이 부족하면 Play Store 배포가 실패할 수 있다
- `versionCode`는 Play Store에서 고유해야 하므로 **반드시 증가**시킨다
- GitHub Secrets가 올바르게 설정되어 있어야 CI/CD가 동작한다
