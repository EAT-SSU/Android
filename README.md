# EatSSU-Android
숭실대에서 먹자, 잇슈!

## 👩‍💻 History
- 기획 `2022.10.10 ~ 2022.11.01`
- 1차 개발  `2022.11.08 ~ 2022.12.14` [사용자인터페이스및실습 강의 수강: JAVA+Firesbase](https://github.com/EAT-SSU/EAT-SSU)  
- 2차 개발 `2023.02.13 ~ 2023.09`
  - 1.0.0 첫 릴리즈 `2023.11.13` [Play Store](https://play.google.com/console/u/0/developers/8219547653514217920/app/4974246039463609210/app-dashboard?timespan=thirtyDays)
  - 1.1.0 디자인 시스템 적용 `2023.11.18`
  - 현재 MVVM 리팩토링 진행 중
  
## 🧚‍♀️ Member 
| 유진<br/>([@HI-JIN2](https://github.com/HI-JIN2)) | 이현지<br/>([@Amepistheo](https://github.com/Amepistheo)) |
| :---: | :---: |
| <img width="250" src="https://avatars.githubusercontent.com/u/94737714?v=4"/> | <img width="250" src="https://avatars.githubusercontent.com/u/110108243?v=4"/> |
| `로그인` `리뷰`<br/>`마이 페이지`  | `메인 페이지`<br/>`신고하기` `공지사항`|

## 📄 Package
### Architecture
- MVVM architecture pattern으로 리팩토링 중에 있으며, 현재 부분적으로 적용되어 있습니다.  
- 이전 코드는 architecture pattern이 없는 코드 입니다. 
```
com.eatssu.android
├── base
├── data
│   ├── entity
│   ├── enums
│   ├── model
│   │   ├── request
│   │   ├── response
│   ├── repository
│   └── service
├── ui
│   ├── common
│   ├── info
│   ├── login
│   ├── main
│   ├── mypage
│   ├── review
└── util
```

## 🐚 Convertion
[Convention Docs](https://github.com/EAT-SSU/EatSSU-Android/wiki/Convention)

## Framework
- Android Studio : Flamingo 2022.2.1 Patch 2
- JDK : 17
- minSDK : 21
- targetSDK : 34
