# 숭실대에서 먹자, 잇슈!
- 숭실대 학식 리뷰 앱
- 기간: 2023.03 ~  
- [PlayStore](https://play.google.com/store/apps/details?id=com.eatssu.android) 출시일 2023.11.28 ~
- [EAT-SSU 소개 Notion](https://eat-ssu.notion.site/EAT-SSU-1d2eeef75a1681198583e5282eaae6ba)

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
- minSDK : 23
- targetSDK : 34

## 🐚 Convertion
- [Android Convention Docs](https://github.com/EAT-SSU/Android/wiki/Android-convention)  
- [Git Convention Docs](https://github.com/EAT-SSU/Android/wiki/Git-convention)
