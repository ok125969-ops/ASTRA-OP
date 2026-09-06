## Phase 3 - Implemented Permission & Tool Execution Flow

Phase 3 implements the gating and permission model for tool execution and voice as a modality.

Key implemented behaviors (feature/jarvis-phase-3-voice):

- AppCapabilityManager: app-side capability detection using PackageManager and PermissionChecker. Reports AVAILABLE, PERMISSION_REQUIRED, NOT_SUPPORTED, etc.
- AppPermissionChecker: checks Android runtime permission state via ContextCompat.
- AppPermissionRequester: integrates with ActivityResultLauncher to request runtime permissions from the Activity/Compose layer and deliver callbacks into the core PermissionRequester interface.
- VoiceViewModel: exposes StateFlows for voice state, transcript, showRationale, and openSettings. Routes requests through CapabilityManager before starting sessions.
- MainActivity: example wiring that creates an ActivityResultLauncher, AppPermissionRequester, AppPermissionChecker, AppCapabilityManager, VoiceSessionManager and constructs VoiceViewModel for Compose UI.
- ToolExecutorImpl (core): implements Planner → Capability check → Permission request → Confirmation → Execution → Audit (InMemoryAuditStore) flow. (Committed in core module.)
- AuditStore: in-memory audit entries recorded for each tool execution attempt.
- VoiceSessionManager routing tests: ensure transcript -> ConversationManager -> OfflineAIProvider -> TTS flow works (unit test included in core).

Notes:
- Voice remains a modality; VoiceSessionManager hands transcripts to ConversationManager, not directly to AI provider.
- OfflineAIProvider remains the default deterministic provider for testing.
- WakeWordDetector remains a No-op experimental implementation and is OFF by default.

What still requires verification (local Gradle/CI needed):
- Full compile and dependency resolution for Android app module.
- Compose UI wiring and ViewModel lifecycles in actual device/emulator.
- Instrumented tests (capability checks with PackageManager) — these may need Robolectric or Android instrumentation.

Next steps (local verification):
Run the following from mobile/jarvis-android:

```
./gradlew :core:test --stacktrace
./gradlew :app:assembleDebug --stacktrace
./gradlew :app:lint :core:check --stacktrace
```

If any failures occur, paste the full output here and I will triage and fix in-scope Phase 3 issues on the same branch.
