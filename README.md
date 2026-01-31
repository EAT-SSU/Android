# 숭실대에서 먹자, 잇슈!
- 숭실대 학식 리뷰 앱
- 기간: 2023.03 ~  
- [PlayStore](https://play.google.com/store/apps/details?id=com.eatssu.android) 출시일 2023.11.28 ~
- [EAT-SSU 소개 Notion](https://eat-ssu.notion.site/EAT-SSU-1d2eeef75a1681198583e5282eaae6ba)
- [리쿠르팅](https://eat-ssu.notion.site/1d2eeef75a1681ae800cf6ffa6faa37d)

## 기여자 
|[유진](https://github.com/HI-JIN2)|[이현지](https://github.com/Amepistheo)|[강유리](https://github.com/kangyuri1114)|[정제훈](https://github.com/PeraSite)|
|:---:|:---:|:---:|:---:|
|![image](https://github.com/user-attachments/assets/aa023f64-60e1-4d6c-8ea2-35d75cc167f9)|![image](https://github.com/user-attachments/assets/3ecf5e16-35ea-452f-96b0-fa4479d7133c)|![image](https://github.com/user-attachments/assets/a1caa937-4cfe-4d19-a2f2-8335f12595d9)|![image](https://github.com/user-attachments/assets/10be935d-845c-4dff-9980-359b7708e129)
|2022.11~현재|2022.11~2024.02|2025.02~현재|2025.08~현재|

![그래픽이미지](https://github.com/user-attachments/assets/e89f46bb-dece-45a9-a453-a00bf9d463cd)




## 🛠 Tech Stack
- Kotlin
- MVVM
- Clean Architecture
- Coroutine + Flow
- UiState
- Hilt
- xml + viewBinding (+dataBinding)
- Retrofit2 + Okhttp3
- Gilde
- KaKao OAuth SDK
- Firebase RemoteConfig, Crashlytics 

## 🤔 Not Yet..
- Modularization
- Jetpack Compose
- DataSource + Repository Pattern

## 📄 Package
```
📦com.eatssu.android
├── 📂alarm
├── 📂data
│   ├── 📂dto
│   │   ├── 📂request
│   │   └── 📂response
│   ├── 📂enums
│   ├── 📂repository(impl)
│   └── 📂service
├── 📂di
├── 📂domain
│   ├── 📂model
│   ├── 📂repository
│   └── 📂usecase
├── 📂presentation
│   ├── 📂base
│   ├── 📂common
│   ├── 📂feature
│   │   └── 📂...
│   └── 📂util
└── 📄App
```


## 🤖 Android
- Android Studio : Android Studio Koala | 2024.1.1
- JDK : 17
- minSDK : 28
- targetSDK : 35

## 🐚 Convention
- [Android Convention Docs](https://github.com/EAT-SSU/Android/wiki/Android-convention)  
- [Git Convention Docs](https://github.com/EAT-SSU/Android/wiki/Git-convention)

## 🚀 CI/CD with Fastlane

이 프로젝트는 fastlane을 사용하여 자동화된 빌드 및 배포를 지원합니다.

**📖 상세한 가이드는 다음 문서를 참조하세요:**
👉 **[Fastlane을 이용한 배포 총 정리](.github/FASTLANE_DEPLOYMENT_GUIDE.md)**

### 빠른 시작

#### 로컬에서 사용

```bash
# 의존성 설치
bundle install

# QA APK 빌드
bundle exec fastlane build_qa_apk

# Release AAB 빌드
bundle exec fastlane build_release_aab environment:production
```

#### GitHub Actions에서 사용

1. **Actions** 탭 → **Fastlane CI/CD** 워크플로우 선택
2. **Run workflow** 클릭하여 수동 빌드
3. 또는 `develop` 브랜치에 push하면 자동 CI 빌드

#### Release 배포

`release/<version>` 브랜치를 `develop`에 머지하면 자동으로:

- AAB 빌드
- Play Store 배포
- GitHub Release 생성 (자동 릴리즈 노트)
- Git 태그 생성
