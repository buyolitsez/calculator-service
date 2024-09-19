package com.github.heheteam

import com.github.heheteam.expr.Value
import com.github.michaelbull.result.Result
import java.nio.file.Path


data class Entry(val exprString: String, val value: Result<Value, Error>)

class Database {

    fun appendEntry(entry: Entry) {
        TODO()
    }

    fun queryLastEntries(count: Int): List<Entry> {
        TODO()
    }

    companion object {
        fun tryToFindDatabase(path: Path): Result<Database, FileOpenError> {
            TODO("TODO")
        }

        fun createEmptyDatabase(path: Path): Result<Database, FileOpenError> {
            TODO()
        }
    }
}