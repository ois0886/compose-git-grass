# Changelog

이 프로젝트의 모든 주요 변경 사항을 기록합니다.

형식은 [Keep a Changelog](https://keepachangelog.com/ko/1.1.0/)를 따르며,
[Semantic Versioning](https://semver.org/lang/ko/)을 준수합니다.

## [Unreleased]

### Added
- CHANGELOG.md 추가
- ProGuard consumer rules 추가 (라이브러리 public API 보호)
- 성능 벤치마크 테스트 추가 (1000+ 데이터 포인트)
- JaCoCo 코드 커버리지 설정
- Dokka API 문서 생성 설정
- 릴리즈 자동화 GitHub Actions 워크플로우 추가

## [0.1.1] - 2026-02-16

### Added
- `GitGrass` 컴포저블: GitHub 스타일 잔디 그래프
- `GitGrassColors` 데이터 클래스: 완전 커스터마이징 가능한 색상 스킴
- `GitGrassDefaults`: GitHub 라이트/다크 테마, 기본 라벨, 기본값 제공
- 연속 기여일(streak) 계산 (최대/현재)
- 월 라벨, 요일 라벨, 연도 라벨 표시 (토글 옵션)
- 셀 클릭 콜백 (`onCellClick`)
- 커스텀 레벨 매핑 (`levelOf` 파라미터)
- 날짜 범위 자동 swap (startDate > endDate)
- 음수 기여 횟수 0으로 클램핑
- 최신 날짜로 자동 스크롤
- 36개 유닛 테스트 (그리드 유틸리티 + 색상 매핑)
- GitHub Actions CI 파이프라인
- Maven Central 배포: `io.github.ois0886:compose-git-grass:0.1.1`

## [0.1.0] - 2026-02-16

### Added
- 프로젝트 초기 설정 (`:library`, `:app` 모듈)
- 핵심 그리드 생성 (`buildGrid`, `generateDayList`)
- 월 라벨 위치 결정 (`createMonthLabels`)
- 연도 라벨 포맷팅 (`formatYearLabel`)

[Unreleased]: https://github.com/ois0886/compose-git-grass/compare/v0.1.1...HEAD
[0.1.1]: https://github.com/ois0886/compose-git-grass/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/ois0886/compose-git-grass/releases/tag/v0.1.0
