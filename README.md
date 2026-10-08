## 상품 관리 CRUD API

Spring Boot로 구현한 상품 등록, 조회, 수정, 삭제 서버이다.

HTTP 메소드 별 API, 요청로그 MiddleWare, 다양한 응답코드 구현하였다.

## 실행 방법

1. 프로젝트 다운.
2. main() 실행.
3. PostMan에서 기본 주소 http://localhost:8080/api/v1/items 를 요청.

## 구현 API

1. 전체 GET
2. 하나만 GET
3. 하나만 POST
4. 여러개 POST
5. 상품 정보 PUT
6. 가격만 PUT
7. 하나만 DELETE
8. 전부 DELETE

### 요청 예시

POST·PUT 요청은 `Content-Type: application/json`으로 전송합니다.

Postman에서는 Body → raw → JSON을 선택합니다.

### 상품 등록 및 이름·가격 수정

{
  "name": "연필",
  "price": 1000
}


### 여러 상품 등록

[
    {
        "name": "지우개",
        "price": 500
    },
    {
        "name": "공책",
        "price": 2000
    }
]

### 가격만 수정
{
    "price": 3000
}

## 응답 형식

1. 단건 조회·등록·수정: 상품 객체 반환
2. 전체 조회·여러 상품 등록: 상품 객체 배열 반환
3. 삭제 성공: 204 No Content, 응답 본문 없음
4. 직접 구현한 오류 응답: statusCode와 message로 통일

### 성공 응답 시
{
    "id": 1,
    "name": "연필",
    "price": 1000
}

### 실패 응답 시

{
    "statusCode": 404,
    "message": "Not Found"
}

## HTTP 응답 코드

1. 200 Ok
2. 201 Created
3. 204 No Content
4. 400 Bad Request
5. 404 Not Found
6. 500 Internal Server Error
7. 503 Service Unavailable

## 미들 웨어
LoggingInterceptor로 요청 처리 전후에 로그를 출력한다.

### 미들웨어 예시

요청시작: POST /api/v1/items

요청종료 status = 201

## 테스트 결과
1. CRUD API 8개 정상 동작
2. 200·201·204 응답
3. 잘못된 가격 입력 시 400 응답
4. 존재하지 않는 상품 조회 시 404 응답
5. 테스트용 API의 500·503 응답
6. 전체 삭제 후 빈 목록 확인
7. 요청 시작·종료 로그 출력