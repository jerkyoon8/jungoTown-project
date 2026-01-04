# 🌱 SpringCommunity Project

## 📖 프로젝트 소개
**SpringCommunity**는 Spring Boot를 기반으로 한 **커뮤니티 및 중고 거래 플랫폼**입니다.
사용자 간의 물품 거래, 실시간 채팅, 그리고 안전한 결제 시스템을 제공하며, OAuth2 소셜 로그인과 Redis를 활용한 세션 관리 등 최신 웹 애플리케이션 기술을 적용하여 개발되었습니다.

<br>

## 🛠️ Tech Stack

### Backend
<img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/MyBatis-C70D2C?style=for-the-badge&logo=mybatis&logoColor=white">

### Database & Cache
<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">

### Frontend
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"> <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vuedotjs&logoColor=white"> <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">

### Infrastructure & Tools
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">

<br>

## 📝 주요 기능 (Features)

### 1. 사용자 (User)
*   **회원가입/로그인**: 일반 이메일 가입 및 **Google OAuth 2.0** 소셜 로그인 지원.
*   **마이페이지**: 내 정보 수정, 프로필 이미지 관리.
*   **권한 관리**: 일반 사용자(USER)와 관리자(ADMIN) 권한 분리.

### 2. 상품 (Product)
*   **상품 등록/수정/삭제**: 이미지 업로드(다중 파일) 지원.
*   **상품 목록 및 검색**: 카테고리별 조회, 키워드 검색, 페이징 처리.
*   **상세 보기**: 최근 본 상품 기능(Cookie/Session), 조회수 증가.
*   **찜하기(Wishlist)**: 관심 상품 등록 및 관리.

### 3. 주문 및 결제 (Order & Payment)
*   **주문 생성**: 상품 구매 프로세스 구현.
*   **결제 연동**: **PortOne (구 Iamport) V2 API**를 활용한 실시간 결제 및 검증.
*   **결제 검증**: 클라이언트-서버 크로스 체크를 통한 결제 무결성 확보.

### 4. 실시간 채팅 (Chat)
*   **1:1 채팅**: 구매자와 판매자 간의 실시간 대화 기능.
*   **WebSocket**: `WebSocket`을 활용한 실시간 메시지 전송.
*   **채팅방 관리**: 상품별 채팅방 생성 및 목록 조회.

<br>

## 💾 ERD (Entity Relationship Diagram)
> *프로젝트의 데이터베이스 구조입니다.*

*(여기에 ERD 이미지를 추가하세요. 예: `! [ERD](./docs/erd.png)`)*
*   **Users**: 사용자 정보
*   **Products**: 상품 정보 (Category FK)
*   **Orders**: 주문 정보 (User FK, Product FK)
*   **Payments**: 결제 정보 (Order FK)
*   **ChatRooms**: 채팅방 (Product FK, Buyer FK, Seller FK)
*   **ChatMessages**: 채팅 메시지 (Room FK, Sender FK)

<br>

## 📂 디렉토리 구조 (Directory Structure)
```
src/main/java/com/juwon/springcommunity
├── config          # 설정 파일 (Security, Web, WebSocket, Redis 등)
├── controller      # 컨트롤러 (Web Layer)
├── domain          # 도메인 엔티티 (Model)
├── dto             # 데이터 전송 객체 (DTO)
├── repository      # 데이터 접근 계층 (MyBatis Mapper Interface)
├── service         # 비즈니스 로직 (Service Layer)
└── util            # 유틸리티 클래스
```

<br>

## 🚀 트러블슈팅 (Troubleshooting)

### 1. 결제 검증 시 UUID 처리 문제
*   **문제**: PortOne 결제 후 `orderId`로 검증할 때, DB의 PK(Long)가 아닌 UUID(`orderUid`)가 전달되어 `NumberFormatException` 발생.
*   **해결**: `PaymentService`에서 `findById` 대신 `findByOrderUid` 메서드를 구현하여 UUID 문자열로 주문을 조회하도록 수정함.

### 2. Redis 세션 동기화
*   **문제**: 다중 서버 환경(혹은 재시작 시) 세션 유지를 위해 메모리 기반 세션의 한계.
*   **해결**: `spring-boot-starter-data-redis`를 도입하여 세션 저장소를 Redis로 외부화함.

<br>

## ⚙️ 실행 방법 (How to run)

**1. 레포지토리 클론**
```bash
git clone https://github.com/your-username/springcommunity.git
```

**2. 설정 파일 수정 (`application.properties` / `application-secret.properties`)**
*   MySQL, Redis 설정 및 OAuth2, PortOne API 키를 본인의 환경에 맞게 수정해야 합니다.

**3. 빌드 및 실행**
```bash
./gradlew bootRun
```

<br>

## 👨‍💻 프로젝트 기간 및 참여
*   **기간**: 202X.XX.XX ~ 202X.XX.XX
*   **개인 프로젝트**
