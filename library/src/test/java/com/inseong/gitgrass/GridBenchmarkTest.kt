package com.inseong.gitgrass

import org.junit.Test
import java.time.LocalDate
import kotlin.system.measureNanoTime

/**
 * 그리드 생성 함수의 성능 벤치마크 테스트.
 *
 * JMH가 아닌 measureNanoTime 기반의 간이 벤치마크로,
 * 대량 데이터에서의 성능 회귀를 감지하기 위한 목적.
 */
class GridBenchmarkTest {

    private fun generateContributions(count: Int): Pair<LocalDate, Map<LocalDate, Int>> {
        val end = LocalDate.of(2025, 12, 31)
        val start = end.minusDays(count.toLong() - 1)
        val map = mutableMapOf<LocalDate, Int>()
        var current = start
        var i = 0
        while (!current.isAfter(end)) {
            map[current] = (i % 10) + 1
            current = current.plusDays(1)
            i++
        }
        return start to map
    }

    @Test
    fun `generateDayList 1000일 100ms 이내 완료`() {
        val end = LocalDate.of(2025, 12, 31)
        val start = end.minusDays(999)

        repeat(5) { generateDayList(start, end) }

        val elapsed = measureNanoTime {
            repeat(100) { generateDayList(start, end) }
        }
        val avgMs = elapsed / 1_000_000.0 / 100
        println("generateDayList(1000일) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 100) { "generateDayList 성능 초과: $avgMs ms" }
    }

    @Test
    fun `buildGrid 1000일 100ms 이내 완료`() {
        val end = LocalDate.of(2025, 12, 31)
        val start = end.minusDays(999)
        val days = generateDayList(start, end)

        repeat(5) { buildGrid(days) }

        val elapsed = measureNanoTime {
            repeat(100) { buildGrid(days) }
        }
        val avgMs = elapsed / 1_000_000.0 / 100
        println("buildGrid(1000일) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 100) { "buildGrid 성능 초과: $avgMs ms" }
    }

    @Test
    fun `calculateStreak 1000건 100ms 이내 완료`() {
        val (start, contributions) = generateContributions(1000)
        val end = LocalDate.of(2025, 12, 31)
        val days = generateDayList(start, end)

        repeat(5) { calculateStreak(contributions, days, end) }

        val elapsed = measureNanoTime {
            repeat(100) { calculateStreak(contributions, days, end) }
        }
        val avgMs = elapsed / 1_000_000.0 / 100
        println("calculateStreak(1000건) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 100) { "calculateStreak 성능 초과: $avgMs ms" }
    }

    @Test
    fun `buildRenderGrid 1000일 100ms 이내 완료`() {
        val (start, contributions) = generateContributions(1000)
        val end = LocalDate.of(2025, 12, 31)
        val days = generateDayList(start, end)
        val grid = buildGrid(days)
        val colors = GitGrassDefaults.colors()

        repeat(5) {
            buildRenderGrid(
                grid = grid,
                contributions = contributions,
                colors = colors,
                levelOf = GitGrassDefaults.levelThresholds,
                cellContentDescription = { day, count -> "$day: $count" },
                cellClickLabel = { day -> "$day details" },
                includeClickLabels = false,
            )
        }

        val elapsed = measureNanoTime {
            repeat(100) {
                buildRenderGrid(
                    grid = grid,
                    contributions = contributions,
                    colors = colors,
                    levelOf = GitGrassDefaults.levelThresholds,
                    cellContentDescription = { day, count -> "$day: $count" },
                    cellClickLabel = { day -> "$day details" },
                    includeClickLabels = false,
                )
            }
        }
        val avgMs = elapsed / 1_000_000.0 / 100
        println("buildRenderGrid(1000일) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 100) { "buildRenderGrid 성능 초과: $avgMs ms" }
    }

    @Test
    fun `buildRenderGrid 3650일 Lazy Canvas 전처리 250ms 이내 완료`() {
        val (start, contributions) = generateContributions(3650)
        val end = LocalDate.of(2025, 12, 31)
        val days = generateDayList(start, end)
        val grid = buildGrid(days)
        val colors = GitGrassDefaults.colors()

        repeat(3) {
            buildRenderGrid(
                grid = grid,
                contributions = contributions,
                colors = colors,
                levelOf = GitGrassDefaults.levelThresholds,
                cellContentDescription = { day, count -> "$day: $count" },
                cellClickLabel = { day -> "$day details" },
                includeClickLabels = false,
            )
        }

        val elapsed = measureNanoTime {
            repeat(30) {
                buildRenderGrid(
                    grid = grid,
                    contributions = contributions,
                    colors = colors,
                    levelOf = GitGrassDefaults.levelThresholds,
                    cellContentDescription = { day, count -> "$day: $count" },
                    cellClickLabel = { day -> "$day details" },
                    includeClickLabels = false,
                )
            }
        }
        val avgMs = elapsed / 1_000_000.0 / 30
        println("buildRenderGrid(3650일, Lazy/Canvas 전처리) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 250) { "buildRenderGrid 10년 전처리 성능 초과: $avgMs ms" }
    }

    @Test
    fun `전체 파이프라인 3650일(10년) 500ms 이내 완료`() {
        val end = LocalDate.of(2025, 12, 31)
        val start = end.minusDays(3649)
        val contributions = mutableMapOf<LocalDate, Int>()
        val colors = GitGrassDefaults.colors()
        var d = start
        var i = 0
        while (!d.isAfter(end)) {
            contributions[d] = (i % 15)
            d = d.plusDays(1)
            i++
        }

        repeat(3) {
            val days = generateDayList(start, end)
            val grid = buildGrid(days)
            buildRenderGrid(
                grid = grid,
                contributions = contributions,
                colors = colors,
                levelOf = GitGrassDefaults.levelThresholds,
                cellContentDescription = { day, count -> "$day: $count" },
                cellClickLabel = { day -> "$day details" },
                includeClickLabels = false,
            )
            calculateStreak(contributions, days, end)
        }

        val elapsed = measureNanoTime {
            repeat(10) {
                val days = generateDayList(start, end)
                val grid = buildGrid(days)
                createMonthLabels(grid)
                buildRenderGrid(
                    grid = grid,
                    contributions = contributions,
                    colors = colors,
                    levelOf = GitGrassDefaults.levelThresholds,
                    cellContentDescription = { day, count -> "$day: $count" },
                    cellClickLabel = { day -> "$day details" },
                    includeClickLabels = false,
                )
                calculateStreak(contributions, days, end)
            }
        }
        val avgMs = elapsed / 1_000_000.0 / 10
        println("전체 파이프라인(3650일) 평균: %.3f ms".format(avgMs))
        assert(avgMs < 500) { "전체 파이프라인 성능 초과: $avgMs ms" }
    }
}
