pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "pida"

include(
    "core:core-api",
    "core:core-domain",
    "core:auth",
)

include(
    "storage:db-core",
    "storage:redis",
)

include(
    "clients:aws-client",
    "clients:notification",
)

include(
    "support:logging",
    "support:monitoring",
    "support:swagger",
)

include(
    "tests:api-docs",
    "tests:test-helper",
    "tests:test-container",
)


