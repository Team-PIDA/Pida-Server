package com.pida.authentication.storage.db.core

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class AuthenticationBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Enumerated(value = EnumType.STRING)
    @Column(columnDefinition = "varchar(50)")
    var entityStatus: AuthenticationEntityStatus = AuthenticationEntityStatus.ACTIVE
        protected set

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp
    @Column
    var updatedAt: LocalDateTime? = null
        protected set

    fun active() {
        entityStatus = AuthenticationEntityStatus.ACTIVE
    }

    fun delete() {
        entityStatus = AuthenticationEntityStatus.DELETE
    }
}
