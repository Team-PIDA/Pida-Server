package com.pida.storage.db.core.support

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import kotlin.reflect.full.isSubclassOf

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp
    @Column
    var updatedAt: LocalDateTime? = null
        protected set

    @Column
    var deletedAt: LocalDateTime? = null
        protected set

    fun softDelete() {
        this.deletedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false
        if (!this::class.isSubclassOf(other::class) || other::class.isSubclassOf(this::class)) return false
        if (id == null || other.id == null) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
