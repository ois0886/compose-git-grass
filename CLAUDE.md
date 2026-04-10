# compose-git-grass

이 문서는 에이전트 작업용 **요약 가이드**다.
상세 규칙과 최신 기준은 반드시 `AGENTS.md`를 따른다.

## Primary Source

- 작업 규칙 단일 출처: `AGENTS.md`
- 코드 품질 단일 출처: `CODE_QUALITY.md`
- 문서 분류 기준: `docs/DOCS_CLASSIFICATION.md`

## Quick Facts

- 배포 좌표: `io.github.ois0886:compose-git-grass:<version>`
- 라이브러리 모듈: `:library` (`com.inseong.gitgrass`)
- 샘플 앱 모듈: `:app`

## Core Commands

```bash
# Unit tests
./gradlew :library:test

# Compose UI instrumentation tests
./gradlew :library:connectedDebugAndroidTest

# Coverage (JaCoCo)
./gradlew :library:jacocoTestReport
./gradlew :library:jacocoCoverageVerification

# Docs/version sync check
./scripts/verify-doc-sync.sh

# Lint
./gradlew :library:lint

# Build
./gradlew :library:assembleDebug
./gradlew :app:assembleDebug
```

## Workflow Reminder

- 개발 작업 시작 전 계획 수립(Plan Mode First)
- 구현 시 테스트 가능성 우선(순수 함수/관심사 분리/작은 단위)
- 작업 완료 시 커밋 + 푸시
- 커밋 메시지는 한국어 Conventional Commit 형식 사용

세부 규칙은 `AGENTS.md`의 Workflow Rules를 확인한다.
