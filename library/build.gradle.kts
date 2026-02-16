plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.inseong.gitgrass"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
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
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates("io.github.ois0886", "compose-git-grass", "0.1.0")

    pom {
        name.set("Compose Git Grass")
        description.set("GitHub contribution graph (grass) UI component for Jetpack Compose")
        url.set("https://github.com/ois0886/compose-git-grass")
        inceptionYear.set("2025")

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
