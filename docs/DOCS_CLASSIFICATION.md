# Documentation Classification

이 문서는 `compose-git-grass` 저장소의 문서 역할을 분류하고, 갱신 기준을 명확히 하기 위한 운영 문서다.

## 1. 문서 역할 정의

### README.md
- 대상: 라이브러리 사용자/도입 검토자
- 역할: 기능 소개, 설치 방법, 사용 예시, API 파라미터, 접근성/커스터마이징 안내
- 성격: 외부 공개 문서 (패키지 소비자 중심)

### CLAUDE.md
- 대상: Claude/Codex 계열 에이전트 작업자
- 역할: 빠른 진입용 요약 가이드(핵심 커맨드/핵심 규칙)
- 성격: 내부 작업 지침 요약 문서 (`AGENTS.md` 참조)

### AGENTS.md
- 대상: AGENTS.md를 우선 읽는 자동화/에이전트 작업자
- 역할: 작업 규칙, 커밋/PR 규칙, 문서 인덱스 및 작업 기록
- 성격: 내부 작업 지침 + 기록 허브

### CODE_QUALITY.md
- 대상: 코드 작성자/리뷰어
- 역할: Kotlin/Compose 패턴, 순수 함수 분리, 에러 처리, 테스트/성능/리팩토링 가이드
- 성격: 코드 품질 표준 문서 (구현 판단 기준)

### CHANGELOG.md
- 대상: 릴리즈 소비자/유지보수자
- 역할: 버전별 변경사항 기록 (Added/Changed/Deprecated/Infrastructure)
- 성격: 릴리즈 이력 문서 (Keep a Changelog, SemVer)

### .claude/settings.local.json
- 대상: 로컬 에이전트 실행 환경
- 역할: 로컬 권한 설정(WebSearch/WebFetch/Bash)
- 성격: 개발자 로컬 런타임 설정 파일

## 2. 소스 오브 트루스(Source of Truth)

- 사용자 가이드의 단일 출처: `README.md`
- 개발/자동화 규칙의 단일 출처: `AGENTS.md` (`CLAUDE.md`는 요약 참조)
- 코드 품질 기준의 단일 출처: `CODE_QUALITY.md`
- 버전 변경 이력의 단일 출처: `CHANGELOG.md`

중복 내용을 수정할 때는 단일 출처 문서를 먼저 업데이트한 뒤, 나머지 문서에는 요약과 링크만 반영한다.

## 3. 문서 갱신 트리거

아래 변경이 발생하면 관련 문서를 함께 갱신한다.

- Public API 변경: `README.md`, `CHANGELOG.md`, 필요 시 `CODE_QUALITY.md`
- 빌드/테스트/배포 파이프라인 변경: `AGENTS.md`, `CLAUDE.md`
- 브랜치/커밋/PR 운영 규칙 변경: `AGENTS.md`, 요약 반영(`CLAUDE.md`)
- 코드 작성 규칙/리뷰 기준 변경: `CODE_QUALITY.md`, 요약 반영(`AGENTS.md`, `CLAUDE.md`)
- 릴리즈 태그/버전 변경: `README.md`, `CHANGELOG.md`, `AGENTS.md`, 요약 반영(`CLAUDE.md`)

## 4. 이번 점검 기록 (2026-04-10)

- 검토 대상
  - `README.md`
  - `CLAUDE.md`
  - `AGENTS.md`
  - `CODE_QUALITY.md`
  - `CHANGELOG.md`
  - `.claude/settings.local.json`
- 결과
  - `AGENTS.md`에 문서 분류 링크 및 점검 기록 추가
  - 문서 분류 기준을 `docs/`로 분리하여 유지보수성 개선
  - `CLAUDE.md`를 요약 가이드로 축소하고 상세 규칙은 `AGENTS.md` 단일 출처로 정리
  - `scripts/verify-doc-sync.sh`로 README/CHANGELOG 동기화 자동 검증 추가, CI 병렬 잡 구조로 실행 시간 최적화
