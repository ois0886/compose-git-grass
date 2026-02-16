# compose-git-grass

GitHub contribution graph (grass) UI component library for Jetpack Compose.

## Project Structure

- **`:library`** - Published library module (`com.inseong:compose-git-grass`)
  - Package: `com.inseong.gitgrass`
  - Contains the `GitGrass` composable and related APIs
- **`:app`** - Sample/demo application
  - Package: `com.inseong.compose_git_grass`
  - Depends on `:library` for development/testing

## Build Commands

```bash
# Build library
./gradlew :library:assembleDebug

# Build sample app
./gradlew :app:assembleDebug

# Publish to local Maven (~/.m2)
./gradlew :library:publishToMavenLocal

# Publish to Maven Central
./gradlew :library:publishAndReleaseToMavenCentral
```

## Publishing

Uses [vanniktech/gradle-maven-publish-plugin](https://github.com/vanniktech/gradle-maven-publish-plugin).

Maven coordinates: `io.github.ois0886:compose-git-grass:<version>`

Required credentials in `~/.gradle/gradle.properties`:
```properties
mavenCentralUsername=<Sonatype Central Portal username>
mavenCentralPassword=<Sonatype Central Portal password>
signing.keyId=<GPG key ID (last 8 chars)>
signing.password=<GPG key passphrase>
signing.secretKeyRingFile=<path to secring.gpg>
```

## Code Conventions

- Kotlin, Jetpack Compose
- Min SDK 26, Compile SDK 36
- Java 11 source/target compatibility
- Version catalog: `gradle/libs.versions.toml`
