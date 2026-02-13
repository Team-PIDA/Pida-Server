package com.pida.fixture.user

import com.navercorp.fixturemonkey.kotlin.setExp
import com.pida.auth.SocialType
import com.pida.test.helper.fixtureBuilder
import com.pida.user.SocialUser
import com.pida.user.User
import com.pida.user.UserProfile
import java.time.LocalDateTime

object UserFixture {
    val user =
        fixtureBuilder<User> {
            setExp(User::id, 1L)
            setExp(User::key, "20250322_UK_acf36b54e803473bb9b989dfde3ac951")
        }

    val userProfile =
        fixtureBuilder<UserProfile> {
            setExp(UserProfile::id, 1L)
            setExp(UserProfile::key, "20250322_UK_acf36b54e803473bb9b989dfde3ac951")
            setExp(UserProfile::email, "uiurihappy@gmail.com")
            setExp(UserProfile::name, "차윤범")
            setExp(UserProfile::nickname, "찰리")
            setExp(UserProfile::createdAt, LocalDateTime.of(2025, 3, 22, 2, 0, 0))
        }

    val socialUser =
        fixtureBuilder<SocialUser> {
            setExp(SocialUser::id, 2L)
            setExp(SocialUser::key, "20250328_UK_c85eef638c604c5db28b08af94f3b258")
            setExp(SocialUser::name, "차윤범")
            setExp(SocialUser::socialId, "010246.3216cb16c25d42a4b00c97f6600ff9b5.2396")
            setExp(SocialUser::socialType, SocialType.APPLE)
        }
}
