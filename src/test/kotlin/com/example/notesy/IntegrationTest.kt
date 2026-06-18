package com.example.notesy

import kotlin.test.*
import io.ktor.server.testing.testApplication
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class IntegrationTest {

    @BeforeTest
    fun clearState() {
        // clear repositories to keep tests isolated
        NotesRepository.getAllNotes().map { it.id }.forEach { NotesRepository.deleteNote(it) }
        FoldersRepository.getAllFolders().map { it.id }.forEach { FoldersRepository.deleteFolder(it) }
    }

    @Test
    fun testGetNotesReturns200() = testApplication {
        application { module() }

        // seed a note via repository so GET returns something
        NotesRepository.addNote(
            Note(id = "n-int-1", title = "Title-Int", content = "Content", folderId = null, createdAt = "now")
        )

        val response: HttpResponse = client.get("/notes")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        assertTrue(body.contains("Title-Int"), "Response should contain the seeded note title")
    }

    @Test
    fun testPostNotesCreates() = testApplication {
        application { module() }

        val createJson = """{ "title": "Created", "content": "creds", "folderId": null }"""

        val response: HttpResponse = client.post("/notes") {
            contentType(ContentType.Application.Json)
            setBody(createJson)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("\"title\":\"Created\""))

        // ensure repository contains the created note
        val all = NotesRepository.getAllNotes()
        assertTrue(all.any { it.title == "Created" })
    }

    @Test
    fun testPostNotesInvalidBodyReturns400() = testApplication {
        application { module() }

        val response: HttpResponse = client.post("/notes") {
            contentType(ContentType.Application.Json)
            // missing required fields
            setBody("{ \"bad\": \"x\" }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testUnknownNoteReturns404() = testApplication {
        application { module() }

        val response: HttpResponse = client.get("/notes/does-not-exist")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}

