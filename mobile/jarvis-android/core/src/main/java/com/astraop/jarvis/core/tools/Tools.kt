package com.astraop.jarvis.core.tools

/**
 * Tool system scaffolding for Phase 5: defines Tool, ToolResult and a registry.
 */
interface Tool {
    val name: String
    val description: String
    val requiredPermissions: List<String>

    suspend fun execute(args: Map<String, Any>): ToolResult
    fun validate(args: Map<String, Any>): Boolean
}

sealed class ToolResult {
    data class Success(val data: String) : ToolResult()
    data class Error(val message: String, val code: String = "TOOL_ERROR") : ToolResult()
    data class PermissionDenied(val permission: String) : ToolResult()
}

class ToolRegistry {
    private val tools = mutableMapOf<String, Tool>()

    fun register(tool: Tool) {
        tools[tool.name] = tool
    }

    fun get(name: String): Tool? = tools[name]

    fun getAll(): List<Tool> = tools.values.toList()
}

/**
 * A safe WebSearchTool stub (does not call network in core). Implementations on Android can use Retrofit/OkHttp.
 */
class WebSearchTool : Tool {
    override val name: String = "web_search"
    override val description: String = "Search the web (core stub)"
    override val requiredPermissions: List<String> = listOf()

    override suspend fun execute(args: Map<String, Any>): ToolResult {
        val q = args["query"] as? String ?: return ToolResult.Error("missing query")
        // Return a deterministic mocked result for tests
        return ToolResult.Success("[stubbed search results for] $q")
    }

    override fun validate(args: Map<String, Any>): Boolean = args.containsKey("query")
}
