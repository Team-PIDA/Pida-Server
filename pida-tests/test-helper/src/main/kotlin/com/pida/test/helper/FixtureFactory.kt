package com.pida.test.helper

import com.navercorp.fixturemonkey.ArbitraryBuilder
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin

public val FixtureFactory: FixtureMonkey =
    FixtureMonkey
        .builder()
        .plugin(KotlinPlugin())
        .build()

public inline fun <reified T> createFixture(): T = FixtureFactory.giveMeOne(T::class.java)

public inline fun <reified T> fixtureBuilder(block: (ArbitraryBuilder<T>.() -> Unit)): T =
    FixtureFactory
        .giveMeBuilder(T::class.java)
        .apply(block)
        .sample()
