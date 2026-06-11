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
    val scrollState = rememberLazyListState()

    // 4. 부수 효과 (LaunchedEffect)
    LaunchedEffect(grid) {
        // 최신 날짜가 보이도록 lazy list 위치 조정
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
val text = remember(name, count) { "$name: $count" }  // 문자열 연결은 저비용
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
├── MonthRow (LazyListState 공유)
├── WeekLabelColumn
├── GrassGridContent (LazyListState 공유)
│   └── GrassWeekColumn (Canvas) → GrassCell (hit target)
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

### 조건부 계산

숨겨진 UI에만 필요한 파생 값은 토글이 켜졌을 때만 계산한다:

```kotlin
val monthPositions = remember(showMonthLabels, grid) {
    if (showMonthLabels) createMonthLabels(grid) else emptyList()
}
val streakInfo = remember(showStreak, safeContributions, days, safeEnd) {
    if (showStreak) calculateStreak(safeContributions, days, safeEnd) else null
}
```

### 반복 렌더링 객체 재사용

셀처럼 반복 횟수가 많은 UI에서 동일한 객체를 각 셀마다 생성하지 않는다. Shape, TextStyle, 라벨 맵 등은 가능한 상위 컴포저블에서 한 번 만들고 하위 컴포넌트로 전달한다.

### 렌더링 데이터 사전 계산

셀별 count, level, color, 접근성 라벨처럼 입력이 바뀔 때만 달라지는 값은 `remember`된 render data로 미리 계산한다. 하위 컴포넌트는 가능한 한 이미 계산된 값을 렌더링만 한다.

```kotlin
val renderGrid = remember(grid, safeContributions, colors, levelOf) {
    buildRenderGrid(...)
}
```

### 컬렉션 복사 최소화

정규화가 필요 없는 입력 컬렉션은 원본을 그대로 반환한다. 값 변경이 실제로 필요한 경우에만 새 컬렉션을 만든다.

### Lazy 렌더링 우선

주 단위처럼 반복 개수가 날짜 범위에 비례하는 UI는 eager `Row`보다 `LazyRow`를 우선한다. 월 라벨과 그리드는 같은 `LazyListState`를 공유해 정렬과 스크롤 동기화를 유지한다.

### Canvas와 Semantics 분리

반복 셀의 시각 렌더링은 Canvas로 합치되, 접근성/클릭/롱클릭은 별도의 hit target 컴포저블로 유지한다. 사용자가 렌더러를 직접 선택하는 public 옵션은 실제 요구가 생기기 전까지 추가하지 않는다.

### Consumer Rules 범위 제한

AAR consumer ProGuard 규칙은 앱 전체에 영향을 주므로 `com.inseong.gitgrass.**`처럼 라이브러리 패키지 범위로 한정한다. 전역 `class *` keep 규칙은 피한다.

### Compose Compiler 리포트

Compose compiler metrics/reports는 기본 빌드에 영향을 주지 않도록 Gradle property로 opt-in한다. 필요할 때만 `-PcomposeCompilerReports=true`로 생성한다.

### 벤치마크 기준

| 연산 | 데이터 규모 | 제한 |
|------|------------|------|
| 단일 연산 (generateDayList, buildGrid, buildRenderGrid, calculateStreak) | 1,000일 | 100회 평균 < 100ms |
| Lazy/Canvas 렌더링 데이터 준비 (buildRenderGrid) | 3,650일 (10년) | 30회 평균 < 250ms |
| 전체 파이프라인 | 3,650일 (10년) | 10회 평균 < 500ms |

새로운 유틸리티 함수 추가 시 대량 데이터 벤치마크를 `GridBenchmarkTest.kt`에 추가한다.

---

## 7. 접근성

### 필수 사항

- 모든 의미 있는 UI 요소에 `contentDescription`을 제공한다.
- 인터랙티브 요소(클릭 가능)에는 `Role.Button`을 설정한다.
- 클릭 가능한 요소에는 `onClickLabel`을 제공한다.

```kotlin
// cellContentDescription, cellClickLabel은 GitGrass() 파라미터로 주입
Box(
    modifier = baseModifier
        .combinedClickable(
            onClick = onClick ?: {},
            onLongClick = onLongClick,
            onClickLabel = clickLabelText,
        )
        .semantics {
            contentDescription = contentDescriptionText
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

---

## 9. Kotlin 모범 사례 (Best Practices)

> 본 프로젝트에 적용 가능한 Kotlin 핵심 원칙을 정리한다.
> 기존 섹션(1~8)에서 이미 다루는 내용(가시성 최소화, @Immutable, remember, KDoc 등)은 제외한다.

### 9.1 안정성 (Safety)

#### 가변성 제한

- `var`보다 `val`을 우선 사용한다.
- 불변 컬렉션(`List`, `Map`)을 기본으로 사용하고, `MutableList`는 함수 내부 빌드 용도로만 사용한 뒤 불변으로 반환한다.
- 데이터 변경이 필요하면 `copy()`를 활용한다.

```kotlin
// O: 내부에서 mutable로 빌드 → 불변으로 반환
internal fun buildGrid(...): List<List<LocalDate?>> {
    val weeks = mutableListOf<MutableList<LocalDate?>>()
    // ... 빌드 로직
    return weeks  // List<MutableList>이지만 반환 타입은 List<List>
}

// O: 상태 변경 시 copy() 활용
val updated = streakInfo.copy(currentStreak = 5)

// X: 외부에 MutableList를 직접 노출
fun getWeeks(): MutableList<MutableList<LocalDate?>> = weeks  // 위험
```

#### 변수 스코프 최소화

- 변수는 사용하는 곳에 가장 가까이 선언한다.
- 구조분해 선언을 활용하여 스코프를 좁힌다.

```kotlin
// O: 구조분해로 스코프 축소
val (safeStart, safeEnd) = remember(startDate, endDate) {
    normalizeDateRange(startDate, endDate)
}

// O: 반복문 내부에서 선언
for ((weekIndex, week) in grid.withIndex()) {
    val firstDay = week.firstNotNullOfOrNull { it } ?: continue
    val month = firstDay.monthValue
    // ...
}

// X: 불필요하게 넓은 스코프
val firstDay: LocalDate? = null  // 반복문 밖에 선언
for (week in grid) {
    firstDay = week.firstNotNullOfOrNull { it }
    // ...
}
```

#### require/check로 기대 조건 명시

- `require()`: 함수 아규먼트 검증 → `IllegalArgumentException`
- `check()`: 객체 상태 검증 → `IllegalStateException`

> 참고: 본 라이브러리는 자동 정규화 전략을 사용한다(섹션 4 참조).
> 하지만 내부 유틸리티나 테스트 헬퍼처럼 **정규화가 아닌 명시적 검증이 필요한 곳**에는
> `require`/`check`를 사용한다.

```kotlin
// 내부 함수에서 사전 조건 검증
internal fun validateLevels(levels: List<Color>) {
    require(levels.isNotEmpty()) { "levels must not be empty" }
}

// 상태 검증
check(googledPlatformType != null) { "Platform type should be resolved" }
```

#### null 안전 처리

- 안전 호출(`?.`), 엘비스 연산자(`?:`), `firstOrNull` 등을 활용한다.
- `!!` 연산자는 null이 아님이 확실히 보장된 경우에만 사용한다.
- 결과가 없을 수 있는 함수는 예외 대신 `null`을 반환한다.

```kotlin
// O: 안전 호출 + 엘비스 연산자
val count = contributions[day] ?: 0
val firstDay = week.firstNotNullOfOrNull { it } ?: continue

// O: null 가능성을 타입으로 표현
val onCellClick: ((LocalDate, Int) -> Unit)? = null

// X: 불필요한 !! 사용
val count = contributions[day]!!  // NPE 위험
```

### 9.2 가독성 (Readability)

#### 가독성 중심 설계

- 코드 작성 시간보다 읽는 시간이 길다. **인식 부하를 최소화**하는 방향으로 설계한다.
- 관용적(idiomatic) 코틀린 코드를 선호하되, 과도한 축약은 피한다.

```kotlin
// O: 명확한 의도 전달
if (level <= 0 || colors.levels.isEmpty()) return colors.empty
return colors.levels[(level - 1).coerceIn(0, colors.levels.lastIndex)]

// X: 지나친 체이닝으로 인식 부하 증가
return level.takeIf { it > 0 }
    ?.let { colors.levels.getOrNull(it - 1) }
    ?: colors.empty
```

#### 프로퍼티는 상태를 나타냄

- 프로퍼티는 **상태(state)를 표현**하는 데 사용한다.
- 복잡한 계산이나 부수 효과가 있는 동작은 **함수**로 작성한다.

```kotlin
// O: 프로퍼티 — 단순 상태
val cellSize: Dp = 12.dp
val monthLabels: List<String> = listOf("", "Jan", ...)

// O: 함수 — 계산/생성 동작
fun colors(): GitGrassColors = GitGrassColors(...)       // 매번 새 객체 생성
fun startDate(weekStartDay: DayOfWeek): LocalDate = ...  // 현재 시간 기반 계산

// X: 프로퍼티에 무거운 계산 숨김
val grid: List<List<LocalDate?>>
    get() = buildGrid(days, weekStartDay)  // 호출마다 전체 그리드 재생성
```

#### 이름 있는 아규먼트 활용

- 동일 타입 파라미터가 연속되거나, 의미가 불명확한 경우 이름 있는 아규먼트를 사용한다.
- 특히 `Boolean` 파라미터는 반드시 이름을 명시한다.

```kotlin
// O: 이름 있는 아규먼트로 의도 명확화
GitGrass(
    contributions = data,
    showYearLabel = true,
    showWeekLabels = true,
    showStreak = false,
    showLegend = true,
)

// X: 의미 불분명한 positional 아규먼트
GitGrass(data, Modifier, startDate, endDate, DayOfWeek.MONDAY, colors, ...)
```

#### 표준 라이브러리 함수 우선

- 직접 구현하기 전에 **kotlin stdlib, collections API**에 동일 기능이 있는지 확인한다.
- `coerceIn`, `coerceAtLeast`, `firstNotNullOfOrNull`, `getOrElse`, `associate`, `withIndex` 등을 적극 활용한다.

```kotlin
// O: stdlib 활용
contributions.mapValues { (_, v) -> v.coerceAtLeast(0) }
monthPositions.associate { (weekIndex, month) -> weekIndex to month }
week.firstNotNullOfOrNull { it }
weekLabels.getOrElse(index) { "" }

// X: 직접 구현
fun clampToZero(value: Int): Int = if (value < 0) 0 else value  // coerceAtLeast(0) 사용
```

### 9.3 설계 (Design)

#### 추상화 수준 통일

- 하나의 함수는 **하나의 추상화 수준**으로 작성한다.
- 높은 수준(의도)과 낮은 수준(구현 세부)을 섞지 않는다.

```kotlin
// O: GitGrass 루트 — 높은 추상화 수준 (조율만 담당)
@Composable
fun GitGrass(...) {
    // 정규화 → 데이터 생성 → UI 구성 (각 단계를 별도 함수에 위임)
    val (safeStart, safeEnd) = remember(...) { normalizeDateRange(...) }
    val grid = remember(...) { buildGrid(days, weekStartDay) }
    Column {
        YearLabel(...)
        MonthRow(...)
        GrassGridContent(...)
    }
}

// X: 루트 함수 안에서 그리드 빌드 로직을 직접 구현
@Composable
fun GitGrass(...) {
    val weeks = mutableListOf<MutableList<LocalDate?>>()
    for (day in days) {
        val dayIndex = (day.dayOfWeek.value - weekStartDay.value + 7) % 7
        // ... 저수준 구현이 직접 들어감
    }
}
```

#### 컴포지션 우선

- 상속보다 **컴포지션(composition)** 을 선호한다.
- 상속은 `is-a` 관계에만 사용하고, `has-a` 관계는 컴포지션으로 표현한다.
- Compose에서는 작은 컴포저블 조합이 곧 컴포지션이다.

```kotlin
// O: 컴포지션 — 작은 컴포저블 조합
GitGrass (루트 조율자)
├── YearLabel          // 독립 컴포넌트
├── MonthRow           // LazyListState를 파라미터로 주입
├── GrassGridContent   // 렌더링 데이터와 LazyListState를 파라미터로 주입
└── ColorLegend        // 독립 컴포넌트

// X: 상속 기반 접근
open class BaseGrassComponent { ... }
class GrassGrid : BaseGrassComponent() { ... }  // 불필요한 상속 계층
```

#### 팩토리 함수 활용

- 생성자보다 **팩토리 함수**가 유리한 경우 활용한다.
- 팩토리 함수는 이름으로 의도를 전달할 수 있고, 캐싱이나 서브타입 반환이 가능하다.

```kotlin
// O: Defaults 객체의 팩토리 함수 — 이름으로 의도 전달
GitGrassDefaults.colors()       // 라이트 테마
GitGrassDefaults.darkColors()   // 다크 테마
GitGrassDefaults.localizedMonthLabels(locale)  // 로케일 기반 생성

// O: 팩토리 함수의 장점 — 매번 새 인스턴스가 생성됨을 이름으로 암시
fun startDate(weekStartDay: DayOfWeek): LocalDate = LocalDate.now()
    .minusYears(1)
    .with(TemporalAdjusters.previousOrSame(weekStartDay))
```

#### sealed 클래스로 제한된 계층 표현

- 태그 클래스(enum 필드로 타입 구분) 대신 **sealed 클래스/인터페이스**를 사용한다.
- 컴파일러가 `when` 분기의 완전성을 검증하여 안전성을 보장한다.

```kotlin
// O: sealed 클래스 — 타입 안전한 결과 표현
sealed interface LoadResult {
    data class Success(val contributions: Map<LocalDate, Int>) : LoadResult
    data class Error(val message: String) : LoadResult
    data object Loading : LoadResult
}

// when 사용 시 else 불필요 → 새 타입 추가 시 컴파일 에러로 누락 방지
when (result) {
    is LoadResult.Success -> GitGrass(contributions = result.contributions)
    is LoadResult.Error -> ErrorMessage(result.message)
    LoadResult.Loading -> LoadingIndicator()
}

// X: 태그 기반 — 타입 안전성 없음
data class LoadResult(
    val type: String,  // "success", "error", "loading"
    val data: Any?,
)
```

### 9.4 효율성 (Efficiency)

#### 불필요한 객체 생성 회피

- 동일한 값의 객체를 반복 생성하지 않는다.
- `remember`를 활용하여 리컴포지션 시 불필요한 재생성을 방지한다 (섹션 6 참조).
- 특히 `Regex`, `Shape`, `TextStyle` 등 비용 높은 객체에 주의한다.

```kotlin
// O: remember로 Shape 재생성 방지
val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

// O: 변환 결과 캐싱
val labelMap = remember(monthPositions) {
    monthPositions.associate { (weekIndex, month) -> weekIndex to month }
}

// X: 매 리컴포지션마다 새 객체 생성
val shape = RoundedCornerShape(cornerRadius)  // remember 없이 매번 생성
```

#### 컬렉션 처리 단계 최소화

- 체이닝 단계를 줄여 중간 컬렉션 생성을 최소화한다.
- `mapNotNull`, `filterIsInstance` 등 결합 연산자를 활용한다.

```kotlin
// O: 단일 연산으로 결합
week.firstNotNullOfOrNull { it }           // filter + first 대체
contributions.mapValues { (_, v) -> v.coerceAtLeast(0) }  // 한 단계로 처리

// X: 불필요한 중간 단계
week.filterNotNull().firstOrNull()         // 중간 리스트 생성
contributions.filter { it.value < 0 }.mapValues { (_, v) -> 0 } +
    contributions.filter { it.value >= 0 }  // 2번 순회 + 합성
```

#### 대량 데이터에는 Sequence 고려

- 컬렉션에 **여러 변환 단계**를 적용할 때 `Sequence`를 고려한다.
- Sequence는 지연 처리(lazy evaluation)로 중간 컬렉션을 만들지 않는다.
- 단, 단일 연산이거나 데이터가 적으면 일반 컬렉션이 더 효율적이다.

```kotlin
// O: 대량 데이터 + 다단계 처리 시 Sequence
(0 until 3650).asSequence()
    .map { start.plusDays(it.toLong()) }
    .filter { contributions.containsKey(it) }
    .toList()

// 일반 컬렉션이 적합한 경우: 단일 연산 또는 소량 데이터
Month.entries.map { it.getDisplayName(TextStyle.SHORT, locale) }  // 12개뿐
```
