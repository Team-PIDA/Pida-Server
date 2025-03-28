package com.pida.support

import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Table
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CleanUp : InitializingBean {
    @PersistenceContext
    private lateinit var entityManager: EntityManager
    private lateinit var tables: List<String>

    @Autowired
    private lateinit var redisConnectionFactory: RedisConnectionFactory

    override fun afterPropertiesSet() {
        tables =
            entityManager.metamodel.entities
                .filter {
                    it.javaType.getAnnotation(Entity::class.java) != null
                }.mapNotNull { it.javaType.getAnnotation(Table::class.java)?.name }
    }

    @Transactional
    fun clean() {
        redisConnectionFactory.connection.serverCommands().flushAll()
        entityManager.flush()
        entityManager.clear()
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate()

        tables.forEach { table ->
            entityManager.createNativeQuery("TRUNCATE TABLE $table").executeUpdate()
        }
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate()
    }
}
