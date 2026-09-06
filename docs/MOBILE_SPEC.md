# JARVIS Mobile: Android Implementation Specification

## Project Overview

**JARVIS Mobile** is a futuristic Android AI assistant built with:
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM with Clean Architecture
- **Min SDK:** 30 (Android 11)
- **Target SDK:** 35 (Android 15)

## Project Structure

```
mobile/jarvis-android/
├── app/
│   ├── build.gradle.kts              # App-level build config
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/
│           └── com/astraop/jarvis/
│               ├── MainActivity.kt    # Entry point
│               ├── di/               # Dependency injection
│               ├── ui/               # Jetpack Compose UI
│               │   ├── screens/
│               │   ├── components/
│               │   ├── theme/
│               │   └── navigation/
│               ├── viewmodel/        # MVVM view models
│               ├── core/             # Business logic
│               │   ├── ai/           # AI engine
│               │   ├── voice/        # Speech input/output
│               │   ├── vision/       # Image processing
│               │   ├── memory/       # Conversation history
│               │   ├── tools/        # Tool system
│               │   ├── planning/     # Multi-step planning
│               │   ├── permissions/  # Permission management
│               │   └── security/     # Security utilities
│               ├── data/             # Data layer
│               │   ├── db/           # Room database
│               │   ├── local/        # Shared preferences
│               │   ├── remote/       # API clients
│               │   └── repository/   # Repository pattern
│               └── utils/            # Utilities
├── build.gradle.kts                  # Root build config
├── settings.gradle.kts               # Project settings
├── gradle.properties                 # Gradle properties
└── README.md                         # Setup instructions

```

## Core Modules

### UI Layer (`ui/`)

**Jetpack Compose screens:**

1. **ChatScreen** — Main conversation interface
   - Message list (scrollable history)
   - Input field (text + voice button)
   - AI response streaming display
   - Tool execution state UI
   - Memory/context display

2. **VoiceInputScreen** — Voice interaction UI
   - Animated listening indicator
   - Real-time transcription display
   - Voice activity indicator
   - Cancel/retry buttons

3. **MemoryScreen** — Memory management
   - Conversation history browser
   - User preferences editor
   - Memory search
   - Delete/clear options

4. **SettingsScreen** — Configuration
   - AI provider selection (OpenAI, offline, custom)
   - API key management
   - Voice settings (language, speed, personality)
   - Permission status
   - Dark/light theme

5. **PermissionScreen** — Permission management
   - Explain why permissions are needed
   - Request permissions
   - Show granted/denied status
   - Link to app settings

### ViewModel Layer (`viewmodel/`)

**Key ViewModels:**

```kotlin
class ChatViewModel : ViewModel() {
    val messages: StateFlow<List<Message>>
    val isLoading: StateFlow<Boolean>
    val currentTool: StateFlow<Tool?>
    
    fun sendMessage(text: String)
    fun sendVoiceInput(audio: ByteArray)
    fun cancelRequest()
}

class VoiceViewModel : ViewModel() {
    val isListening: StateFlow<Boolean>
    val transcription: StateFlow<String>
    val audioLevel: StateFlow<Float>
    
    fun startListening()
    fun stopListening()
}

class MemoryViewModel : ViewModel() {
    val conversations: StateFlow<List<Conversation>>
    val userPreferences: StateFlow<UserPreferences>
    
    fun searchMemory(query: String)
    fun deleteConversation(id: String)
}

class SettingsViewModel : ViewModel() {
    val aiProvider: StateFlow<String>
    val voiceConfig: StateFlow<VoiceConfig>
    
    fun updateProvider(provider: String)
    fun updateVoiceSettings(config: VoiceConfig)
}
```

### Core AI Engine (`core/ai/`)

**Interfaces:**

```kotlin
interface AIProvider {
    suspend fun chat(
        messages: List<AIMessage>,
        config: ChatConfig = ChatConfig()
    ): AIResponse
    
    fun streamChat(
        messages: List<AIMessage>,
        callback: StreamingCallback,
        config: ChatConfig = ChatConfig()
    ): Job
    
    suspend fun vision(
        image: Bitmap,
        prompt: String
    ): String
    
    suspend fun embedding(text: String): FloatArray
}

interface StreamingCallback {
    fun onStart()
    fun onToken(token: String)
    fun onComplete(response: String)
    fun onError(error: Throwable)
}

data class AIMessage(
    val role: String, // "user", "assistant", "system"
    val content: String,
    val imageUrl: String? = null
)

data class AIResponse(
    val content: String,
    val toolCalls: List<ToolCall> = emptyList(),
    val reasoning: String? = null,
    val confidence: Float = 1f
)
```

**Implementations:**

```kotlin
class OpenAIProvider(
    private val apiKey: String,
    private val model: String = "gpt-4-turbo"
) : AIProvider {
    // Implementation using OpenAI API
    override suspend fun chat(messages: List<AIMessage>, config: ChatConfig) = 
        // Call OpenAI API
}

class OfflineProvider(
    private val modelPath: String
) : AIProvider {
    // Implementation using local model (Llama.cpp, TFLite, etc.)
    override suspend fun chat(messages: List<AIMessage>, config: ChatConfig) = 
        // Run local model
}

class HybridProvider(
    private val online: AIProvider,
    private val offline: AIProvider
) : AIProvider {
    // Switch between providers based on connectivity/cost
    override suspend fun chat(messages: List<AIMessage>, config: ChatConfig) = 
        if (isOnline()) online.chat(messages, config)
        else offline.chat(messages, config)
}
```

### Voice Engine (`core/voice/`)

**Interfaces:**

```kotlin
interface SpeechRecognizer {
    fun startListening(listener: SpeechListener)
    fun stopListening()
    fun cancel()
}

interface SpeechListener {
    fun onListening(audioLevel: Float)
    fun onPartial(text: String)
    fun onResult(text: String)
    fun onError(error: SpeechError)
}

interface TextToSpeech {
    suspend fun speak(text: String, config: VoiceConfig)
    fun stop()
}

interface VoiceActivityDetector {
    fun detectVoiceActivity(audioBuffer: FloatArray): Boolean
}

data class VoiceConfig(
    val language: String = "en-US",
    val speechRate: Float = 1f,
    val pitch: Float = 1f,
    val personality: String = "professional" // or "friendly", "formal"
)
```

**Android Implementations:**

```kotlin
class AndroidSpeechRecognizer(context: Context) : SpeechRecognizer {
    private val recognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(context)
    
    override fun startListening(listener: SpeechListener) {
        // Use RecognizerIntent to start speech recognition
    }
}

class AndroidTextToSpeech(context: Context) : TextToSpeech {
    private val tts = android.speech.tts.TextToSpeech(context) { }
    
    override suspend fun speak(text: String, config: VoiceConfig) {
        tts.apply {
            language = Locale.forLanguageTag(config.language)
            setSpeechRate(config.speechRate)
            setPitch(config.pitch)
        }
        tts.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null)
    }
}
```

### Vision Engine (`core/vision/`)

**Interfaces:**

```kotlin
interface ImageProcessor {
    fun cropImage(bitmap: Bitmap, region: Rect): Bitmap
    fun resizeImage(bitmap: Bitmap, maxSize: Int): Bitmap
    fun encodeToBase64(bitmap: Bitmap): String
}

interface OCREngine {
    suspend fun extractText(image: Bitmap): String
}

interface ObjectDetector {
    suspend fun detectObjects(image: Bitmap): List<DetectedObject>
}

interface ImageAnalyzer {
    suspend fun analyzeImage(image: Bitmap, prompt: String): String
}

data class DetectedObject(
    val label: String,
    val confidence: Float,
    val boundingBox: Rect
)
```

**Android Implementations:**

```kotlin
class MLKitOCREngine(context: Context) : OCREngine {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    override suspend fun extractText(image: Bitmap): String {
        val inputImage = InputImage.fromBitmap(image, InputImage.ROTATION_0)
        val result = recognizer.process(inputImage).await()
        return result.text
    }
}

class MLKitObjectDetector(context: Context) : ObjectDetector {
    private val detector = ObjectDetection.getClient(ObjectDetectorOptions.DEFAULT_OPTIONS)
    
    override suspend fun detectObjects(image: Bitmap): List<DetectedObject> {
        val inputImage = InputImage.fromBitmap(image, InputImage.ROTATION_0)
        val detections = detector.process(inputImage).await()
        return detections.map { detection ->
            DetectedObject(
                label = detection.labels.first().text,
                confidence = detection.labels.first().confidence,
                boundingBox = detection.boundingBox
            )
        }
    }
}
```

### Memory System (`core/memory/`)

**Interfaces:**

```kotlin
interface ConversationMemory {
    suspend fun addMessage(message: Message)
    suspend fun getHistory(limit: Int = 50): List<Message>
    suspend fun search(query: String): List<Message>
    suspend fun clear()
}

interface UserPreferences {
    suspend fun set(key: String, value: Any)
    suspend fun get(key: String): Any?
    suspend fun getAll(): Map<String, Any>
}

interface TaskMemory {
    suspend fun saveTask(task: Task)
    suspend fun getTask(id: String): Task?
    suspend fun getTasks(project: String): List<Task>
}

interface MemoryStore {
    suspend fun store(key: String, value: String)
    suspend fun retrieve(key: String): String?
    suspend fun delete(key: String)
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user" or "assistant"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val embedding: FloatArray? = null // For semantic search
)
```

**Implementation:**

```kotlin
@Database(entities = [MessageEntity::class], version = 1)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}

@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: MessageEntity)
    
    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<MessageEntity>
    
    @Query("SELECT * FROM messages WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    suspend fun search(query: String): List<MessageEntity>
}
```

### Tool Engine (`core/tools/`)

**Core Interfaces:**

```kotlin
interface Tool {
    val name: String
    val description: String
    val requiredPermissions: List<String>
    val parameters: List<ToolParameter>
    
    suspend fun execute(args: Map<String, Any>): ToolResult
    fun validate(args: Map<String, Any>): Boolean
}

interface ToolRegistry {
    fun register(tool: Tool)
    fun get(name: String): Tool?
    fun getAll(): List<Tool>
    fun search(query: String): List<Tool>
}

interface ToolExecutor {
    suspend fun execute(toolName: String, args: Map<String, Any>): ToolResult
}

data class ToolParameter(
    val name: String,
    val type: String, // "string", "int", "boolean", etc.
    val required: Boolean,
    val description: String
)

sealed class ToolResult {
    data class Success(val data: String) : ToolResult()
    data class Error(val message: String, val code: String = "TOOL_ERROR") : ToolResult()
    data class PermissionDenied(val permission: String) : ToolResult()
}
```

**Built-in Tools:**

```kotlin
class OpenAppTool(context: Context) : Tool {
    override val name = "open_app"
    override val description = "Launch an installed application"
    override val requiredPermissions = emptyList() // Apps are opened without special permission
    override val parameters = listOf(
        ToolParameter("app_name", "string", true, "Name or package name of the app")
    )
    
    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val appName = args["app_name"] as String
        return try {
            val intent = context.packageManager.getLaunchIntentForPackage(appName)
                ?: return ToolResult.Error("App not found: $appName")
            context.startActivity(intent)
            ToolResult.Success("Opened $appName")
        } catch (e: Exception) {
            ToolResult.Error("Failed to open app: ${e.message}")
        }
    }
}

class CreateReminderTool(context: Context) : Tool {
    override val name = "create_reminder"
    override val description = "Create a reminder or alarm"
    override val requiredPermissions = listOf("android.permission.SCHEDULE_EXACT_ALARM")
    override val parameters = listOf(
        ToolParameter("title", "string", true, "Reminder text"),
        ToolParameter("time_in_minutes", "int", true, "Minutes from now"),
        ToolParameter("label", "string", false, "Optional category")
    )
    
    override suspend fun execute(args: Map<String, Any>): ToolResult {
        // Implementation using AlarmManager
    }
}

class WebSearchTool : Tool {
    override val name = "web_search"
    override val description = "Search the web"
    override val requiredPermissions = listOf("android.permission.INTERNET")
    override val parameters = listOf(
        ToolParameter("query", "string", true, "Search query")
    )
    
    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val query = args["query"] as String
        // Use a web search API (e.g., Bing, Google Custom Search)
    }
}
```

### Permission Manager (`core/permissions/`)

```kotlin
interface PermissionChecker {
    fun hasPermission(permission: String): Boolean
    fun shouldShowRationale(permission: String): Boolean
}

interface PermissionRequester {
    fun requestPermission(permission: String, callback: (Boolean) -> Unit)
    fun requestPermissions(permissions: List<String>, callback: (Map<String, Boolean>) -> Unit)
}

class AndroidPermissionManager(
    private val activity: Activity
) : PermissionChecker, PermissionRequester {
    
    override fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == 
            PackageManager.PERMISSION_GRANTED
    }
    
    override fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        if (hasPermission(permission)) {
            callback(true)
            return
        }
        
        // Show rationale if needed
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show dialog explaining why permission is needed
        }
        
        // Request permission
        // Use ActivityResultContracts.RequestPermission()
    }
}
```

## Data Flow: Message to Response

```kotlin
User sends message "What's the weather?"
    ↓
ChatViewModel.sendMessage(text)
    ↓
AIProvider.chat([SystemMessage, HistoryMessages, UserMessage])
    ↓
OpenAI API returns: {
    "role": "assistant",
    "content": "I'll check the weather for you.",
    "tool_calls": [{"tool": "web_search", "args": {"query": "weather"}}]
}
    ↓
ToolExecutor.execute("web_search", {"query": "weather"})
    ↓
WebSearchTool returns: ToolResult.Success(weatherData)
    ↓
AIProvider.chat([..., ToolCallResult])
    ↓
Final response with weather information
    ↓
TextToSpeech.speak(response)
    ↓
Update UI with response
    ↓
ConversationMemory.addMessage(userMessage, assistantMessage)
```

## Database Schema

```sql
-- Conversation messages
CREATE TABLE messages (
    id TEXT PRIMARY KEY,
    role TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    embedding BLOB, -- FloatArray for semantic search
    image_url TEXT
);

-- User preferences
CREATE TABLE user_preferences (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at INTEGER
);

-- Tasks/projects
CREATE TABLE tasks (
    id TEXT PRIMARY KEY,
    project TEXT,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT,
    created_at INTEGER,
    updated_at INTEGER
);

-- Conversations (sessions)
CREATE TABLE conversations (
    id TEXT PRIMARY KEY,
    summary TEXT,
    message_count INTEGER,
    created_at INTEGER,
    updated_at INTEGER
);
```

## Build Configuration

**build.gradle.kts (root):**
```kotlin
plugins {
    id("com.android.application") version "8.1.0"
    kotlin("android") version "2.0.0"
    id("com.google.dagger.hilt.android") version "2.48"
}

android {
    compileSdk = 35
    defaultConfig {
        applicationId = "com.astraop.jarvis"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}

dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    
    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    
    // ML Kit
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:object-detection:17.0.0")
    
    // Camera
    implementation("androidx.camera:camera-core:1.2.3")
    implementation("androidx.camera:camera-camera2:1.2.3")
    
    // Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

## Permissions (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.READ_CALENDAR" />
```

All permissions are requested at runtime using Jetpack's ActivityResultContracts.

## Testing Strategy

- **Unit Tests:** Core AI, memory, tools
- **Integration Tests:** Voice pipeline, tool execution
- **UI Tests:** Jetpack Compose screen tests
- **End-to-End:** Full conversation flow

## Deployment

- **CI/CD:** GitHub Actions (compile, test, build APK)
- **Release:** Firebase App Distribution
- **Monitoring:** Crashlytics, Analytics

---

**Next Phase:** See `DEVELOPMENT.md` for development setup and contribution guidelines.
