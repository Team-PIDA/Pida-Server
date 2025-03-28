package com.pida.storage.db.core.support
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryWhereStep
import kotlin.reflect.KClass

class JDSLExtensions : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<JDSLExtensions> {
        override fun newInstance(): JDSLExtensions = JDSLExtensions()
    }

    inline fun <reified T : Any> selectFrom(type: KClass<T>): SelectQueryWhereStep<T> {
        val entity = entity(type)
        return select(entity).from(entity)
    }
}
