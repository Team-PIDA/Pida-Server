package com.pida.scheduler

import com.pida.support.extension.logger
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * 저녁 알림 스케줄러 실행 경합 방지를 위한 분산 락
 */
@Component
class NotificationExecutionLock(
    @param:Qualifier("coreRedissonClient")
    private val redissonClient: RedissonClient,
) {
    private val logger by logger()

    companion object {
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE
        private const val LOCK_KEY_PREFIX = "notification:evening"
        private const val WAIT_SECONDS = 1L
        private const val LEASE_SECONDS = 3600L
    }

    enum class ExecutionResult {
        ACQUIRED,
        SKIPPED_BY_CONTENTION,
        FAIL_OPEN,
    }

    fun runWithEveningLock(
        date: LocalDate = LocalDate.now(),
        action: () -> Unit,
    ): ExecutionResult {
        val lockKey = "$LOCK_KEY_PREFIX:${date.format(DATE_FORMATTER)}"

        val lock =
            try {
                redissonClient.getLock(lockKey)
            } catch (error: RuntimeException) {
                logger.error(
                    "Failed to create evening notification lock. Executing action with fail-open: key=$lockKey, date=$date, errorType=${error::class.simpleName}",
                    error,
                )
                action()
                return ExecutionResult.FAIL_OPEN
            }

        val acquired =
            try {
                lock.tryLock(WAIT_SECONDS, LEASE_SECONDS, TimeUnit.SECONDS)
            } catch (error: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn("Interrupted while acquiring evening notification lock: $lockKey", error)
                return ExecutionResult.SKIPPED_BY_CONTENTION
            } catch (error: RuntimeException) {
                logger.error(
                    "Failed to acquire evening notification lock. Executing action with fail-open: key=$lockKey, date=$date, errorType=${error::class.simpleName}",
                    error,
                )
                action()
                return ExecutionResult.FAIL_OPEN
            }

        if (!acquired) {
            logger.info("Skipped evening notifications due to lock contention: $lockKey")
            return ExecutionResult.SKIPPED_BY_CONTENTION
        }

        return try {
            action()
            ExecutionResult.ACQUIRED
        } finally {
            runCatching {
                if (lock.isHeldByCurrentThread) {
                    lock.unlock()
                }
            }.onFailure { error ->
                logger.warn(
                    "Failed to release evening notification lock: $lockKey, errorType=${error::class.simpleName}",
                    error,
                )
            }
        }
    }
}
