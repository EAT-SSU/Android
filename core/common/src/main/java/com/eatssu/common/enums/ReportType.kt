package com.eatssu.common.enums


enum class ReportType(val description: String) {
    NO_ASSOCIATE_CONTENT("메뉴와 관련없는 내용"),
    IMPROPER_CONTENT("음란성, 욕설 등 부적절한 내용"),
    IMPROPER_ADVERTISEMENT("부적절한 홍보 또는 광고"),
    COPY("리뷰 작성 취지에 맞지 않은 내용 (복사글 등)"),
    COPYRIGHT("저작권 도용 의심 (사진 등)"),
    EXTRA("기타 (하단 내용 작성)")
}