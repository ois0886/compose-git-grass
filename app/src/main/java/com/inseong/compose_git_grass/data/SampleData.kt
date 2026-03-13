package com.inseong.compose_git_grass.data

import com.inseong.gitgrass.GitGrassDefaults
import java.time.LocalDate
import kotlin.random.Random

internal fun generateSampleData(): Map<LocalDate, Int> {
    val random = Random(seed = 42)
    val start = GitGrassDefaults.startDate()
    val end = GitGrassDefaults.endDate()
    val map = mutableMapOf<LocalDate, Int>()
    var current = start
    while (!current.isAfter(end)) {
        if (random.nextFloat() > 0.3f) {
            map[current] = random.nextInt(1, 15)
        }
        current = current.plusDays(1)
    }
    return map
}

internal fun generateSparseData(): Map<LocalDate, Int> {
    val random = Random(seed = 123)
    val start = LocalDate.now().minusMonths(6)
    val end = LocalDate.now()
    val map = mutableMapOf<LocalDate, Int>()
    var current = start
    while (!current.isAfter(end)) {
        if (random.nextFloat() > 0.8f) {
            map[current] = random.nextInt(1, 6)
        }
        current = current.plusDays(1)
    }
    return map
}
