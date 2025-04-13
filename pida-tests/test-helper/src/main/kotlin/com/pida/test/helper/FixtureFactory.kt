package com.pida.test.helper

import com.navercorp.fixturemonkey.ArbitraryBuilder
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin

val FixtureFactory: FixtureMonkey =
    FixtureMonkey
        .builder()
        .plugin(KotlinPlugin())
        .build()

inline fun <reified T> createFixture(): T = FixtureFactory.giveMeOne(T::class.java)

inline fun <reified T> fixtureBuilder(block: (ArbitraryBuilder<T>.() -> Unit)): T =
    FixtureFactory
        .giveMeBuilder(T::class.java)
        .apply(block)
        .sample()
