# Changelog

이 프로젝트의 모든 주요 변경 사항을 기록합니다.

형식은 [Keep a Changelog](https://keepachangelog.com/ko/1.0.0/)를 따르며,
[Semantic Versioning](https://semver.org/lang/ko/)을 준수합니다.

## [Unreleased]

## [1.2.0] - 2026-07-15

### Added
- `GitGrassSelection`: 날짜, 인셋 아웃라인 색상/두께를 담는 제어형 선택 상태 API 추가
- `GitGrass.selection`: 앱 상태로 선택 셀을 강조하고 `selected` semantics를 노출하는 옵션 추가
- `GitGrassDefaults.weekLabelsFor()`: 주 시작일에 맞춰 캐시된 영어 요일 라벨을 반환하는 헬퍼 추가
- 선택 해석, 월 경계, 완전한 주 단위 초기 스크롤 및 샘플 통계 테스트 추가

### Changed
- 월 라벨과 주별 Canvas를 하나의 `LazyRow` 항목으로 통합해 스크롤 동기화 구조 단순화
- 월 라벨을 매월 1일이 속한 주에 배치하고 첫 주 충돌 시 새 월을 우선하도록 개선
- 최신 날짜 자동 스크롤을 월 라벨이 잘리지 않는 완전한 주 열 기준으로 변경
- `weekStartDay` 변경 시 기본 영어 요일 라벨도 같은 순서로 자동 정렬
- 선택 변경 시 render grid를 재계산하지 않고 보이는 Canvas와 semantics만 갱신
- 샘플 앱을 GitHub 라이트/다크 대표 데모와 `Layout`/`Localization`/`Themes`/`Levels` 갤러리로 재구성
- README 설치/API/선택 예제와 실제 에뮬레이터 라이트·다크 이미지를 1.2.0 기준으로 갱신
- README/AGENTS/CLAUDE/CODE_QUALITY의 버전과 내부 구조 설명 동기화

### Fixed
- 롱클릭 전용 셀에 빈 클릭 액션이 함께 노출되던 접근성 semantics 수정
- 일요일 시작 그래프가 기본 월요일 순서 라벨을 표시하던 불일치 수정

## [1.1.1] - 2026-06-11

### Fixed
- Compose UI 인스트루먼트 테스트에서 `performLongClick` 참조 오류로 발생하던 CI(`ui-test`) 컴파일 실패를 `performTouchInput { longClick() }` 방식으로 수정

### Changed
- CI `ui-test` 잡에 타임아웃(30분)과 에뮬레이터 `-no-metrics` 옵션을 추가해 실행 안정성 개선
- 숨겨진 월 라벨/스트릭 UI에 필요한 파생 계산을 조건부로 수행하도록 최적화
- 음수 기여 값이 없는 입력 Map은 정규화 시 원본을 재사용해 불필요한 컬렉션 복사를 제거
- 셀/범례 렌더링에서 Shape 재사용 및 불필요한 clip 제거로 반복 셀 할당 비용 감소
- 셀별 count, color, 접근성 라벨을 렌더링 전용 데이터로 미리 계산해 리컴포지션 중 반복 계산 감소
- 샘플 앱에서 날짜 범위, 로케일 라벨, 커스텀 색상 팔레트를 `remember`로 재사용하도록 개선
- 그리드와 월 라벨 행을 주 단위 `LazyRow`로 전환해 긴 날짜 범위에서 화면 밖 UI 구성 비용 감소
- 셀 색상 렌더링을 내부 Canvas 경로로 전환하고, 접근성/클릭/롱클릭은 투명 hit target overlay로 유지
- ProGuard consumer rules의 Compose/Immutable keep 범위를 라이브러리 패키지로 축소
- `-PcomposeCompilerReports=true` 사용 시 Compose compiler metrics/reports를 생성하도록 opt-in 설정 추가
- Lazy/Canvas 렌더링 데이터 준비 경로의 10년 범위 성능 벤치마크 추가
- PR을 Draft가 아닌 Ready for review 상태로 생성하도록 에이전트 작업 문서에 명시

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

[Unreleased]: https://github.com/ois0886/compose-git-grass/compare/v1.2.0...HEAD
[1.2.0]: https://github.com/ois0886/compose-git-grass/compare/v1.1.1...v1.2.0
[1.1.1]: https://github.com/ois0886/compose-git-grass/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/ois0886/compose-git-grass/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/ois0886/compose-git-grass/compare/v0.1.1...v1.0.0
[0.1.1]: https://github.com/ois0886/compose-git-grass/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/ois0886/compose-git-grass/releases/tag/v0.1.0
