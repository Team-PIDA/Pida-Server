package com.pida.support

internal interface TestEntity<ID> {
    var id: ID
}

internal abstract class InMemoryDB<ID, E : TestEntity<ID>> {
    private val database: MutableMap<ID, E> = mutableMapOf()

    protected abstract fun generateId(): ID

    /**
     * InMemoryDB Insert
     *
     * @param entity [E] Entity class
     * @return [entity] of saved [E]
     */
    fun save(entity: E): E? {
        if (database.contains(entity.id)) {
            return null
        }

        entity.id = generateId()
        database[entity.id] = entity

        return entity
    }

    /**
     * InMemoryDB Find By Id
     *
     * @param id [ID] Entity's id
     * @return [E] of saved Entity class
     */
    fun findById(id: ID): E? = database[id]

    /**
     * InMemoryDB Find All
     */
    fun findAll(): MutableList<E> = database.values.toMutableList()

    /**
     * InMemoryDB Update
     *
     * @param id [ID] saved Entity's id
     * @param entity [E] update Entity class
     */
    fun update(
        id: ID,
        entity: E,
    ): E? {
        database[id] ?: return null
        database[id] = entity
        return entity
    }

    /**
     * InMemoryDB Delete By Id
     *
     * @param id [ID] Entity's id
     */
    fun deleteById(id: ID) {
        checkNotNull(database.remove(id)) {
            "Data is Not Found with id#$id"
        }
    }

    /**
     * InMemoryDB Delete All
     */
    fun deleteAll() {
        database.clear()
    }
}
