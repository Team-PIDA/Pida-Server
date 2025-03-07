package com.pida.test.helper

import com.navercorp.fixturemonkey.kotlin.setExp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

data class TestUser(
    val key: String,
    val name: String,
) {
    companion object {
        fun fixture(): TestUser = createFixture()
    }
}

class FixtureTests {
    @Test
    fun `test user identity`() {
        val newUsers =
            (1..5).map {
                TestUser.fixture()
            }
        assertThat(newUsers).hasSize(5)
    }

    @Test
    fun `test arbitrary builder`() {
        val newUser: TestUser =
            fixtureBuilder {
                setExp(TestUser::key, UUID.randomUUID().toString())
            }

        assertThat(newUser).isNotNull
    }
}
