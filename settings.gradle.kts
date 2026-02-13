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
    "pida-core:core-api",
    "pida-core:core-domain",
)

include(
    "pida-storage:db-core",
    "pida-storage:redis",
)

include(
    "pida-clients:airquality-client",
    "pida-clients:aws-client",
    "pida-clients:notification",
    "pida-clients:oauth-client",
    "pida-clients:weather-client",
    "pida-clients:map-client",
)

include(
    "pida-supports:logging",
    "pida-supports:monitoring",
    "pida-supports:swagger",
)

include(
    "pida-tests:api-docs",
    "pida-tests:test-helper",
    "pida-tests:test-container",
)


