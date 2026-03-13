# Code Quality Guide

compose-git-grass 프로젝트의 코드 퀄리티 가이드라인.
모든 코드 작성 및 리뷰 시 이 문서의 규칙을 준수한다.

---

## 1. Kotlin 컨벤션

### 네이밍

| 대상 | 규칙 | 예시 |
|------|------|------|
| 클래스 / 컴포저블 | PascalCase | `GitGrass`, `GrassCell`, `GitGrassColors` |
| 함수 / 변수 | camelCase | `buildGrid`, `dayIndexInWeek`, `cellSize` |
| 상수 (object 내) | camelCase 또는 PascalCase | `GitGrassDefaults.cellSize`, `GitGrassDefaults.monthLabels` |
| 테스트 함수 | 백틱 + 설명문 | `` `normalizeDateRange swaps when start after end` `` |

### 가시성

- **Public API는 최소화**한다. 외부에 노출할 필요가 없는 모든 것은 `internal`로 선언한다.
- Public: `GitGrass`, `GitGrassColors`, `GitGrassDefaults`, `GitGrassStreakInfo`
- Internal: UI 컴포넌트 (`YearLabel`, `MonthRow`, `GrassCell` 등), 유틸리티 함수 (`buildGrid`, `calculateStreak` 등)

### 데이터 클래스

- Compose에 전달하는 데이터 클래스에는 `@Immutable` 어노테이션을 사용한다.
- Compose 컴파일러 최적화(스마트 리컴포지션 스킵)를 활성화하기 위함이다.

```kotlin
@Immutable
data class GitGrassColors(
    val empty: Color,
    val levels: List<Color>,
    val text: Color,
    val border: Color,
)
```

---

## 2. Compose 패턴

### Composable 함수 구조

루트 컴포저블은 다음 순서를 따른다:

```kotlin
@Composable
fun GitGrass(...) {
    // 1. 입력 정규화 (remember)
    val (safeStart, safeEnd) = remember(startDate, endDate) {
        normalizeDateRange(startDate, endDate)
    }

    // 2. 파생 상태 계산 (remember)
    val grid = remember(days, weekStartDay) { buildGrid(days, weekStartDay) }

    // 3. Compose 상태
    val scrollState = rememberScrollState()

    // 4. 부수 효과 (LaunchedEffect)
    LaunchedEffect(grid) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    // 5. 레이아웃 구성
    Column(modifier = modifier) { ... }
}
```

### 파라미터 순서

```
1. 필수 데이터 (contributions, dates)
2. modifier (기본값: Modifier)
3. 스타일 (colors, cellSize, fontSize)
4. UI 토글 (showYearLabel, showLegend)
5. 텍스트 라벨 (streakMaxLabel, lessLabel)
6. 람다/콜백 (levelOf, onCellClick, onCellLongClick)
```

### remember 사용 규칙

- **비용 높은 계산**에만 `remember`를 사용한다 (그리드 빌드, streak 계산, Shape 생성 등).
- **의존성을 명시적으로 선언**한다. 필요한 key만 포함한다.
- 단순 값에는 `remember`를 사용하지 않는다.

```kotlin
// O: 비용 높은 계산
val grid = remember(days, weekStartDay) { buildGrid(days, weekStartDay) }
val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

// X: 단순 값에 불필요한 remember
val label = remember { "Less" }  // 불필요
```

### Modifier 패턴

base modifier를 정의한 후 조건부로 확장한다:

```kotlin
val baseModifier = Modifier
    .size(size)
    .clip(shape)
    .background(color, shape)
    .semantics { contentDescription = description }

Box(
    modifier = if (onClick != null || onLongClick != null) {
        baseModifier.combinedClickable(...)
    } else {
        baseModifier
    }
)
```

---

## 3. 아키텍처

### 순수 함수 분리

- **비즈니스 로직은 순수 함수로 추출**한다. Compose/UI 의존성을 가지지 않는다.
- 순수 함수는 `GridUtils.kt`처럼 별도 파일에 모은다.
- 입력 → 출력이 명확한 함수로 작성하여 단위 테스트를 용이하게 한다.

```kotlin
// 순수 함수: UI 의존성 없음, 테스트 용이
internal fun buildGrid(days: List<LocalDate>, weekStartDay: DayOfWeek): List<List<LocalDate?>>
internal fun calculateStreak(contributions: Map<LocalDate, Int>, days: List<LocalDate>, today: LocalDate): GitGrassStreakInfo
```

### 데이터 흐름

```
원시 입력 (Map<LocalDate, Int>, LocalDate)
    ↓
정규화 (normalizeDateRange, normalizeContributions)
    ↓
생성 (generateDayList → buildGrid)
    ↓
파생 데이터 (createMonthLabels, formatYearLabel, calculateStreak)
    ↓
UI 렌더링 (Composable + remember)
```

각 단계는 이전 단계의 출력만을 입력으로 받는 순수 변환이다.

### 컴포넌트 계층

- 하나의 컴포저블은 **하나의 책임**만 가진다.
- 루트 컴포저블이 조율하고, 하위 컴포넌트는 독립적으로 동작한다.

```
GitGrass (루트 조율자)
├── YearLabel
├── MonthRow (scrollState 공유)
├── WeekLabelColumn
├── GrassGridContent (scrollState 공유)
│   └── GrassWeekColumn → GrassCell
├── StreakSummary
└── ColorLegend
```

---

## 4. 에러 처리

### 예외 대신 자동 정규화

라이브러리는 잘못된 입력에 대해 **예외를 던지지 않는다**. 대신 자동으로 정규화한다:

| 상황 | 처리 방식 |
|------|-----------|
| start > end | 자동 swap (`normalizeDateRange`) |
| 음수 contribution | 0으로 clamping (`coerceAtLeast(0)`) |
| 범위 초과 level | 마지막 색상으로 clamping (`coerceIn`) |
| 누락된 라벨 | 빈 문자열 반환 (`getOrElse { "" }`) |
| 빈 입력 리스트 | 빈 결과 반환 (`emptyList()`) |

### 방어적 코딩 패턴

```kotlin
// coerceAtLeast: 하한 보장
contributions.mapValues { (_, v) -> v.coerceAtLeast(0) }

// coerceIn: 범위 보장
colors.levels[(level - 1).coerceIn(0, colors.levels.lastIndex)]

// getOrElse: 안전한 인덱스 접근
weekLabels.getOrElse(index) { "" }

// 빈 컬렉션 조기 반환
if (days.isEmpty()) return emptyList()
```

---

## 5. 테스트

### 테스트 이름 규칙

백틱(`` ` ``)으로 감싼 **설명적 문장**을 사용한다. 실패 시 메시지가 곧 문서가 된다.

```kotlin
@Test
fun `normalizeDateRange swaps when start after end`() { ... }

@Test
fun `calculateStreak ignores days after today`() { ... }

@Test
fun `buildGrid each week has exactly 7 slots`() { ... }
```

### 테스트 구조

- **JUnit 4** + `Assert` (assertEquals, assertTrue, assertFalse)
- 하나의 테스트는 **하나의 동작**만 검증한다.
- **순수 함수를 우선 테스트**한다 (UI 테스트보다 단위 테스트 우선).

### 테스트 필수 조건

- 새로운 기능 추가 시 **유닛 테스트를 함께 작성**한다.
- 기존 테스트가 깨지지 않는지 확인한다 (`./gradlew :library:test`).
- 엣지 케이스를 포함한다: 빈 입력, 역순 입력, 경계값, 윤년 등.

### 테스트 파일 구성

기능 단위로 테스트 파일을 분리한다:

```
GridUtilsTest.kt       — 그리드 생성, 날짜, streak 계산
GitGrassDefaultsTest.kt — 기본값, 로케일, 레벨 임계값
LevelToColorTest.kt     — 색상 매핑
GridBenchmarkTest.kt    — 성능 벤치마크
```

---

## 6. 성능

### remember 메모이제이션

비용 높은 계산은 반드시 `remember`로 캐싱한다:

```kotlin
val grid = remember(days, weekStartDay) { buildGrid(days, weekStartDay) }
val streakInfo = remember(safeContributions, days) {
    calculateStreak(safeContributions, days, safeEnd)
}
val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
```

### 벤치마크 기준

| 연산 | 데이터 규모 | 제한 |
|------|------------|------|
| 단일 연산 (generateDayList, buildGrid, calculateStreak) | 1,000일 | 100회 평균 < 100ms |
| 전체 파이프라인 | 3,650일 (10년) | 10회 평균 < 500ms |

새로운 유틸리티 함수 추가 시 대량 데이터 벤치마크를 `GridBenchmarkTest.kt`에 추가한다.

---

## 7. 접근성

### 필수 사항

- 모든 의미 있는 UI 요소에 `contentDescription`을 제공한다.
- 인터랙티브 요소(클릭 가능)에는 `Role.Button`을 설정한다.
- 클릭 가능한 요소에는 `onClickLabel`을 제공한다.

```kotlin
Box(
    modifier = baseModifier
        .combinedClickable(
            onClick = onClick ?: {},
            onLongClick = onLongClick,
            onClickLabel = "$date 상세보기",
        )
        .semantics {
            contentDescription = "$date: ${count}건"
            if (onClick != null) {
                role = Role.Button
            }
        }
)
```

### 루트 레이아웃

루트 컴포저블에도 전체를 설명하는 `contentDescription`을 설정한다:

```kotlin
Column(
    modifier = modifier.semantics {
        contentDescription = "Contribution graph"
    },
)
```

---

## 8. 문서화

### KDoc

- **공개 API**에는 반드시 KDoc을 작성한다.
- `@param` 태그로 각 파라미터를 설명한다.
- 자동 정규화 동작(swap, clamping)을 명시한다.

```kotlin
/**
 * A GitHub-style contribution graph (grass) composable.
 *
 * @param contributions Map of dates to contribution counts.
 *   Negative values are automatically clamped to 0.
 * @param startDate First date shown in the graph.
 *   If [startDate] is after [endDate], they are swapped automatically.
 */
```

### 섹션 구분 주석

복잡한 로직 내부에서는 구분선 주석으로 가독성을 높인다:

```kotlin
// ── Max streak: longest consecutive run ──────────────────
var maxStreak = 0
var runLength = 0

// ── Current streak: count backward from today ────────────
var currentStreak = 0
```
