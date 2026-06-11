plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.dokka)
    jacoco
}

android {
    namespace = "com.inseong.gitgrass"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

composeCompiler {
    val reportsEnabled = providers.gradleProperty("composeCompilerReports")
        .map { it.equals("true", ignoreCase = true) }
        .getOrElse(false)

    if (reportsEnabled) {
        reportsDestination = layout.buildDirectory.dir("compose_compiler/reports")
        metricsDestination = layout.buildDirectory.dir("compose_compiler/metrics")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
    )

    val debugTree = fileTree(
        layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
    ) { exclude(fileFilter) }

    val kotlinDebugTree = fileTree(
        layout.buildDirectory.dir("tmp/kotlin-classes/debug")
    ) { exclude(fileFilter) }

    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

tasks.register<JacocoCoverageVerification>("jacocoCoverageVerification") {
    dependsOn("jacocoTestReport")

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
    )

    val debugTree = fileTree(
        layout.buildDirectory.dir("intermediates/javac/debug/compileDebugJavaWithJavac/classes")
    ) { exclude(fileFilter) }

    val kotlinDebugTree = fileTree(
        layout.buildDirectory.dir("tmp/kotlin-classes/debug")
    ) { exclude(fileFilter) }

    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("io.github.ois0886", "compose-git-grass", "1.1.1")

    pom {
        name.set("Compose Git Grass")
        description.set("GitHub contribution graph (grass) UI component for Jetpack Compose")
        url.set("https://github.com/ois0886/compose-git-grass")
        inceptionYear.set("2026")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("ois0886")
                name.set("Inseong")
                url.set("https://github.com/ois0886")
            }
        }

        scm {
            url.set("https://github.com/ois0886/compose-git-grass")
            connection.set("scm:git:git://github.com/ois0886/compose-git-grass.git")
            developerConnection.set("scm:git:ssh://git@github.com/ois0886/compose-git-grass.git")
        }
    }
}
