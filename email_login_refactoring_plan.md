# 사용자 인증 'username' -> 'email' 전환 리팩토링 계획

## 목표
- 기존의 `username` 기반 로그인 및 사용자 관리 체계를 `email` 기반으로 완전히 전환합니다.
- 코드 전체의 불일치 문제를 해결하고, `email`을 사용자의 유일한 식별자로 사용하도록 통일합니다.

---

## 단계별 진행 순서

### 1단계: `User.java` 핵심 수정
- **작업 내용**: `User` 클래스에 `UserDetails` 인터페이스의 `getUsername()` 메소드를 추가하고, 이 메소드가 `email` 필드 값을 반환하도록 구현합니다.
- **이유**: Spring Security는 `getUsername()`가 반환하는 값으로 사용자를 식별합니다. 이 부분을 수정하면, 시스템 전반에서 사용자의 ID가 `email`로 인식되게 만드는 가장 핵심적인 변경입니다.

### 2단계: `UserService.java` 전체 리팩토링
- **작업 내용**:
    1. `loadUserByUsername(String email)` 메소드가 `userRepository.findByEmail(email)`을 호출하도록 수정합니다. (파라미터 이름은 인터페이스 규약상 `username`이지만, 실제로는 `email`이 전달됩니다.)
    2. 기존의 `findUserByUsername`, `isUsernameDuplicate` 등 `username`을 사용하던 모든 메소드를 `findUserByEmail`, `isEmailDuplicate` 등으로 변경하거나 삭제합니다.
    3. 회원가입(`createUser`), 정보 수정(`updateUser`) 로직에서 `username` 관련 코드를 제거합니다.

### 3단계: `UserRepository` 및 Mapper(.xml) 정리
- **작업 내용**: `UserService` 변경에 따라 더 이상 사용되지 않는 `username` 관련 조회 메소드나 SQL 쿼리를 `UserRepository.java`와 `UserRepository.xml`에서 삭제하거나 수정합니다.
- **이유**: 불필요한 코드를 제거하여 혼란을 줄이고 유지보수성을 높입니다.

### 4단계: DTO 및 화면(HTML) 수정
- **작업 내용**:
    1. 회원가입, 정보 수정 시 사용되는 DTO(`UserCreateRequestDto`, `UserUpdateRequestDto`)에서 `username` 필드를 제거합니다.
    2. 회원가입 및 정보 수정 화면(`createUserForm.html`, `updateUserForm.html`)에서 `username` 입력 필드를 완전히 제거합니다.
- **이유**: 사용자로부터 `username`을 입력받지 않도록 하여 `email` 중심의 정책을 일관되게 유지합니다.

### 5단계: 최종 확인
- **작업 내용**: 모든 변경이 완료된 후, 다음 기능들이 정상적으로 동작하는지 확인합니다.
    - 신규 회원가입 (email, nickname, password 사용)
    - 이메일과 비밀번호를 사용한 일반 로그인
    - 구글 소셜 로그인
    - 회원 정보 수정
