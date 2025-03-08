package com.pida.test.helper

import com.navercorp.fixturemonkey.kotlin.setExp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

data class TestMember(
    val key: String,
    val name: String,
) {
    companion object {
        fun fixture(): TestMember = createFixture()
    }
}

class FixtureTests {
    @Test
    fun `test member identity`() {
        val newMembers =
            (1..5).map {
                TestMember.fixture()
            }
        assertThat(newMembers).hasSize(5)
    }

    @Test
    fun `test arbitrary builder`() {
        val newMember: TestMember =
            fixtureBuilder {
                setExp(TestMember::key, UUID.randomUUID().toString())
            }

        assertThat(newMember).isNotNull
    }
}
