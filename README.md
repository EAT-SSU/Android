# EatSSU-Android
숭실대에서 먹자 잇슈!

## android convention

### layout id 규칙
ex)
@+id/tv_login  
@+id/et_password  
@+id/btn_login  

_view 이름_
  - TextView -> tv_
  - ImageView -> iv_
  - EditText -> et_
  - Button, ImageButton -> btn_ 
  - ConstraintLayout -> layout_
  - BottonNavitaionView -> bnv_

### layout 파일명

`activity/fragment_기능` ex) activity_login, fragment_home  
snake_case 적용한다.  
activity_sign_in.xml, fragment_sign_up.xml, item_user.xml (리사이클러뷰 아이템 레이아웃은 “item_”로 시작!)  

### drawable 파일명
역시 xml 이기 때문에 snake_case를 적용한다. 자세한 내용은 아래를 참고한다.  
icon은 “ic_” -> ic_error.xml  
image는 “img_” -> img_default_user.png  
shape는 “shape_” -> shape_border_radius10.xml  
selector는 “selector_” -> selector_edittext_background.xml  

### 아이콘 이름 규칙
`ic_기능_모양`  
1. 화살표 같은 경우는 ic_arrow_방향 ex) ic_back, ic_like  
2. 선택 여부가 있는 아이콘은 ic_기능_모양과 ic_기능_모양_selected 로 구분  
3. 색은 안넣어도 될 거 같습니다 왜냐 코드에서 tint로 변경 가능 각자 변경해서 쓰기 ....  



### 클래스 파일명
UpperCamelCase 적용  
MainActivity.kt, UserViewModel.kt, WriteFragment.kt, UserInfo.kt  
### 함수명
동사형태로 작성하고, lowerCamelCase 적용  
fun showList(), fun updateContacts()  
### 변수명
명사형태로 작성하고, lowerCamelCase 적용  
isEnd(Boolean 타입 제외), viewPagerAdapter  

### 더미데이터 이름 규칙
`recyclerview 이름_숫자` ex)post_1, post_2


### 코드 주석 규칙 (kt 파일에 사용)

1. 코드 위에 어떤 기능인지 설명 ex) 파이어베이스 연결, 좋아요 기능  
2. 공통적으로 사용하는 변수를 제외한 애들은 선언 옆에 // 이 주석을 사용해서 설명해주기  
3. 화면 전환 시 어느 화면에서 어느 화면으로 넘어가는지 설명  
4. 자세하게 써주기  


## Git convention

1. **Github Flow**
1. 작은 기능 하나 구현 할 때 마다 커밋하기  
2. issue는 큰 기능이나 한 화면 기준으로 큰 단위의 작업
3. PR은 작업 분량으로 정상작동 내용이 있다면 머지하는 식으로 작은 단위의 작업
4. 커밋 하나라도 했으면 PR 바로 하기

### Branch Convention
`feat/{구현 기능}` ex) feat/review, feat/login, feat/main ...

브랜치를 새로 생성할 때에는 main 브랜치를 기점으로 생성하기

### Commit Message Convention
`작업유형: 작업내용 #이슈번호` ex) add: login 파일 추가 #20

- 파일 추가 : add
- 버그 수정 : fix
- 리팩터링 : refactoring
- 파일 삭제 : remove
- 기능 추가 : feat
- 문서 수정 : docs
- 주석 추가 : comment  

작은 기능 하나 구현 할 때 마다 커밋하기


### ISSUE Convention
`[작업유형] 작업내용`
ex) [FEAT] 리뷰 목록 

### PR Convention
`[작업유형] 작업내용`
ex) [FEAT] 리뷰 쓰기 구현 완료  

