import com.github.heheteam.Database
import com.github.heheteam.Entry
import com.github.heheteam.ZeroDivisionError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDatabase {
    val path: Path = FileSystems.getDefault().getPath("test_database.json")
    val entrySuccess: Entry = Entry("2+3", Ok(5.0))
    val entryError: Entry = Entry("6:0", Err(ZeroDivisionError("Division by zero")))

    val empty = "[]"
    val with1Query = "[{\"type\":\"com.github.heheteam.EntrySuccess\",\"exprString\":\"2+3\",\"value\":5.0}]"

    @Test fun testCreateEmptyDatabase() {
        val res = Database.createEmptyDatabase(path)
        assert(res.isOk)
    }

    @Test fun testTryToCreateDatabaseThatAlreadyExists() {
        Files.createFile(path)
        Files.write(path, empty.toByteArray())

        val res = Database.createEmptyDatabase(path)
        assert(res.isErr)
        assertEquals("Error creating the file", res.error.message)
    }

    @Test fun testOpenEmptyDatabase() {
        Files.createFile(path)
        Files.write(path, empty.toByteArray())

        val res = Database.tryToFindDatabase(path)
        assert(res.isOk)
    }

    @Test fun testAppendAndQueryEntry() {
        Files.createFile(path)
        Files.write(path, empty.toByteArray())

        val res = Database.tryToFindDatabase(path)
        res.value.appendEntry(entrySuccess)
        val list = res.value.queryLastEntries(1)
        assertEquals(1, list.size)
        val entry = list[0]
        assertEquals(entrySuccess.exprString, entry.exprString)
        assertEquals(entrySuccess.value, entry.value)
    }

    @Test fun testOpenNonEmptyDatabase() {
        Files.createFile(path)
        Files.write(path, with1Query.toByteArray())

        val res = Database.tryToFindDatabase(path)
        val list = res.value.queryLastEntries(1)
        assertEquals(1, list.size)
        val entry = list[0]
        assertEquals(entrySuccess.exprString, entry.exprString)
        assert(entry.value.isOk)
        assertEquals(entrySuccess.value.value, entry.value.value)
    }

    @Test fun testGetLatestItemsForHistory() {
        Files.createFile(path)
        Files.write(path, with1Query.toByteArray())

        val res = Database.tryToFindDatabase(path)
        assertEquals(with1Query, res.value.getLatestItemsForHistory(1))
    }

    @Test fun testAppendAndQueryEntryToANonEmptyDatabase() {
        Files.createFile(path)
        Files.write(path, with1Query.toByteArray())

        val res = Database.tryToFindDatabase(path)
        res.value.appendEntry(entryError)
        val list = res.value.queryLastEntries(2)
        assertEquals(2, list.size)
        val entry1 = list[0]
        val entry2 = list[1]
        assertEquals(entrySuccess.exprString, entry1.exprString)
        assertEquals(entryError.exprString, entry2.exprString)
        assert(entry1.value.isOk)
        assert(entry2.value.isErr)
        assertEquals(entrySuccess.value.value, entry1.value.value)
        assertEquals(entryError.value.error.message, entry2.value.error.message)
    }

    @AfterTest fun deleteFile() {
        Files.delete(path)
    }
}
