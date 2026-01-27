package com.pida.scheduler

import com.pida.notification.weekday.WeekdayNotificationService
import com.pida.support.extension.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import kotlin.random.Random

/**
 * 평일 힐링 푸시 알림 스케줄러
 *
 * 평일(월~금) 중 주당 랜덤으로 2회, 오후 6시에 실행
 */
@Component
class WeekdayNotificationScheduler(
    private val weekdayNotificationService: WeekdayNotificationService,
) {
    private val logger by logger()
    private var executionCountThisWeek = 0
    private var lastExecutedWeek: Int? = null

    companion object {
        private const val MAX_EXECUTIONS_PER_WEEK = 2
        private val EXECUTION_PROBABILITY = 0.4 // 월~금 5일 중 2회 실행 확률 (2/5 = 0.4)
    }

    // 월요일 오후 6시
    @Scheduled(cron = "0 0 18 ? * MON")
    fun mondayCheck() {
        executeIfEligible(DayOfWeek.MONDAY)
    }

    // 화요일 오후 6시
    @Scheduled(cron = "0 0 18 ? * TUE")
    fun tuesdayCheck() {
        executeIfEligible(DayOfWeek.TUESDAY)
    }

    // 수요일 오후 6시
    @Scheduled(cron = "0 0 18 ? * WED")
    fun wednesdayCheck() {
        executeIfEligible(DayOfWeek.WEDNESDAY)
    }

    // 목요일 오후 6시
    @Scheduled(cron = "0 0 18 ? * THU")
    fun thursdayCheck() {
        executeIfEligible(DayOfWeek.THURSDAY)
    }

    // 금요일 오후 6시
    @Scheduled(cron = "0 0 18 ? * FRI")
    fun fridayCheck() {
        executeIfEligible(DayOfWeek.FRIDAY)
    }

    /**
     * 평일 알림 실행 여부 결정
     *
     * - 이번 주에 이미 2회 실행했으면 스킵
     * - 아직 2회 미만이면 확률적으로 실행
     * - 금요일인 경우 아직 2회 실행하지 않았다면 무조건 실행
     *
     * @param day 요일
     */
    private fun executeIfEligible(day: DayOfWeek) {
        val currentWeek = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)

        // 새로운 주가 시작되면 카운터 리셋
        if (lastExecutedWeek != currentWeek) {
            executionCountThisWeek = 0
            lastExecutedWeek = currentWeek
        }

        // 이미 주당 2회 실행했으면 스킵
        if (executionCountThisWeek >= MAX_EXECUTIONS_PER_WEEK) {
            logger.info("Weekday notification already sent $MAX_EXECUTIONS_PER_WEEK times this week, skipping $day")
            return
        }

        val shouldExecute =
            when {
                // 금요일인데 아직 2회 실행 안 했으면 무조건 실행
                day == DayOfWeek.FRIDAY && executionCountThisWeek < MAX_EXECUTIONS_PER_WEEK -> {
                    val remaining = MAX_EXECUTIONS_PER_WEEK - executionCountThisWeek
                    logger.info("Friday: executing all remaining notifications ($remaining)")
                    true
                }
                // 목요일인데 아직 1회도 실행 안 했으면 확률 높여서 실행
                day == DayOfWeek.THURSDAY && executionCountThisWeek == 0 -> {
                    Random.nextDouble() < 0.6 // 목요일까지 1회도 안 했으면 60% 확률
                }
                // 그 외의 경우 기본 확률로 실행
                else -> Random.nextDouble() < EXECUTION_PROBABILITY
            }

        if (shouldExecute) {
            logger.info("Executing weekday notification on $day (count: ${executionCountThisWeek + 1}/$MAX_EXECUTIONS_PER_WEEK)")
            weekdayNotificationService.sendWeekdayNotifications()
            executionCountThisWeek++

            // 금요일이고 아직 2회 실행 안 했으면 반복 실행
            if (day == DayOfWeek.FRIDAY && executionCountThisWeek < MAX_EXECUTIONS_PER_WEEK) {
                executeIfEligible(day)
            }
        } else {
            logger.info("Skipped weekday notification on $day (random)")
        }
    }
}
