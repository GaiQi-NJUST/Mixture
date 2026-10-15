// Mixture: workspace paths for Termux projects.
package io.github.mixture.termux

object TermuxWorkspace {
    const val WORKSPACE_DIR_NAME = "workspace"
    const val CONFIG_FILE_NAME = "project.json"
    const val LOG_FILE_NAME = "task.log"
    const val SESSION_FILE_NAME = "session.json"

    private val PROJECT_NAME_PATTERN = Regex("^[a-z0-9][a-z0-9_-]{0,63}$")
    private val BAD_SEGMENTS = setOf("", ".", "..")

    fun isValidProjectName(name: String): Boolean {
        if (!PROJECT_NAME_PATTERN.matches(name)) return false
        if (name.contains("..")) return false
        return true
    }

    fun requireValidProjectName(name: String) {
        require(isValidProjectName(name)) { "Invalid project name: $name" }
    }

    fun projectDir(workspaceRoot: String, projectName: String): String {
        requireValidProjectName(projectName)
        val root = workspaceRoot.trimEnd('/')
        require(root.isNotEmpty()) { "Workspace root must not be empty" }
        val dir = "$root/$WORKSPACE_DIR_NAME/$projectName"
        require(isInside(root, dir)) { "Project dir escapes workspace root" }
        return dir
    }

    fun configFile(workspaceRoot: String, projectName: String): String {
        return projectDir(workspaceRoot, projectName) + "/" + CONFIG_FILE_NAME
    }

    fun logFile(workspaceRoot: String, projectName: String): String {
        return projectDir(workspaceRoot, projectName) + "/" + LOG_FILE_NAME
    }

    fun sessionFile(workspaceRoot: String, projectName: String): String {
        return projectDir(workspaceRoot, projectName) + "/" + SESSION_FILE_NAME
    }

    fun isInside(root: String, path: String): Boolean {
        val cleanRoot = root.trimEnd('/')
        val cleanPath = path.trimEnd('/')
        if (cleanPath == cleanRoot) return true
        if (!cleanPath.startsWith(cleanRoot + "/")) return false
        val rest = cleanPath.removePrefix(cleanRoot + "/")
        val parts = rest.split("/")
        return parts.none { it in BAD_SEGMENTS }
    }
}
