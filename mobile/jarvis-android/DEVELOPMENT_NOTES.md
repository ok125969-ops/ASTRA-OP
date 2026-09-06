# JARVIS Mobile (scaffold)

This folder contains a minimal scaffold for the JARVIS Android application described in MOBILE_SPEC.md.

What I added in this commit:

- A multi-module Gradle scaffold: `:app` (Android) and `:core` (JVM library for core logic)
- Core interfaces for AI providers, a simple OfflineAIProvider (streaming and non-streaming)
- ConversationManager and ContextManager scaffolds
- Memory and Tool system interfaces with simple in-memory implementations
- Permission manager wrapper and a simple planner helper
- Basic Android MainActivity and AndroidManifest with required permissions
- Unit tests for core provider and conversation streaming (JVM unit tests)

Next steps (what I will do after you review / allow me to continue):
- Implement Phase 3 voice engine adapters and Android speech integration
- Implement encrypted local memory (Room + encrypted prefs) and migration tests
- Implement tool execution wiring, permission checks and UI integration
- Implement assistant integration utilities and manifest role declarations

To build and run tests locally:

```
cd mobile/jarvis-android
./gradlew :core:test
./gradlew :app:assembleDebug
```

