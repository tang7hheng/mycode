# Android Project Guidelines

## Project Structure
- Single-module Android app (`:app`)
- Kotlin with Jetpack Compose UI
- Package: `com.example.myapplication`
- Min SDK 24, Target SDK 34

## Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

## Key Configuration Files
- `gradle/libs.versions.toml` - Version catalog (dependencies)
- `app/build.gradle.kts` - App module config
- `local.properties` - Android SDK location (auto-generated, not in VCS)

## Code Organization
- `app/src/main/java/com/example/myapplication/` - Main source
  - `MainActivity.kt` - Entry point
  - `ui/theme/` - Compose theme (Color, Theme, Type)
- `app/src/test/` - Unit tests
- `app/src/androidTest/` - Instrumented tests

## Development Notes
- Uses Compose BOM 2024.04.01
- Kotlin 1.9.0 with Compose compiler 1.5.1
- Java 8 compatibility
- Edge-to-edge UI enabled
- No custom ProGuard rules active
- Gradle user home set to `C:\gradle_home` (Windows)

## Testing
- Unit tests: `./gradlew testDebugUnitTest`
- Instrumented tests: `./gradlew connectedDebugAndroidTest`
- Test runner: AndroidJUnit4

## Common Issues
- Ensure Android SDK at `D:\androidSDK\Sdk` (check local.properties)
- Windows paths in gradle.properties
- Compose preview requires `@Preview` annotations

## Clear Project Command

When user says "清空项目" (clear project), perform these actions:

### Files to DELETE:
- All files in `app/src/main/java/com/example/myapplication/` except `MainActivity.kt`
- All custom resource files in `app/src/main/res/` (keep only default structure)
- All custom dependencies added to `app/build.gradle.kts`
- All test files in `app/src/test/` and `app/src/androidTest/`
- Any additional modules or files added to root directory

### Files to RESET:
- `app/src/main/java/com/example/myapplication/MainActivity.kt` - Reset to minimal Compose template
- `app/build.gradle.kts` - Remove custom dependencies, keep only base Compose setup
- `gradle/libs.versions.toml` - Remove custom versions/libraries, keep only base

### Files to KEEP (do not modify):
- `build.gradle.kts` (root)
- `settings.gradle.kts`
- `gradle.properties`
- `local.properties`
- `.gitignore`
- `gradle/` directory structure
- `app/src/main/AndroidManifest.xml`
- `app/proguard-rules.pro`

### Result:
Project should be a clean, minimal Android Compose app that compiles successfully with only "Hello Android" content.