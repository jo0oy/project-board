# 게시판 서비스 project-board

Spring Boot와 관련 기술들, JPA, Thymeleaf etc. 로 구현한 게시판 서비스


## 요구사항 및 상세 기능 

- 인증 기능
    - 로그인
    - 로그아웃
- 회원 기능
    - 회원가입
    - 내 정보 조회
    - 내 정보 수정
- 게시글 기능
    - 게시글 작성
    - 게시글 수정
    - 게시글 삭제
    - 게시글 ‘좋아요’
    - 게시글 조회 - 조회수 증가
- 댓글 기능
    - 게시글에 댓글 작성
    - 댓글 삭제
    - 게시글에 해당하는 댓글 리스트 조회
- 게시판 기능
    - 전체 게시글 조회
    - 전체 게시글 검색 (작성자, 제목, 해시태그, 작성일(YYYY-MM)) 기능
    - 전체 게시글 페이징, 정렬(최신순, 오래된순, 제목 사전순, 조회수 높은순) 기능
- 해시태그 기능
    - 전체 해시태그 리스트 조회
    - 해시태그 관련 게시글 리스트 조회
    - 해시태그 검색 기능

## 개발환경

* IntelliJ Ultimate 2022.03
* Java 11
* Gradle 7.5
* Spring Boot 2.7.7

## 세부 기술 스택 

Spring Boot

* Spring Boot Actuator
* Spring Web
* Spring Data JPA
* Thymeleaf
* Spring Security
* H2 Database
* MariaDB Driver
* Lombok
* Spring Boot DevTools
* Spring Validation
* JUnit5

그 외 

* Summernote Editor
* Querydsl
* Mapstruct
* AWS S3
* Redis
* Testcontainers

## ERD
![diagram_1](./project-board-entity-diagram.png)

![diagram_2](./project-board-erd.png)

## Feature List

* `Spring Data JPA`를 이용한 CRUD 구현
* `Querydsl`을 이용한 동적 쿼리 작성 (다양한 검색 조건 + 페이징/정렬 조건 구현)
* 페치조인, defalut_batch_fetch_size 설정 등을 사용한 쿼리 최적화 
* `Spring Security`를 이용한 인증/인가 보안 로직 구현 
* `Spring Validation`을 이용한 입력 데이터 검증 및 에러 메세지 설정 
*  게시글 작성 페이지에 `Summernote Editor` 적용 및 이미지 파일 업로드 로직 구현
* `AWS S3`를 이용한 게시글 첨부 파일 스토리지 구축 및 업로드/다운로드/삭제 로직 구현 
* `Redis`를 이용한 성능 최적화: 로그인, 수정/삭제 권한 확인 시 중복되는 User 조회 --> User 정보 캐싱
* `MapStruct`를 이용해 데이터간 Mapping 코드 간소화 
* `Cookie`를 이용한 조회수 기능 추가 (중복 조회수 증가 방지)
* `Testcontainers`를 이용해 독립적인 테스트 환경 구축: Redis/MariaDB
* `JUnit5` + `AssertJ`를 이용한 테스트 코드 작성 
* GitHub에 프로젝트 릴리즈 
* `Heroku`를 통해 애플리케이션 배포 


## Results

### 데모 페이지 

*  https://project-board-service.herokuapp.com/
