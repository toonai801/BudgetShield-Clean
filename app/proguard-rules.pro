# Budget Shield release shrinking rules.

# Room schemas, entities, and DAOs are referenced through generated code and
# annotation processing. Keep model metadata conservative until release QA has
# broader signed-artifact coverage.
-keep class com.toonai.budgetshield.data.model.** { *; }
-keep class com.toonai.budgetshield.data.database.** { *; }

# Hilt entry points and generated components are referenced reflectively by the
# Android runtime and Hilt internals.
-keep class dagger.hilt.** { *; }
-keep class hilt_aggregated_deps.** { *; }
-keep class *_HiltModules_* { *; }
-keep class *_GeneratedInjector { *; }

# Navigation keys are serialized through Kotlin serialization.
-keep class com.toonai.budgetshield.navigation.** { *; }

