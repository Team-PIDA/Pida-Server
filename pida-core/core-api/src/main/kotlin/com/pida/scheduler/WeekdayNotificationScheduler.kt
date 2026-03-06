package com.pida.scheduler

import com.pida.notification.weekday.WeekdayNotificationService
import com.pida.support.extension.logger
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.random.Random

/**
 * 평일 힐링 푸시 알림 스케줄러
 *
 * 평일(월~금) 중 주당 랜덤으로 2회 실행 여부를 결정합니다.
 * 실행 트리거는 EveningNotificationScheduler(매일 18:00)에서 호출합니다.
 */
@Component
class WeekdayNotificationScheduler(
    private val weekdayNotificationService: WeekdayNotificationService,
) {
    private val logger by logger()
    private var executionCountThisWeek = 0
    private var lastExecutedWeekKey: Int? = null

    companion object {
        private const val MAX_EXECUTIONS_PER_WEEK = 2
        private const val EXECUTION_PROBABILITY = 0.4
        private const val THURSDAY_BOOSTED_PROBABILITY = 0.6
    }

    fun executeWeekdayNotification(today: LocalDate = LocalDate.now()) = executeIfEligible(today)

    /**
     * 평일 알림 실행 여부 결정
     *
     * - 이번 주에 이미 2회 실행했으면 스킵
     * - 아직 2회 미만이면 확률적으로 실행
     * - 금요일인 경우 남은 횟수만큼 연속 실행
     *
     * @param today 실행 날짜
     */
    private fun executeIfEligible(today: LocalDate) {
        val currentWeekKey = today.toWeekKey()

        // 새로운 주가 시작되면 카운터 리셋
        if (lastExecutedWeekKey != currentWeekKey) {
            executionCountThisWeek = 0
            lastExecutedWeekKey = currentWeekKey
        }

        // 이미 주당 2회 실행했으면 스킵
        if (executionCountThisWeek >= MAX_EXECUTIONS_PER_WEEK) {
            return
        }

        val day = today.dayOfWeek

        if (day == DayOfWeek.FRIDAY) {
            executeRemainingOnFriday()
            return
        }

        if (!shouldExecute(day)) {
            logger.info("Skipped weekday notification on $day (random)")
            return
        }

        val executed =
            runSchedulerSafely(
                logger = logger,
                failureMessage = "Failed to execute weekday notification scheduler",
            ) {
                weekdayNotificationService.sendWeekdayNotificationsSync()
            }

        if (executed) {
            executionCountThisWeek++
        }
    }

    private fun executeRemainingOnFriday() {
        while (executionCountThisWeek < MAX_EXECUTIONS_PER_WEEK) {
            val executed =
                runSchedulerSafely(
                    logger = logger,
                    failureMessage = "Failed to execute weekday notification scheduler",
                ) {
                    weekdayNotificationService.sendWeekdayNotificationsSync()
                }

            if (!executed) {
                return
            }

            executionCountThisWeek++
        }
    }

    private fun shouldExecute(day: DayOfWeek): Boolean =
        when {
            day == DayOfWeek.THURSDAY && executionCountThisWeek == 0 -> {
                Random.nextDouble() < THURSDAY_BOOSTED_PROBABILITY
            }
            else -> Random.nextDouble() < EXECUTION_PROBABILITY
        }
}
