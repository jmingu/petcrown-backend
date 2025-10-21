# PetCrown Backend 개발 가이드

User 패키지를 기준으로 한 표준 개발 패턴과 규칙을 정의합니다.

## 📁 패키지 구조

```
api/src/main/java/kr/co/api/{domain}/
├── controller/           # REST 컨트롤러
├── converter/           # 객체 변환 담당
│   ├── domainEntity/    # 도메인 ↔ 엔티티 변환
│   ├── dtoDomain/       # DTO ↔ 도메인 변환
│   ├── entityCommand/   # 엔티티 ↔ 커맨드 변환
│   └── dtoCommand/      # DTO ↔ 커맨드 양방향 변환 (요청→커맨드, 커맨드→응답)
├── domain/             # 도메인 모델 (DDD)
│   ├── model/          # 도메인 엔티티
│   └── vo/             # 값 객체 (Value Objects)
├── dto/                # 데이터 전송 객체
│   ├── command/        # 서비스 레이어용 커맨드 DTO
│   ├── request/        # HTTP 요청 DTO
│   └── response/       # HTTP 응답 DTO
├── mapper/             # MyBatis 매퍼 인터페이스
└── service/            # 비즈니스 로직 서비스
```

## 🔄 데이터 흐름 패턴

### Controller → Service 데이터 전달 규칙

#### 3개 이하 파라미터: 직접 전달
```java
// ✅ Good: 2개 파라미터
@GetMapping("/v1/check-email")
public ResponseEntity<CommonResponseDto> checkEmailDuplicate(@RequestParam String email) {
    userService.checkEmailDuplicate(email);
    return success();
}

// ✅ Good: 3개 파라미터
public ResponseEntity<CommonResponseDto> verifyEmailCode(@RequestBody EmailVerificationRequestDto request) {
    userService.verifyEmailCode(request.getEmail(), request.getCode());
    return success();
}
```

#### 3개 초과 파라미터: Command DTO 사용
```java
// ✅ Good: RequestDto → CommandDto 변환 사용
@PostMapping("/v1")
public ResponseEntity<CommonResponseDto> createUser(@RequestBody UserRegistrationRequestDto request) {
    // RequestDto → CommandDto 변환 (입력 변환)
    UserRegistrationDto userRegistrationDto = userDtoCommandConverter.toCommandDto(request);
    userService.createUser(userRegistrationDto);
    return success();
}

// ✅ Good: Service 응답 → ResponseDto 변환 사용
@PostMapping("/v1/login")
public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto request) throws Exception {
    LoginTokenDto login = userService.login(request.getEmail(), request.getPassword());
    // CommandDto → ResponseDto 변환 (출력 변환)
    LoginResponseDto responseDto = userDtoCommandConverter.toResponseDto(login);
    return success(responseDto);
}
```

## 🔄 Converter 패턴

### 1. DtoCommand Converter
**역할**: HTTP DTO ↔ Service 레이어 Command DTO (양방향 변환)
```java
@Component
public class UserDtoCommandConverter {

    // Request → Command 변환 (입력)
    public UserRegistrationDto toCommandDto(UserRegistrationRequestDto request) {
        return new UserRegistrationDto(
            request.getEmail(),
            request.getName(),
            // ... 기타 필드
        );
    }

    // Command → Response 변환 (출력)
    public LoginResponseDto toResponseDto(LoginTokenDto loginTokenDto) {
        return new LoginResponseDto(
            loginTokenDto.getAccessToken(),
            loginTokenDto.getRefreshToken()
        );
    }
}
```

### 2. DtoDomain Converter
**역할**: Command DTO ↔ 도메인 객체
```java
@Component
public class UserDtoDomainConverter {

    public User toUserForRegistration(UserRegistrationDto dto) {
        return User.createUserByEmail(
            dto.getEmail(),
            dto.getName(),
            dto.getNickname(),
            // ... 기타 필드
        );
    }
}
```

### 3. DomainEntity Converter
**역할**: 도메인 객체 ↔ 엔티티
```java
@Component
public class UserDomainEntityConverter {

    // 도메인 → 엔티티 변환
    public UserEntity toUserEntityForRegistration(User user, RoleEntity roleEntity, LoginTypeEntity loginTypeEntity, CompanyEntity companyEntity) {
        return new UserEntity(
            user.getUserId(),
            user.getEmail().getValue(),
            user.getUserUuid(),
            // ... 기타 필드
        );
    }

    // 엔티티 → 도메인 변환
    public User toUserDomain(UserEntity entity) {
        return User.getUserAllFiled(
            entity.getUserId(),
            Email.of(entity.getEmail()),
            entity.getUserUuid(),
            // ... 기타 필드
        );
    }
}
```

### 4. EntityCommand Converter
**역할**: 엔티티 ↔ Command DTO (조회/응답용)
```java
@Component
public class UserEntityCommandConverter {

    public UserInfoDto toUserInfoDto(UserEntity userEntity) {
        return new UserInfoDto(
            userEntity.getUserId(),
            userEntity.getEmail(),
            // ... 기타 필드
        );
    }
}
```

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

## 🛠️ Service 레이어 패턴

### 서비스 메서드 구조
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Transactional
    public void createUser(UserRegistrationDto userRegistrationDto) {

        // 1. CommandDto → Domain 변환
        User user = userDtoDomainConverter.toUserForRegistration(userRegistrationDto);

        // 2. 비즈니스 규칙 검증
        validateUserForRegistration(user);

        // 3. 외부 의존성 조회 (기본값, 참조 데이터 등)
        RoleEntity defaultRole = roleMapper.selectDefaultRole()
                .orElseThrow(() -> new PetCrownException(MISSING_REQUIRED_VALUE));

        // 4. Domain → Entity 변환
        UserEntity userEntity = userDomainEntityConverter.toUserEntityForRegistration(
            user, defaultRole, defaultLoginType, defaultCompany);

        // 5. 영속성 저장
        userMapper.insertUser(userEntity);

        // 6. 후속 처리 (이메일 발송, 알림 등)
        // ...
    }
}
```
- @Transactional(readOnly = true) 상단 선언
- insert, update는 메서드단에 @Transactional 재선언

## 🗃️ 데이터베이스 접근 패턴

### MyBatis Mapper 사용
```java
@Mapper
public interface UserMapper {

    void insertUser(UserEntity userEntity);

    UserEntity selectByEmail(String email);

    UserEntity selectByUserId(Long userId);

    void updateUserInfo(UserUpdateDto userUpdateDto);
}
```
- Entity, commandDto 사용, 3개이하는 그냥 파라미터 사용

### Entity 설계 원칙 (중요)

#### Entity는 DB 테이블과 1:1 매칭
- **Entity는 반드시 DB 테이블 구조와 정확히 일치해야 한다**
- **Claude는 Entity 파일을 절대 수정하지 않는다**
  - Entity는 개발자가 수동으로 DB 컬럼과 동일하게 작성
  - 필드 추가, 수정, 삭제 금지
- **JOIN용 필드를 Entity에 추가하지 않는다**

  ```java
  // ❌ Bad: JOIN 결과를 Entity에 추가
  public class CommunityPostEntity {
      private Long postId;
      private Long userId;
      private String userName;  // ❌ community_post 테이블에 없는 컬럼
  }

  // ✅ Good: DB 테이블과 정확히 일치
  public class CommunityPostEntity {
      private Long postId;
      private Long userId;
      // userName은 없음 (테이블에 없으므로)
  }
  ```

#### JOIN 결과 처리 방법
- **조회 전용 DTO를 생성하여 사용한다**

  ```xml
  <!-- ❌ Bad: Entity에 없는 필드 매핑 시도 -->
  <select id="selectPost" resultType="kr.co.common.entity.community.CommunityPostEntity">
      SELECT cp.*, u.name
      FROM community_post cp
      LEFT JOIN "user" u ON cp.user_id = u.user_id
  </select>

  <!-- ✅ Good: 조회 전용 DTO 사용 -->
  <select id="selectPost" resultType="kr.co.common.entity.community.CommunityPostQueryDto">
      SELECT
          cp.post_id,
          cp.user_id,
          cp.category,
          cp.title,
          cp.content,
          u.name as user_name
      FROM community_post cp
      LEFT JOIN "user" u ON cp.user_id = u.user_id
  </select>
  ```

  ```java
  // common/src/main/java/kr/co/common/entity/community/CommunityPostQueryDto.java
  @Getter
  @AllArgsConstructor
  public class CommunityPostQueryDto {
      private Long postId;
      private Long userId;
      private String category;
      private String title;
      private String content;
      private String userName;  // ✅ JOIN 결과 필드
  }
  ```

#### Entity와 DB 컬럼 매핑 규칙
- Entity 필드명 = DB 컬럼명 (camelCase ↔ snake_case 자동 변환)
- Entity 필드와 DB 컬럼 예시:
  - `UserEntity.name` → `user.name`
  - `UserEntity.phoneNumber` → `user.phone_number`
  - `PetEntity.petName` → `pet.pet_name`

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

## 📋 개발 체크리스트

### 새로운 기능 개발 시 확인사항

#### 1. 패키지 구조 확인
- [ ] controller, service, domain, dto, mapper, converter 패키지 구조 준수
- [ ] converter 하위에 용도별 패키지 생성 (domainEntity, dtoDomain, dtoCommand, entityCommand)

#### 2. Controller 레이어
- [ ] 3개 초과 파라미터 시 Command DTO 사용
- [ ] DtoCommand Converter 사용 (Request→Command, Command→Response 양방향 변환)
- [ ] @AuthRequired 어노테이션 적절히 설정
- [ ] Swagger 어노테이션 추가

#### 3. Service 레이어
- [ ] @Transactional 적절히 설정 (readOnly, 전파 옵션 등)
- [ ] Command DTO → Domain 변환
- [ ] 비즈니스 로직은 Domain 객체에 캡슐화
- [ ] Domain → Entity 변환 후 저장

#### 4. Domain 레이어
- [ ] 불변 객체 설계 (final 필드)
- [ ] 정적 팩토리 메서드 사용
- [ ] Value Objects 적극 활용
- [ ] 비즈니스 규칙 도메인 내부에 구현

#### 5. Converter 레이어
- [ ] 각 Converter의 역할에 맞는 변환 로직 구현
- [ ] DtoCommand: DTO ↔ 커맨드 양방향 변환 (요청→커맨드, 커맨드→응답)
- [ ] DtoDomain: 커맨드 ↔ 도메인
- [ ] DomainEntity: 도메인 ↔ 엔티티
- [ ] EntityCommand: 엔티티 ↔ 커맨드 (조회용)
- [ ] null 체크 포함
- [ ] 단방향성 유지 (순환 참조 방지)

#### 6. DTO 설계
- [ ] Command DTO는 불변 객체로 설계
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