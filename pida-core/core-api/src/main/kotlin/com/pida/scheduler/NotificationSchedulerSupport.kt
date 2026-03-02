package com.pida.scheduler

import org.slf4j.Logger
import java.time.LocalDate
import java.time.temporal.ChronoField

internal fun LocalDate.toWeekKey(): Int = (year * 100) + get(ChronoField.ALIGNED_WEEK_OF_YEAR)

internal inline fun runSchedulerSafely(
    logger: Logger,
    failureMessage: String,
    block: () -> Unit,
): Boolean =
    runCatching { block() }
        .onFailure { error -> logger.error(failureMessage, error) }
        .isSuccess

abstract class DailyNotificationScheduler {
    private var lastExecutedDate: LocalDate? = null

    protected fun executeOncePerDay(
        logger: Logger,
        failureMessage: String,
        action: () -> Unit,
    ) {
        val today = LocalDate.now()

        if (lastExecutedDate == today) {
            return
        }

        val executed = runSchedulerSafely(logger, failureMessage, action)
        if (executed) {
            lastExecutedDate = today
        }
    }
}
