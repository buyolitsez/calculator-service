package com.github.heheteam

import com.github.heheteam.expr.Value
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.math.max

data class Entry(val exprString: String, val value: Result<Value, Error>) {
    private fun toEntrySuccess(): EntrySuccess {
        return EntrySuccess(exprString, value.value)
    }

    private fun toEntryError(): EntryError {
        return EntryError(exprString, value.error)
    }

    fun toSerializable(): EntrySerializable {
        return if (value.isOk) {
            toEntrySuccess()
        } else {
            toEntryError()
        }
    }
}

@Serializable
sealed class EntrySerializable {
    abstract val exprString: String

    abstract fun toEntry(): Entry
}

@Serializable
data class EntrySuccess(override val exprString: String, val value: Value) : EntrySerializable() {
    override fun toEntry(): Entry {
        return Entry(exprString, Ok(value))
    }
}

@Serializable
data class EntryError(override val exprString: String, val error: Error) : EntrySerializable() {
    override fun toEntry(): Entry {
        return Entry(exprString, Err(error))
    }
}

class Database private constructor(private val path: Path, private val allEntries: MutableList<EntrySerializable>) {
    private constructor(path: Path) : this(path, mutableListOf()) {
        Files.write(path, "[]".toByteArray())
    }

    fun appendEntry(entry: Entry) {
        allEntries.add(entry.toSerializable())
        Files.write(path, Json.encodeToString(allEntries).toByteArray(), StandardOpenOption.WRITE)
    }

    fun queryLastEntries(count: Int): List<Entry> {
        return allEntries.slice(max(0, allEntries.size - count)..<allEntries.size).map {
            it.toEntry()
        }
    }

    fun getLatestItemsForHistory(count: Int): String {
        return Json.encodeToString(queryLastEntries(count).map { it.toSerializable() })
    }

    companion object {
        fun tryToFindDatabase(path: Path): Result<Database, FileOpenError> {
            if (!Files.exists(path)) {
                return Err(FileOpenError("File does not exist"))
            }
            if (!Files.isReadable(path)) {
                return Err(FileOpenError("File is not readable"))
            }
            if (!Files.isWritable(path)) {
                return Err(FileOpenError("File is not writable"))
            }
            return Ok(
                Database(
                    path,
                    Json.decodeFromString<List<EntrySerializable>>(Files.readString(path)).toMutableList(),
                ),
            )
        }

        fun createEmptyDatabase(path: Path): Result<Database, FileOpenError> {
            try {
                Files.createFile(path)
            } catch (e: Exception) {
                return Err(FileOpenError("Error creating the file"))
            }
            if (!Files.isReadable(path)) {
                return Err(FileOpenError("File is not readable"))
            }
            if (!Files.isWritable(path)) {
                return Err(FileOpenError("File is not writable"))
            }
            return Ok(Database(path))
        }

        fun openDatabase(path: Path): Result<Database, FileOpenError> {
            return tryToFindDatabase(path).onFailure {
                createEmptyDatabase(path)
            }
        }
    }
}