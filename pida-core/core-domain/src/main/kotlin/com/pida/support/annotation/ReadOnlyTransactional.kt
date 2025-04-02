package com.pida.support.annotation

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
annotation class ReadOnlyTransactional
