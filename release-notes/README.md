# Release Notes Guide

이 디렉토리에는 버전별 릴리즈 노트가 YAML 형식으로 저장됩니다.

## 파일 형식

```
v{버전}.yml
```

예시: `v3.2.0.yml`

## YAML 구조

```yaml
ko: |
  - 한국어 릴리즈 노트 항목 1
  - 한국어 릴리즈 노트 항목 2
en: |
  - English release note item 1
  - English release note item 2
```

## 작성 규칙

1. 각 항목은 `-`으로 시작
2. 한국어와 영어 모두 작성 필수
3. 개발자 용어 사용 금지, 사용자 친화적 언어 사용
4. 최소 10자 이상의 내용 작성
5. 빈 파일 또는 빈 내용 금지

## CI/CD 연동

- GitHub Actions가 자동으로 해당 버전의 YAML 파일을 찾습니다
- 한국어는 Google Play Store Korea, 영어는 Global에 업로드됩니다
- 파일이 없거나 내용이 부족하면 배포가 실패할 수 있습니다

## 예시

```yaml
# v3.2.0.yml
ko: |
  - 로그인 기능 개선으로 더 안정적인 로그인 경험 제공
  - 앱 충돌 문제 해결로 전반적인 안정성 강화
  - UI 개선으로 사용자 경험 향상
en: |
  - Improved login functionality for more stable experience
  - Fixed app crashes to enhance overall stability
  - UI improvements for better user experience
```