# compose-git-grass consumer ProGuard rules
# 이 규칙은 AAR에 포함되어 라이브러리를 사용하는 앱에 자동 적용됩니다.

# Public API 클래스 보호
-keep public class com.inseong.gitgrass.GitGrassKt { *; }
-keep public class com.inseong.gitgrass.GitGrassColors { *; }
-keep public class com.inseong.gitgrass.GitGrassDefaults { *; }
-keep public class com.inseong.gitgrass.GitGrassStreakInfo { *; }

# Kotlin data class의 component, copy 메서드 보호
-keepclassmembers class com.inseong.gitgrass.GitGrassColors {
    public <methods>;
}
-keepclassmembers class com.inseong.gitgrass.GitGrassStreakInfo {
    public <methods>;
}

# Compose @Composable 어노테이션 메서드 보호
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# @Immutable 어노테이션 클래스 보호 (Compose 안정성 추론)
-keep @androidx.compose.runtime.Immutable class * { *; }
