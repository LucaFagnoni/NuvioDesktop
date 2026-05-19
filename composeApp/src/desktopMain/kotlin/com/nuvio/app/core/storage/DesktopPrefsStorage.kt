package com.nuvio.app.core.storage

import java.io.File
import java.util.prefs.Preferences

/**
 * Hybrid storage: uses java.util.prefs for small values and
 * file-based storage for values exceeding the 8KB Preferences limit.
 */
internal object DesktopPrefsStorage {
    private val prefs: Preferences = Preferences.userRoot().node("com/nuvio/app")

    private val storageDir: File by lazy {
        val dir = File(System.getProperty("user.home"), ".nuvio${File.separator}storage")
        dir.mkdirs()
        dir
    }

    // java.util.prefs max value length is 8192 chars
    private const val MAX_PREFS_LENGTH = 8000

    fun getString(key: String): String? {
        // Check file storage first (for large values)
        val file = fileForKey(key)
        if (file.exists()) {
            return try { file.readText(Charsets.UTF_8) } catch (_: Exception) { null }
        }
        return prefs.get(key, null)
    }

    fun putString(key: String, value: String) {
        val file = fileForKey(key)
        if (value.length > MAX_PREFS_LENGTH) {
            // Store in file, remove from prefs if present
            try {
                file.writeText(value, Charsets.UTF_8)
            } catch (_: Exception) { }
            try { prefs.remove(key) } catch (_: Exception) { }
        } else {
            // Store in prefs, remove file if present
            prefs.put(key, value)
            prefs.flush()
            if (file.exists()) file.delete()
        }
    }

    fun getBoolean(key: String): Boolean? {
        return if (prefs.get(key, null) != null) prefs.getBoolean(key, false) else null
    }

    fun putBoolean(key: String, value: Boolean) {
        prefs.putBoolean(key, value)
        prefs.flush()
    }

    fun getInt(key: String): Int? {
        return if (prefs.get(key, null) != null) prefs.getInt(key, 0) else null
    }

    fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
        prefs.flush()
    }

    fun getFloat(key: String): Float? {
        return if (prefs.get(key, null) != null) prefs.getFloat(key, 0f) else null
    }

    fun putFloat(key: String, value: Float) {
        prefs.putFloat(key, value)
        prefs.flush()
    }

    fun remove(key: String) {
        prefs.remove(key)
        prefs.flush()
        val file = fileForKey(key)
        if (file.exists()) file.delete()
    }

    fun getStringSet(key: String): Set<String>? {
        val raw = getString(key) ?: return null
        return if (raw.isEmpty()) emptySet() else raw.split("\u001F").toSet()
    }

    fun putStringSet(key: String, value: Set<String>) {
        putString(key, value.joinToString("\u001F"))
    }

    fun clear() {
        prefs.clear()
        prefs.flush()
        if (storageDir.exists()) {
            storageDir.listFiles()?.forEach { it.delete() }
        }
    }

    private fun fileForKey(key: String): File {
        // Sanitize key for filesystem
        val safeKey = key.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return File(storageDir, "$safeKey.dat")
    }
}
