package com.inseong.gitgrass

import java.time.LocalDate

/** Map of dates to contribution counts. */
internal typealias ContributionData = Map<LocalDate, Int>

/** Grid of weeks, each containing 7 nullable dates. */
internal typealias Grid = List<List<LocalDate?>>

/** List of (weekIndex, monthNumber) pairs for month label positioning. */
internal typealias MonthPositions = List<Pair<Int, Int>>
