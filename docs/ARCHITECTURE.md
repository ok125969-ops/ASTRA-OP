# AI Operating Layer Architecture

This document defines the AI Operating Layer architecture for JARVIS and how all subsystems (AI, Voice, Memory, Tools, Agents, Android bridge and Security) connect to the central runtime. It replaces the older, assistant-focused framing and describes the runtime model required for JARVIS to become an intelligent, permission-aware orchestration layer above Android.

## Core idea

JARVIS is an AI operating layer that sits above Android and below the user interaction surfaces. It provides intelligence, reasoning, planning, memory, and a unified execution/runtime environment for actions that are legitimately supported by Android. JARVIS never bypasses Android security — it adapts to and reports device capabilities, permission status, and restrictions.

## High-level runtime

JARVIS Core Runtime
```
                USER
                  ↓
      JARVIS AI OPERATING LAYER (central runtime)
      ├── AI ENGINE (reasoning, providers)
      ├── MEMORY (short/long/preferences)
      ├── MULTIMODAL (voice, vision, text)
      ├── AGENT RUNTIME (planner, tool selection)
      ├── TOOL ENGINE (capability-exposed tools)
      ├── ANDROID SYSTEM BRIDGE (intents, notifications, apps)
      ├── AUTOMATION (tasks, scheduled workflows)
      └── SECURITY (permissions, confirmation, audit)
                  ↓
               ANDROID
                  ↓
              HARDWARE
```

Key architectural principles:
- Single central runtime: all inputs (voice/text/image/assistant invocation) funnel into the same context/reasoning/planning pipeline. Voice is a modality, not an isolated flow.
- Capability-driven: features are exposed via capabilities (Voice, Camera, Apps, Files, Notifications, Media, Web, Assistant). Each reports AVAILABLE, UNAVAILABLE, PERMISSION_REQUIRED, DEVICE_RESTRICTED, or NOT_SUPPORTED.
- Permission-first and auditable: any tool that could change the device or user data must declare required permissions and pass a risk assessment and user confirmation.
- Provider-agnostic AI: an AIProvider abstraction allows online/offline/hybrid models. The planner and agent runtime orchestrate tool calls, not the voice layer.
- Modular, testable and lifecycle-aware: components use coroutines, Flow/StateFlow and repository abstractions.

## Request flow (unified)

1. Input captured (voice/text/image/assistant intent)
2. JARVIS Input Adapter converts modality → canonical Request object
3. Context Builder attaches recent conversation + relevant memory (short/long) and capability status
4. AI Engine (reasoning + intent classification) produces an intent, optional tool call plan, or a natural language response
5. Planner/Agent Runtime validates plan, selects tools from ToolRegistry and evaluates permissions
6. Permission Manager consults user (rationale/confirmation) where required
7. Tools execute via Android System Bridge (intents/APIs) with result validation and audit logging
8. Final response is composed and returned to the UI/TTS

This single pipeline allows the same behavior whether the user spoke "Hey JARVIS, open my notes" or typed the same request.

## Capabilities & Capability Manager

Every device-facing capability is modeled and exposed via CapabilityManager.

Capabilities:
- VOICE
- CAMERA
- NOTIFICATIONS
- APPS
- FILES
- MEDIA
- WEB
- ASSISTANT_ROLE

Capability state values:
- AVAILABLE
- UNAVAILABLE
- PERMISSION_REQUIRED
- DEVICE_RESTRICTED
- NOT_SUPPORTED

The CapabilityManager enables the planner and UI to adapt dynamically.

## Voice as a modality (not an island)

Voice pipeline connects to the central runtime through a thin adapter:

VOICE SUBSYSTEM
```
Microphone -> VAD -> SpeechRecognizer -> (transcript) -> JARVIS INPUT ADAPTER -> Context -> AI Engine -> Planner -> Action/Response -> TTS/UI
```

Important constraints:
- Voice subsystem must never directly execute tools or call Android APIs without going through the planner and permission checks.
- Wake-word detection is modular and optional. On devices where always-on microphone access is restricted, JARVIS will degrade to push-to-talk or foreground-listening with clear UX.
- VoiceSessionManager orchestrates listening, partial transcription, final result, handing the canonical Request to the central runtime and then rendering the streaming response to UI or TTS.

## Agent & Tool Execution contract

- Tools must declare: name, description, parameters, requiredPermissions, and a safe execution contract.
- The planner turns AI output into Tool calls; the ToolExecutor checks permissions via PermissionManager before executing.
- For any tool that could affect user data or other apps, require explicit confirmation (configurable by user preferences and risk assessment).
- Execution results are validated and stored in an audit log (immutable record) for user inspection.

## Memory & Context

- Use layered memory: ShortTermMemory (session-scoped), ConversationMemory, LongTermMemory, and PreferenceMemory.
- ContextBuilder retrieves only relevant memories (semantic search with embeddings when available) and enforces token/context limits when sending to models.
- Provide UI for memory inspection, deletion, and full reset.

## Security & Permissions

- Central PermissionManager coordinates runtime permission checks and rationale UI.
- All tool executions pass through RiskAssessment which triggers user confirmation flows for medium/high-risk actions.
- Sensitive data should not be stored without user consent. Use encryption for local storage (AndroidX Security) and provide retention controls.

## Android System Bridge

- Expose only official Android APIs and supported intents; never use hidden or undocumented APIs.
- Detect whether the device supports ASSISTANT_ROLE, Assistant invocation, and other OS features dynamically and adjust UX accordingly.
- Use foreground services or Activity-bound flows for microphone usage where required by OS for background access.

## Observability & Testing

- Each tool call and permission flow is audited in an execution log for debugging and user review.
- Unit and integration tests mock AI providers, speech/TTS engines, and Android bridges.
- CI runs core unit tests (:core:test) and app assemble/lint; additional device tests run in gated branches.

## Phases mapping to the runtime

- Phase 2: Intelligence foundation — AIProvider, ConversationManager, ContextManager, Planner, ToolRegistry (scaffolded)
- Phase 3: Multimodal voice foundation — Voice subsystem wired to JARVIS Input Adapter and ContextBuilder (will be implemented now)
- Phase 4: Persistent memory — Room + encrypted storage + retention controls + semantic search
- Phase 5: Agent & Tool Runtime — Planner extensions, ToolExecutor with permission gating and confirmation flows
- Phase 6: Android System Integration — Assistant role, intent handling, capability detection, final bridged tools


---

This document is the canonical architecture reference for implementation and for evaluating each phase's changes to ensure they extend the same runtime rather than creating isolated demos.
