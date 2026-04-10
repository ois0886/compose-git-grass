# compose-git-grass

GitHub contribution graph (grass) UI component library for Jetpack Compose.

## Project Structure

- **`:library`** - Published library module (`io.github.ois0886:compose-git-grass`)
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

# Run library tests
./gradlew :library:test

# Generate code coverage report (JaCoCo)
./gradlew :library:jacocoTestReport
# → library/build/reports/jacoco/jacocoTestReport/html/

# Verify coverage threshold (80% minimum)
./gradlew :library:jacocoCoverageVerification

# Verify README/CHANGELOG version sync with library version
./scripts/verify-doc-sync.sh

# Run Android Lint
./gradlew :library:lint

# Generate API documentation (Dokka)
./gradlew :library:dokkaGenerate
# → library/build/dokka/html/

# Publish to local Maven (~/.m2)
./gradlew :library:publishToMavenLocal

# Publish to Maven Central
./gradlew :library:publishAndReleaseToMavenCentral
```

## Publishing

Uses [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin).

Maven coordinates: `io.github.ois0886:compose-git-grass:<version>`

### 로컬 배포 (수동)

Required credentials in `~/.gradle/gradle.properties`:
```properties
mavenCentralUsername=<Sonatype Central Portal username>
mavenCentralPassword=<Sonatype Central Portal password>
signing.keyId=<GPG key ID (last 8 chars)>
signing.password=<GPG key passphrase>
signing.secretKeyRingFile=<path to secring.gpg>
```

### 자동 배포 (CI)

`v*` 태그 push 시 `.github/workflows/release.yml`이 자동 실행:
1. 태그 버전과 `library/build.gradle.kts` 버전 일치 검증
2. 테스트 실행 → 커버리지 리포트 → API 문서 생성
3. Maven Central 배포
4. GitHub Release 생성 (AAR 첨부)

**필요한 GitHub Secrets:**
- `MAVEN_CENTRAL_USERNAME` / `MAVEN_CENTRAL_PASSWORD`
- `SIGNING_KEY` (ASCII-armored GPG private key)
- `SIGNING_KEY_ID` (GPG key ID 마지막 8자)
- `SIGNING_KEY_PASSWORD` (GPG passphrase)

**릴리즈 절차:**
```bash
# 1. library/build.gradle.kts에서 버전 업데이트
# 2. CHANGELOG.md 업데이트
# 3. 커밋 후 태그 생성 및 push
git tag v1.1.0
git push origin v1.1.0
```

## Code Conventions

- Kotlin, Jetpack Compose
- Min SDK 26, Compile SDK 36
- Java 11 source/target compatibility (CI: JDK 17)
- Version catalog: `gradle/libs.versions.toml`

## CI/CD

### CI (`.github/workflows/ci.yml`)
- **트리거**: `main` branch push / PR
- **병렬 잡 구조**:
  - `docs-sync`: README/CHANGELOG 버전 동기화 검증 (`scripts/verify-doc-sync.sh`)
  - `quality`: 테스트 → 커버리지 리포트/임계값 검증 → Android Lint
  - `assemble`: 라이브러리/샘플앱 빌드
  - `ui-test`: Compose UI 인스트루먼트 테스트(`connectedDebugAndroidTest`)
  - `build`: 위 잡 완료 여부를 집계하는 게이트 잡
- 커버리지 리포트 및 Lint 리포트는 GitHub Actions artifact로 업로드

### Release (`.github/workflows/release.yml`)
- **트리거**: `v*` 태그 push
- **스텝**: 버전 검증 → 테스트 → 커버리지 → Dokka 문서 → Maven Central 배포 → GitHub Release

## 품질 도구

- **JaCoCo**: 코드 커버리지 측정 (`./gradlew :library:jacocoTestReport`) + 임계값 검증 80% (`./gradlew :library:jacocoCoverageVerification`)
- **Android Lint**: 정적 분석 (`./gradlew :library:lint`)
- **Dokka 2.0**: API 문서 생성 (`./gradlew :library:dokkaGenerate`)
- **ProGuard Consumer Rules**: `library/consumer-rules.pro`에 public API 보호 규칙 정의
- **성능 벤치마크**: `GridBenchmarkTest.kt`에서 대량 데이터(1000~3650일) 성능 검증
- **CHANGELOG**: `CHANGELOG.md`에 Keep a Changelog 형식으로 변경 이력 관리

## 라이브러리 소스 구조

- `library/src/main/java/com/inseong/gitgrass/`
  - `GitGrass.kt` — 메인 컴포저블 (public API)
  - `GitGrassColors.kt` — 색상 스킴 데이터 클래스
  - `GitGrassComponents.kt` — 내부 UI 컴포넌트 (YearLabel, MonthRow, WeekLabelColumn, GrassGridContent, GrassWeekColumn, GrassCell, StreakSummary, ColorLegend)
  - `GitGrassDefaults.kt` — 기본값 및 팩토리 (색상, 라벨, 크기, 로케일, 레이아웃 상수)
  - `GridUtils.kt` — 순수 함수 유틸리티 (normalizeDateRange, normalizeContributions, generateDayList, buildGrid, dayIndexInWeek, weekDaysOrdered, createMonthLabels, formatYearLabel, calculateStreak)
  - `TypeAliases.kt` — 내부 타입 별칭 (ContributionData, Grid, MonthPositions)

## 테스트 구조

- `library/src/test/` — JUnit 4 유닛 테스트
  - `GridUtilsTest.kt` — 그리드 생성, 날짜 처리, streak 계산, 입력 정규화, 주 시작일, 월 경계
  - `GitGrassDefaultsTest.kt` — startDate, endDate, levelThresholds, 로케일 라벨, 색상
  - `LevelToColorTest.kt` — 색상 매핑 (8개)
  - `GridBenchmarkTest.kt` — 성능 벤치마크 (4개)
- `library/src/androidTest/` — Compose UI 인스트루먼트 테스트
  - `GrassCellTest.kt` — 셀 클릭/롱클릭 콜백 검증
  - `LabelRenderingTest.kt` — 월 라벨, 주 라벨 렌더링 검증

## Code Quality

코드 퀄리티 가이드라인은 [CODE_QUALITY.md](./CODE_QUALITY.md)를 따른다.
모든 코드 작성 및 리뷰 시 해당 문서의 규칙을 준수한다.

## Workflow Rules

### 1. Plan Mode First (필수)
- 모든 개발 작업 시작 전에 반드시 `/plan` 모드로 진입하여 구현 계획을 수립한다.
- 계획 단계에서: 영향 범위 파악, 파일 구조 분석, 구현 전략 결정, 테스트 전략 수립.
- 계획이 확정된 후에 코드 작성을 시작한다.

### 2. AGENTS.md 우선 확인
- 작업 시작 전 반드시 `AGENTS.md`를 읽고 프로젝트 컨벤션과 지침을 따른다.

### 3. 테스트 가능한 코드 작성 원칙
- **관심사 분리**: UI 로직과 비즈니스 로직을 분리한다. 계산/변환 로직은 순수 함수로 추출하여 단위 테스트가 가능하게 한다.
- **의존성 주입**: 외부 의존성은 파라미터로 주입받아 테스트 시 교체 가능하게 한다.
- **순수 함수 우선**: 부수 효과(side effect) 없는 순수 함수를 우선 사용한다. 입력 → 출력이 명확한 함수는 테스트가 쉽다.
- **작은 단위**: 하나의 함수/컴포저블은 하나의 책임만 가진다. 큰 함수는 테스트 가능한 작은 함수로 분리한다.
- **테스트 작성**: 새로운 기능 추가 시 유닛 테스트를 함께 작성한다. 기존 테스트가 깨지지 않는지 확인한다.

### 4. 작업 완료 시 커밋 & 푸시
- 모든 작업이 완료되면 반드시 git commit 후 `git push`까지 수행한다.
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
docs: AGENTS.md 워크플로우 규칙 추가
```

## Documentation Classification

프로젝트 문서 분류와 역할 정의는 `docs/DOCS_CLASSIFICATION.md`를 기준으로 관리한다.

- README.md: 사용자/배포 관점의 공식 안내 문서
- CLAUDE.md, AGENTS.md: 에이전트 작업 규칙 및 운영 가이드
- CODE_QUALITY.md: 코드 작성/리뷰 품질 기준
- CHANGELOG.md: 릴리즈 변경 이력(Keep a Changelog)

문서 간 중복 내용이 발생할 경우, 상세 설명은 `docs/`에 모으고 AGENTS/CLAUDE에는 요약과 링크만 유지한다.

### Single Source Policy

- 에이전트 작업 규칙의 단일 출처는 `AGENTS.md`로 한다.
- `CLAUDE.md`는 요약/진입 문서로 유지하고, 상세 규칙은 `AGENTS.md`를 참조한다.
- 작업 규칙 수정 시 `AGENTS.md`를 먼저 변경하고, 필요 시 `CLAUDE.md`에는 요약만 동기화한다.

## Documentation Record

- 2026-04-10: 프로젝트 전체 문서(README.md, CLAUDE.md, CODE_QUALITY.md, CHANGELOG.md, `.claude/settings.local.json`)를 재검토하고 문서 분류 기준을 `docs/DOCS_CLASSIFICATION.md`로 분리 기록함.
- 2026-04-10: 문서 좌표 표기를 `io.github.ois0886:compose-git-grass`로 통일하고, AGENTS/CLAUDE 중복 관리를 위해 단일 출처 정책을 추가함.
- 2026-04-10: `scripts/verify-doc-sync.sh`를 추가해 README/CHANGELOG 버전 동기화를 자동 검증하고, CI를 `quality/assemble/ui-test` 병렬 구조로 최적화함.
- 2026-04-10: CI `ui-test` 실패 원인(`GrassCellTest`의 `performLongClick` 참조 오류) 수정 후, GitHub Actions 런 #24225465922 전체 잡 통과를 확인함.
