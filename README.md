# EatSSU-Android
숭실대에서 먹자, 잇슈!

| 유진<br/>([@HI-JIN2](https://github.com/HI-JIN2)) | 이현지<br/>([@Amepistheo](https://github.com/Amepistheo)) |
| :---: | :---: |
| <img width="250" src="https://avatars.githubusercontent.com/u/94737714?v=4"/> | <img width="250" src="https://avatars.githubusercontent.com/u/110108243?v=4"/> |
| `로그인` `리뷰`<br/>`마이 페이지`  | `메인 페이지`<br/>`신고하기` `공지사항`|

## package
### architecture
- MVVM architecture pattern으로 리팩토링 중에 있으며, 현재 부분적으로 적용되어 있습니다.  
- 이전 코드는 architecture pattern이 없는 코드 입니다. 
```
com.eatssu.android
├── adapter
├── base
├── data
│   ├── entity
│   ├── enums
│   ├── model
│   │   ├── request
│   │   ├── response
│   └── service
├── repository
├── view
│   ├── calendar
│   ├── infopage
│   ├── login
│   ├── main
│   ├── mypage
│   ├── review
└── viewmodel
```

## Framework
- Android Studio : 2022.2.1 Patch 2
- JAVA : 17
- minSDK : 21
- targetSDK : 34

## Infra
- Github Actions
- AWS S3
