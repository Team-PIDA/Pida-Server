package com.pida.support.tx

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class Tx(
    _txAdvice: TxAdvice,
) {
    init {
        txAdvice = _txAdvice
    }

    companion object {
        private lateinit var txAdvice: TxAdvice

        fun <T> writeable(function: () -> T): T = txAdvice.writeable(function)

        fun <T> readable(function: () -> T): T = txAdvice.readable(function)

        fun <T> requiresNew(function: () -> T): T = txAdvice.requiresNew(function)
    }

    @Component
    class TxAdvice {
        @Transactional
        fun <T> writeable(function: () -> T): T = function()

        @Transactional(readOnly = true)
        fun <T> readable(function: () -> T): T = function()

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        fun <T> requiresNew(function: () -> T): T = function()
    }
}
