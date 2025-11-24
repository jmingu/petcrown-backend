# PetCrown Backend 개발 가이드

User 패키지를 기준으로 한 표준 개발 패턴과 규칙을 정의합니다.

## 📁 패키지 구조

```
api/src/main/java/kr/co/api/{domain}/
├── controller/           # REST 컨트롤러
├── domain/             # 도메인 모델 (DDD)
│   ├── model/          # 도메인 엔티티
│   └── vo/             # 값 객체 (Value Objects)
├── dto/                # 데이터 전송 객체
│   ├── command/        # 서비스 레이어용 내부 DTO
│   ├── request/        # HTTP 요청 DTO
│   └── response/       # HTTP 응답 DTO
├── repository/         # JOOQ 레포지토리 인터페이스
└── service/            # 비즈니스 로직 서비스
```

## 🔄 데이터 흐름 패턴 (JOOQ 기반)

### 전체 흐름

```
Client → RequestDto → Controller → 내부용 Dto → Service (Domain 생성/비즈니스 로직)
→ Repository (Domain 저장/조회) → 내부용 Dto → Service → ResponseDto → Controller → Client
```

### Controller 레이어

**역할**: 클라이언트 요청/응답 처리, DTO 변환

```java
// ✅ Good: RequestDto → 내부용 Dto → Service → 내부용 Dto → ResponseDto
@PostMapping("/v1")
public ResponseEntity<CommonResponseDto> createUser(@RequestBody UserRegistrationRequestDto request) {
    // 1. RequestDto → 내부용 Dto (생성자 직접 호출)
    UserRegistrationDto userRegistrationDto = new UserRegistrationDto(
        request.getEmail(),
        request.getName(),
        request.getNickname(),
        request.getPassword(),
        request.getPasswordCheck()
    );

    // 2. Service 호출
    userService.createUser(userRegistrationDto);

    return success();
}

// ✅ Good: Service에서 내부용 Dto 받아서 ResponseDto로 변환
@PostMapping("/v1/login")
public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto request) throws Exception {
    // 1. Service에서 내부용 Dto 반환
    LoginTokenDto loginTokenDto = userService.login(request.getEmail(), request.getPassword());

    // 2. 내부용 Dto → ResponseDto 변환 (생성자 직접 호출)
    LoginResponseDto responseDto = new LoginResponseDto(
        loginTokenDto.getAccessToken(),
        loginTokenDto.getRefreshToken()
    );

    return success(responseDto);
}
```

**Controller 규칙**:
- 파라미터 3개 이하: 직접 전달
- 파라미터 3개 초과: 내부용 Dto 사용
- RequestDto → 내부용 Dto 변환은 Controller에서 처리
- 내부용 Dto → ResponseDto 변환은 Controller에서 처리

## 🏗️ Domain Driven Design (DDD) 패턴

### 도메인 객체 설계

#### 1. 도메인 엔티티 (Aggregate Root)
```java
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private final Long userId;
    private final Email email;          // Value Object 사용
    private final UserName name;        // Value Object 사용
    private final Nickname nickname;    // Value Object 사용

    // 정적 팩토리 메서드
    public static User createUserByEmail(String emailValue, String nameValue, ...) {
        // Value Objects 생성 (유효성 검증 포함)
        Email email = Email.of(emailValue);
        UserName name = UserName.of(nameValue);

        return new User(null, email, uuid, name, ...);
    }

    // ID로만 생성 (최소 정보)
    public static User ofId(Long userId) {
        if (userId == null) {
            throw new PetCrownException(BusinessCode.MISSING_REQUIRED_VALUE);
        }
        return new User(userId, null, null, ...);
    }

    // 비즈니스 로직
    public void validateEmailVerified() {
        if (!"Y".equals(this.isEmailVerified)) {
            throw new PetCrownException(BusinessCode.EMAIL_NOT_VERIFIED);
        }
    }
}
```

#### 2. Value Objects (VO)
```java
@Getter
public class Email {

    private final String value;

    private Email(String email) {
        ValidationUtils.validateEmail(email);  // 생성 시 유효성 검증
        this.value = email;
    }

    public static Email of(String email) {
        return new Email(email);
    }
}
```

### 주요 원칙

1. **불변 객체**: 모든 필드는 `final`로 선언
2. **정적 팩토리 메서드**: 생성자 대신 의미있는 이름의 정적 메서드 사용
3. **Value Objects**: 원시 타입 대신 VO 사용으로 타입 안정성 확보
4. **비즈니스 로직 캡슐화**: 도메인 객체 내부에 비즈니스 규칙 구현

## 📝 DTO 설계 원칙

### Command DTO 특징
```java
@Getter
@AllArgsConstructor
public class UserRegistrationDto {

    private final String email;
    private final String name;
    private final String nickname;
    private final String password;
    private final String passwordCheck;
    private final String phoneNumber;
    private final LocalDate birthDate;
    private final String gender;
}
```

- 서비스 레이어에서 사용하는 내부 DTO
- **불변 객체**: 모든 필드는 `final`로 선언 

## 🛠️ Service 레이어 패턴 (JOOQ 기반)

**역할**: 도메인 생성, 비즈니스 로직 담당

### 서비스 메서드 구조
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createUser(UserRegistrationDto userRegistrationDto) {

        // 1. 내부용 Dto → Domain 변환 (정적 팩토리 메서드 사용)
        User user = User.createUserByEmail(
            userRegistrationDto.getEmail(),
            userRegistrationDto.getName(),
            userRegistrationDto.getNickname(),
            userRegistrationDto.getPassword(),
            userRegistrationDto.getPasswordCheck()
        );

        // 2. 비즈니스 규칙 검증 (도메인 객체에 위임)
        user.validateForRegistration();

        // 3. Repository를 통해 도메인 저장 (Repository가 JOOQ Record로 변환)
        userRepository.save(user);

        // 4. 후속 처리 (이메일 발송, 알림 등)
        // ...
    }

    public UserDetailDto getUserById(Long userId) {
        // Repository에서 내부용 Dto 또는 Domain 반환
        return userRepository.findById(userId)
                .orElseThrow(() -> new PetCrownException(USER_NOT_FOUND));
    }
}
```

**Service 규칙**:
- @Transactional(readOnly = true) 클래스 상단 선언
- insert, update는 메서드단에 @Transactional 재선언
- 도메인 생성 및 비즈니스 로직 담당
- Repository를 통해 영속성 처리
- 내부용 Dto 또는 Domain으로 데이터 주고받기

## 🗃️ Repository 레이어 패턴 (JOOQ 기반)

**역할**: 데이터베이스 접근, 도메인 ↔ JOOQ Record 변환

### Repository 인터페이스
```java
public interface UserRepository {

    // 등록 - 도메인으로 파라미터 받기
    void save(User user);

    // 수정 - 도메인, 파라미터, 내부용 Dto로 파라미터 받기
    void update(User user);
    void update(Long userId, String name, String nickname);
    void update(UserListDto userDto);

    // 조회 - 내부용 Dto 또는 Domain 반환
    Optional<UserDetailDto> findById(Long userId);

    Optional<User> findByEmail(String email);

    List<UserListDto> findAll();

    // 삭제 - 기본 타입 사용
    void deleteById(Long userId);
}
```

### Repository 구현 예시
```java
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final DSLContext dsl;

    @Override
    public void save(User user) {
        // Domain → JOOQ Record 변환
        UserRecord record = dsl.newRecord(USER);
        record.setEmail(user.getEmail().getValue());
        record.setName(user.getName().getValue());
        record.setNickname(user.getNickname().getValue());
        record.setPassword(user.getPassword().getValue());
        // ... 기타 필드

        record.store();  // INSERT
    }

    @Override
    public void update(User user) {
        // Domain → JOOQ Record 변환 및 UPDATE
        dsl.update(USER)
            .set(USER.NAME, user.getName().getValue())
            .set(USER.NICKNAME, user.getNickname().getValue())
            .where(USER.USER_ID.eq(user.getUserId()))
            .execute();
    }

    @Override
    public Optional<UserDetailDto> findById(Long userId) {
        // JOOQ 조회 → 내부용 Dto 반환
        return dsl.select(
                USER.USER_ID,
                USER.EMAIL,
                USER.NAME,
                USER.NICKNAME
            )
            .from(USER)
            .where(USER.USER_ID.eq(userId))
            .fetchOptional(record -> new UserDetailDto(
                record.get(USER.USER_ID),
                record.get(USER.EMAIL),
                record.get(USER.NAME),
                record.get(USER.NICKNAME)
            ));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // JOOQ 조회 → Domain 반환
        return dsl.selectFrom(USER)
            .where(USER.EMAIL.eq(email))
            .fetchOptional(record -> User.of(
                record.getUserId(),
                record.getEmail(),
                record.getName(),
                record.getNickname()
            ));
    }
}
```

**Repository 규칙**:
- **등록/수정**: 도메인으로 파라미터 받기 (Repository 내부에서 JOOQ Record로 변환)
- **조회**: 내부용 Dto 또는 Domain으로 반환
- **삭제**: 기본 타입(Long, String 등) 사용
- JOOQ DSLContext를 활용한 타입 안전 쿼리
- JOIN이 필요한 경우 내부용 Dto로 반환

## 🚨 예외 처리 패턴

### Domain 레벨 검증
```java
// Value Object에서 생성 시 검증
private Email(String email) {
    ValidationUtils.validateEmail(email);  // 실패 시 PetCrownException 발생
    this.value = email;
}

// 도메인 객체에서 비즈니스 규칙 검증
public void validateEmailVerified() {
    if (!"Y".equals(this.isEmailVerified)) {
        throw new PetCrownException(BusinessCode.EMAIL_NOT_VERIFIED);
    }
}
```

## 📋 개발 체크리스트 (JOOQ 기반)

### 새로운 기능 개발 시 확인사항

#### 1. 패키지 구조 확인
- [ ] controller, service, domain, dto, repository 패키지 구조 준수

#### 2. Controller 레이어
- [ ] RequestDto → 내부용 Dto 변환 (생성자 직접 호출)
- [ ] 내부용 Dto → ResponseDto 변환 (생성자 직접 호출)
- [ ] 파라미터 3개 이하: 직접 전달, 3개 초과: 내부용 Dto 사용
- [ ] @AuthRequired 어노테이션 적절히 설정
- [ ] Swagger 어노테이션 추가

#### 3. Service 레이어
- [ ] @Transactional 적절히 설정 (readOnly, 전파 옵션 등)
- [ ] 내부용 Dto → Domain 변환은 정적 팩토리 메서드 사용
- [ ] 비즈니스 로직은 Domain 객체에 캡슐화
- [ ] Repository를 통해 도메인 저장/조회

#### 4. Repository 레이어
- [ ] 등록/수정: 도메인으로 파라미터 받기
- [ ] 조회: 내부용 Dto 또는 Domain 반환
- [ ] JOOQ DSLContext 활용한 타입 안전 쿼리
- [ ] Domain ↔ JOOQ Record 변환 로직 구현

#### 5. Domain 레이어
- [ ] 불변 객체 설계 (final 필드)
- [ ] 정적 팩토리 메서드 사용
- [ ] Value Objects 적극 활용
- [ ] 비즈니스 규칙 도메인 내부에 구현

#### 6. DTO 설계
- [ ] 내부용 Dto는 불변 객체로 설계
- [ ] Request/Response DTO는 HTTP 스펙에 맞게 설계
- [ ] 적절한 validation 어노테이션 추가
- [ ] **리스트 응답은 반드시 필드로 한 번 더 감싸서 반환** (확장성 확보)

## 📦 Response DTO 설계 원칙

### 리스트 응답은 항상 필드로 감싸기

**리스트를 직접 반환하지 않고, Response DTO로 한 번 더 감싸서 반환한다.**

#### ❌ Bad: 리스트 직접 반환
```java
@GetMapping("/v1/species")
public ResponseEntity<CommonResponseDto> getAllSpecies() {
    List<SpeciesDto> speciesList = petService.getAllSpecies();
    return success(speciesList);  // ❌ 직접 반환
}
```
```json
{
  "data": [
    {"speciesId": 1, "name": "강아지"},
    {"speciesId": 2, "name": "고양이"}
  ]
}
```

#### ✅ Good: Response DTO로 감싸서 반환
```java
// Response DTO 생성
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SpeciesListResponseDto {
    private List<SpeciesDto> species;  // 리스트를 필드로 감싸기
}

// Controller
@GetMapping("/v1/species")
public ResponseEntity<CommonResponseDto> getAllSpecies() {
    List<SpeciesDto> speciesList = petService.getAllSpecies();
    SpeciesListResponseDto response = new SpeciesListResponseDto(speciesList);
    return success(response);  // ✅ DTO로 감싸서 반환
}
```
```json
{
  "data": {
    "species": [
      {"speciesId": 1, "name": "강아지"},
      {"speciesId": 2, "name": "고양이"}
    ]
  }
}
```

### 리스트를 감싸야 하는 이유

1. **확장성**: 나중에 추가 필드를 넣기 쉬움
   ```java
   public class SpeciesListResponseDto {
       private List<SpeciesDto> species;
       private int totalCount;        // 추가 가능
       private LocalDate lastUpdated; // 추가 가능
   }
   ```

2. **일관성**: 모든 API 응답이 동일한 구조를 가짐
3. **명확성**: 응답 데이터의 의미가 명확함 (`species` 필드명으로 의미 전달)
4. **타입 안전성**: 클라이언트에서 타입 추론이 쉬움

### 네이밍 규칙

- 단일 항목 리스트: `{도메인}ListResponseDto`
  - 예: `SpeciesListResponseDto`, `BreedListResponseDto`, `VoteListResponseDto`
- 필드명: 복수형 사용
  - 예: `species`, `breeds`, `votes`, `ranking`

### 실제 예제

```java
// 1. DTO 생성
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BreedListResponseDto {
    private List<BreedDto> breeds;
}

// 2. Controller에서 사용
@GetMapping("/v1/breeds")
public ResponseEntity<CommonResponseDto> getBreedsBySpeciesId(@RequestParam Long speciesId) {
    List<BreedDto> breedList = petService.getBreedsBySpeciesId(speciesId);
    BreedListResponseDto response = new BreedListResponseDto(breedList);
    return success(response);
}

// 3. JSON 응답
{
  "data": {
    "breeds": [
      {"breedId": 1, "name": "골든 리트리버"},
      {"breedId": 2, "name": "시바견"}
    ]
  }
}
```


---

**이 가이드를 기준으로 모든 새로운 기능을 개발하고, 기존 코드도 점진적으로 이 패턴에 맞춰 리팩토링합니다.**