# JARVIS Mobile: Android Implementation Specification

> NOTE: architecture updated. See docs/ARCHITECTURE.md for the AI Operating Layer vision, capability model, and unified runtime design.

## Project Overview

**JARVIS Mobile** is a futuristic Android AI assistant built with:
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM with Clean Architecture
- **Min SDK:** 30 (Android 11)
- **Target SDK:** 35 (Android 15)

(Existing spec continues...)

## Architectural note

This repository follows the AI Operating Layer model. All modality inputs (voice, text, image, assistant invocation) must be routed through a single JARVIS runtime pipeline (Input Adapter → Context → Reasoning → Planner → Tool Execution → Response). Voice is implemented as a modality that uses the central Conversation/Planner/Tool pipeline; it does NOT directly call tools or bypass permission checks.

(For details see docs/ARCHITECTURE.md)
