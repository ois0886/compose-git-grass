# compose-git-grass

Jetpack Compose에서 사용할 수 있는 가볍고 독립적인 GitHub 잔디(contribution graph) UI 컴포넌트 라이브러리입니다.

`Map<LocalDate, Int>`만 넘기면 끝입니다.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ois0886/compose-git-grass)](https://central.sonatype.com/artifact/io.github.ois0886/compose-git-grass)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg)](https://developer.android.com/about/versions/oreo)

## Why?

GitHub의 잔디 그래프는 활동량을 한눈에 보여주는 훌륭한 시각화 방식입니다.
습관 추적, 공부 기록, 코딩 활동, 건강 데이터 등 매일의 활동을 기록하는 앱이라면 같은 패턴을 활용할 수 있지만, Compose에서 바로 쓸 수 있는 간단한 라이브러리가 없었습니다.

기존 접근 방식들은 Material3에 의존하거나, 앱 전용 모델에 묶여 있거나, 특정 프로젝트와 강하게 결합되어 있었습니다. **compose-git-grass**는 이 문제를 해결하기 위해 만들었습니다:

- **UI 의존성 제로** - Compose Foundation(`BasicText`, `Box`, `Row`)만 사용합니다. Material3가 필요 없습니다.
- **입력 하나** - `Map<LocalDate, Int>`면 충분합니다. 커스텀 모델도, 어댑터도, 보일러플레이트도 없습니다.
- **완전한 커스터마이징** - 색상, 라벨, 크기, 임계값, 셀 클릭까지 모든 것을 파라미터로 설정할 수 있고, 기본값도 잘 잡혀 있습니다.

## Setup

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.ois0886:compose-git-grass:0.1.1")
}
```

## Quick Start

```kotlin
// 최소 사용 - 데이터만 넘기면 끝
GitGrass(
    contributions = mapOf(
        LocalDate.now() to 5,
        LocalDate.now().minusDays(1) to 3,
    )
)
```

```kotlin
// 다크모드 + 스트릭 + 셀 클릭
GitGrass(
    contributions = data,
    colors = GitGrassDefaults.darkColors(),
    showStreak = true,
    onCellClick = { date, count ->
        println("$date: ${count}회 기여")
    },
)
```

## Customization

### Colors

원하는 색상과 레벨 수를 자유롭게 지정할 수 있습니다:

```kotlin
// 파란색 테마, 3단계
GitGrass(
    contributions = data,
    colors = GitGrassColors(
        empty = Color(0xFFEEEEEE),
        levels = listOf(
            Color(0xFFBBDEFB),
            Color(0xFF42A5F5),
            Color(0xFF1565C0),
        ),
        text = Color.Black,
        border = Color.Gray,
    ),
    levelOf = { count ->
        when {
            count <= 0 -> 0
            count <= 5 -> 1
            count <= 10 -> 2
            else -> 3
        }
    },
)
```

기본 제공 색상:
- `GitGrassDefaults.colors()` - GitHub 라이트 테마
- `GitGrassDefaults.darkColors()` - GitHub 다크 테마

### Localization

```kotlin
// 한국어 라벨
GitGrass(
    contributions = data,
    weekLabels = listOf("월", "화", "수", "목", "금", "토", "일"),
    monthLabels = listOf("", "1월", "2월", "3월", "4월", "5월", "6월",
        "7월", "8월", "9월", "10월", "11월", "12월"),
    showStreak = true,
    streakMaxLabel = "최대 연속",
    streakCurrentLabel = "현재 연속",
)
```

### Sizing

```kotlin
GitGrass(
    contributions = data,
    cellSize = 16.dp,
    cellSpacing = 4.dp,
    cellCornerRadius = 4.dp,
    labelFontSize = 12.sp,
)
```

### Toggle Components

```kotlin
GitGrass(
    contributions = data,
    showYearLabel = false,
    showWeekLabels = false,
    showMonthLabels = true,
    showStreak = true,
)
```

## API Reference

### `GitGrass`

| Parameter | Type | Default | Description |
|---|---|---|---|
| `contributions` | `Map<LocalDate, Int>` | *필수* | 날짜별 기여 횟수 |
| `startDate` | `LocalDate` | ~1년 전 (월요일) | 그래프 시작 날짜 |
| `endDate` | `LocalDate` | 오늘 | 그래프 끝 날짜 |
| `colors` | `GitGrassColors` | GitHub 라이트 | 색상 스킴 |
| `monthLabels` | `List<String>` | 영어 | 월 이름 (인덱스 0 미사용) |
| `weekLabels` | `List<String>` | 영어 | 요일 이름 (월요일 시작) |
| `cellSize` | `Dp` | 12.dp | 셀 크기 |
| `cellSpacing` | `Dp` | 3.dp | 셀 간격 |
| `cellCornerRadius` | `Dp` | 2.dp | 셀 모서리 반경 |
| `labelFontSize` | `TextUnit` | 10.sp | 라벨 폰트 크기 |
| `showYearLabel` | `Boolean` | true | 연도 라벨 표시 |
| `showWeekLabels` | `Boolean` | true | 요일 라벨 표시 |
| `showMonthLabels` | `Boolean` | true | 월 라벨 표시 |
| `showStreak` | `Boolean` | false | 스트릭 요약 표시 |
| `streakMaxLabel` | `String` | "Max streak" | 최대 스트릭 라벨 |
| `streakCurrentLabel` | `String` | "Current streak" | 현재 스트릭 라벨 |
| `levelOf` | `(Int) -> Int` | 0/1-3/4-6/7-9/10+ | 기여 횟수 → 레벨 매핑 |
| `onCellClick` | `((LocalDate, Int) -> Unit)?` | null | 셀 탭 콜백 |

### `GitGrassColors`

| Field | Description |
|---|---|
| `empty` | 기여 없는 날의 색상 |
| `levels` | 기여 레벨별 색상 리스트 (개수 자유) |
| `text` | 라벨 텍스트 색상 |
| `border` | 셀 테두리 색상 |

### Forgiving Input

잘못된 입력도 크래시 없이 안전하게 처리합니다:

- **날짜 역전** - `startDate > endDate`이면 자동으로 교체
- **음수 값** - 0으로 클램프
- **누락된 날짜** - 기여 0회로 처리
- **짧은 라벨 리스트** - 부족한 항목은 빈 문자열로 대체

## Development Highlights

- **Pure Compose Foundation** - Material3 의존 없이 Foundation의 `BasicText`만 사용하여, 어떤 디자인 시스템을 쓰는 프로젝트에서도 충돌 없이 동작합니다.
- **유연한 색상 레벨** - 고정된 `level1`~`level4` 대신 `levels: List<Color>`를 사용합니다. 3단계든, 5단계든, 10단계든 자유롭게 지정할 수 있습니다.
- **픽셀 단위 월 라벨 정렬** - 월 라벨이 그리드와 동일한 `Arrangement.spacedBy` + 고정 너비 슬롯 구조를 사용하여, 어떤 셀 크기에서도 정확하게 정렬됩니다.
- **공유 ScrollState** - 월 라벨 행과 그리드가 하나의 `ScrollState`를 공유하여 가로 스크롤 시 항상 동기화됩니다.
- **순수 함수 & 테스트** - 그리드/스트릭 계산 로직이 부수효과 없는 순수 함수로 구현되어 있으며, 28개의 유닛 테스트가 그리드 생성, 스트릭 계산, 색상 매핑, 엣지 케이스를 검증합니다.
- **오늘 날짜로 자동 스크롤** - `LaunchedEffect`로 첫 컴포지션 시 가장 최근 날짜(오른쪽 끝)로 자동 스크롤됩니다.

## Requirements

- Min SDK 26 (Android 8.0)
- Jetpack Compose (Foundation)

## License

```
Copyright 2025 Inseong

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
