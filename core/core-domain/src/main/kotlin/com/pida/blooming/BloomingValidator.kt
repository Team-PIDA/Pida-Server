package com.pida.blooming

import com.pida.support.error.ErrorException
import com.pida.support.error.ErrorType
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BloomingValidator {
    suspend fun addValidate(blooming: Blooming?) {
        blooming?.let {
            if (it.createdAt.toLocalDate() == LocalDate.now()) {
                throw ErrorException(ErrorType.ALREADY_BLOOMING)
            }
        }
    }
}
