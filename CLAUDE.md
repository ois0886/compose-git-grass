# compose-git-grass

GitHub contribution graph (grass) UI component library for Jetpack Compose.

## Project Structure

- **`:library`** - Published library module (`com.inseong:compose-git-grass`)
  - Package: `com.inseong.gitgrass`
  - Contains the `GitGrass` composable and related APIs
- **`:app`** - Sample/demo application
  - Package: `com.inseong.compose_git_grass`
  - Depends on `:library` for development/testing

## Build Commands

```bash
# Build library
./gradlew :library:assembleDebug

# Build sample app
./gradlew :app:assembleDebug

# Publish to local Maven (~/.m2)
./gradlew :library:publishToMavenLocal

# Publish to Maven Central
./gradlew :library:publishAndReleaseToMavenCentral
```

## Publishing

Uses [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin).

Maven coordinates: `io.github.ois0886:compose-git-grass:<version>`

Required credentials in `~/.gradle/gradle.properties`:
```properties
mavenCentralUsername=<Sonatype Central Portal username>
mavenCentralPassword=<Sonatype Central Portal password>
signing.keyId=<GPG key ID (last 8 chars)>
signing.password=<GPG key passphrase>
signing.secretKeyRingFile=<path to secring.gpg>
```

## Code Conventions

- Kotlin, Jetpack Compose
- Min SDK 26, Compile SDK 36
- Java 11 source/target compatibility
- Version catalog: `gradle/libs.versions.toml`

## Workflow Rules

### 1. Plan Mode First (필수)
- 모든 개발 작업 시작 전에 반드시 `/plan` 모드로 진입하여 구현 계획을 수립한다.
- 계획 단계에서: 영향 범위 파악, 파일 구조 분석, 구현 전략 결정, 테스트 전략 수립.
- 계획이 확정된 후에 코드 작성을 시작한다.

### 2. CLAUDE.md 우선 확인
- 작업 시작 전 반드시 `CLAUDE.md`를 읽고 프로젝트 컨벤션과 지침을 따른다.

### 3. 테스트 가능한 코드 작성 원칙
- **관심사 분리**: UI 로직과 비즈니스 로직을 분리한다. 계산/변환 로직은 순수 함수로 추출하여 단위 테스트가 가능하게 한다.
- **의존성 주입**: 외부 의존성은 파라미터로 주입받아 테스트 시 교체 가능하게 한다.
- **순수 함수 우선**: 부수 효과(side effect) 없는 순수 함수를 우선 사용한다. 입력 → 출력이 명확한 함수는 테스트가 쉽다.
- **작은 단위**: 하나의 함수/컴포저블은 하나의 책임만 가진다. 큰 함수는 테스트 가능한 작은 함수로 분리한다.
- **테스트 작성**: 새로운 기능 추가 시 유닛 테스트를 함께 작성한다. 기존 테스트가 깨지지 않는지 확인한다.

### 4. 작업 완료 시 커밋
- 모든 작업이 완료되면 반드시 git commit을 수행한다.
- 커밋 메시지는 한국어로, conventional commit 형식을 따른다 (예: `feat:`, `fix:`, `refactor:`, `docs:`).

### 커밋 메시지 형식

```
<type>: <한국어 설명>
```

**타입:**
- `feat:` 새로운 기능 추가
- `fix:` 버그 수정
- `refactor:` 코드 리팩토링 (기능 변경 없음)
- `docs:` 문서 수정
- `test:` 테스트 추가/수정
- `chore:` 빌드, 설정 등 기타 변경

**예시:**
```
feat: GitHub 잔디 그래프 컴포넌트 추가
fix: 날짜 계산 오류 수정
docs: CLAUDE.md 워크플로우 규칙 추가
```
