package com.pida.docs.support

enum class DocsTag(
    val tagName: String,
) {
    /** New */
    AUTH("🔑 Authentication API"),
    USER("🧍🏻 User API"),
    DDAY("🗒️ D-Day API"),
    VARIABLE("⚙️ Variable API"),
    NOTIFICATION("🔔 Notification API"),
    PRIZE_MONEY("💸 PrizeMoney API"),
    DEPOSIT("💰 Deposit API"),
    BANK("🏧 Bank API"),
    FOLLOW("👫🏻 Follow API"),
    COUPON("\uD83C\uDFF7\uFE0F Coupon API"),
    CHALLENGE("🔥 Challenge API"),
    ORDER("🛵 Order API"),
    CHALLENGER("\uD83E\uDDCD\uD83C\uDFFB \uD83D\uDD25Challenger API"),
    BANNER("📢 Banner API"),
    FILMING("📷 Filming API"),
    RETURNS("🧾 Returns API"),
    RANKING("👑 Ranking API"),
    NOTICE("📣 Notice API"),
    APP_VERSION("📱 App-Version API"),
    WORD("🧐 Word API"),
    PAYMENT("Payment API"),

    /** V2 */
    FILMING_V2("📷 Filming API V2"),

    /** Legacy */
    LEGACY_AUTH("🔑 Legacy-Authentication API"),
    LEGACY_USER("🧍🏻Legacy-User API"),
    LEGACY_DDAY("🗒️ Legacy-D-Day API"),
    LEGACY_VARIABLE("⚙️ Legacy-Variable API"),
    LEGACY_NOTIFICATION("🔔 Legacy-Notification API"),
    LEGACY_FOLLOW("👫🏻 Legacy-Follow API"),
}
