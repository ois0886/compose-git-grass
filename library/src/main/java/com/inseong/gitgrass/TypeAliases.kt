package com.inseong.gitgrass

import java.time.LocalDate

/** Map of dates to contribution counts. */
internal typealias ContributionData = Map<LocalDate, Int>

/** Grid of weeks, each containing 7 nullable dates. */
internal typealias Grid = List<List<LocalDate?>>

/** Grid of precomputed render data, preserving the same week/day shape as [Grid]. */
internal typealias RenderGrid = List<List<GrassCellRenderData?>>

/** List of (weekIndex, monthNumber) pairs for month label positioning. */
internal typealias MonthPositions = List<Pair<Int, Int>>
