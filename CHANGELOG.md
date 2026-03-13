# Changelog

이 프로젝트의 모든 주요 변경 사항을 기록합니다.

형식은 [Keep a Changelog](https://keepachangelog.com/ko/1.0.0/)를 따르며,
[Semantic Versioning](https://semver.org/lang/ko/)을 준수합니다.

## [Unreleased]

## [1.1.0] - 2026-03-13

### Added
- `cellContentDescription` 파라미터: GrassCell 접근성 텍스트 커스터마이징 (하드코딩 한국어 제거)
- `cellClickLabel` 파라미터: GrassCell 클릭 라벨 커스터마이징
- 내부 타입 별칭 (`ContributionData`, `Grid`, `MonthPositions`) 추가
- 레이아웃 상수 `GitGrassDefaults`에 추출 (`weekLabelWidth`, `DAYS_PER_WEEK`, spacing 값)
- Compose UI 테스트 추가 (`GrassCellTest`, `LabelRenderingTest`)
- `createMonthLabels()`, `calculateStreak()` 추가 유닛 테스트
- CI에 Android Lint 정적 분석 단계 추가
- JaCoCo 커버리지 최소 80% 임계값 설정 (`jacocoCoverageVerification`)

### Changed
- `calculateStreak()` 이중 반복을 단일 패스로 최적화
- 매직 넘버 (`7`, `28.dp`, `4.dp`, `2.dp`, `8.dp`, `16.dp`)를 명명된 상수로 추출
- CODE_QUALITY.md 섹션 9 제목 및 서적 인용 제거 (내용 유지)
- 샘플 앱 `MainActivity`를 기능별 파일로 분리 (`demos/`, `components/`, `data/`)

### Deprecated
- `GitGrassColors.border` — 렌더링에 사용되지 않음, 2.0.0에서 제거 예정

### Infrastructure
- `library/build.gradle.kts`에 Compose UI 테스트 의존성 추가
- `library/src/androidTest/` 인스트루먼트 테스트 디렉토리 구성
- CI에 커버리지 임계값 검증 및 Lint 리포트 업로드 스텝 추가

## [1.0.0] - 2026-03-12

### Added
- **색상 범례 (Legend)**: "Less ↔ More" 색상 범례 표시 (`showLegend`, `lessLabel`, `moreLabel`)
- **로케일 대응**: `localizedMonthLabels()`, `localizedWeekLabels()` — 디바이스 로케일 기반 월/요일 라벨
- **주 시작일 커스텀**: `weekStartDay` 파라미터로 월요일/일요일 등 설정 가능
- **롱 프레스 콜백**: `onCellLongClick` 파라미터 추가
- **접근성**: 모든 셀에 `contentDescription`, `semantics`, `onClickLabel` 추가 (스크린 리더 지원)
- **그래프 접근성**: 루트 컴포저블에 "Contribution graph" semantics 추가
- `normalizeContributions()`, `normalizeDateRange()` 순수 함수 추출
- `dayIndexInWeek()`, `weekDaysOrdered()` 유틸리티 함수 추가
- `GrassWeekColumn` 컴포저블 분리 (관심사 분리)
- `GitGrassDefaultsTest` 테스트 스위트 신규 추가
- 윤년, 주 시작일 관련 테스트 케이스 추가

### Changed
- `GitGrass` 컴포저블 입력 검증을 순수 함수 호출로 리팩토링
- `MonthRow`의 `labelMap`을 `remember`로 메모이제이션 (성능 최적화)
- `GrassCell`의 `RoundedCornerShape`를 `remember`로 메모이제이션 (성능 최적화)
- `StreakSummary`의 텍스트를 `remember`로 메모이제이션
- `GrassGridContent`를 `GrassWeekColumn` 단위로 분리 (관심사 분리)
- `buildGrid()`에 `weekStartDay` 파라미터 추가
- `GitGrassDefaults.startDate()`에 `weekStartDay` 파라미터 추가

### Deprecated
- 0.1.x 버전은 더 이상 지원하지 않습니다. 1.0.0으로 업그레이드하세요.

### Infrastructure
- CHANGELOG.md 추가
- ProGuard consumer rules 추가 (라이브러리 public API 보호)
- 성능 벤치마크 테스트 추가 (1000+ 데이터 포인트)
- JaCoCo 코드 커버리지 설정
- Dokka 2.0 API 문서 생성 설정
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

[Unreleased]: https://github.com/ois0886/compose-git-grass/compare/v1.1.0...HEAD
[1.1.0]: https://github.com/ois0886/compose-git-grass/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/ois0886/compose-git-grass/compare/v0.1.1...v1.0.0
[0.1.1]: https://github.com/ois0886/compose-git-grass/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/ois0886/compose-git-grass/releases/tag/v0.1.0
