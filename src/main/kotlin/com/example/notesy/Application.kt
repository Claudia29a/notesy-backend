package com.example.notesy

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    configureRouting()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Notesy Backend is running!", ContentType.Text.Plain)
        }

        // ===== Folder Endpoints =====

        get("/folders") {
            call.respond(FoldersRepository.getAllFolders())
        }

        get("/folders/{id}") {
            val id = call.parameters["id"]
            val folder = FoldersRepository.getFolderById(id ?: "")
            if (folder != null) {
                call.respond(folder)
            } else {
                call.respond(HttpStatusCode.NotFound, "Folder not found")
            }
        }

        post("/folders") {
            val request = call.receive<CreateFolderRequest>()
            val folder = Folder(
                id = java.util.UUID.randomUUID().toString(),
                name = request.name,
                createdAt = java.time.LocalDateTime.now().toString()
            )
            FoldersRepository.addFolder(folder)
            call.respond(HttpStatusCode.Created, folder)
        }

        put("/folders/{id}") {
            val id = call.parameters["id"] ?: ""
            val request = call.receive<CreateFolderRequest>()
            val existingFolder = FoldersRepository.getFolderById(id)

            if (existingFolder != null) {
                val updatedFolder = Folder(
                    id = id,
                    name = request.name,
                    createdAt = existingFolder.createdAt
                )
                FoldersRepository.updateFolder(id, updatedFolder)
                call.respond(HttpStatusCode.OK, updatedFolder)
            } else {
                call.respond(HttpStatusCode.NotFound, "Folder not found")
            }
        }

        delete("/folders/{id}") {
            val id = call.parameters["id"] ?: ""
            val success = FoldersRepository.deleteFolder(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Folder deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Folder not found")
            }
        }

        // ===== Note Endpoints =====

        get("/notes") {
            call.respond(NotesRepository.getAllNotes())
        }

        get("/notes/{id}") {
            val id = call.parameters["id"]
            val note = NotesRepository.getNoteById(id ?: "")
            if (note != null) {
                call.respond(note)
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }

        get("/folders/{folderId}/notes") {
            val folderId = call.parameters["folderId"] ?: ""
            call.respond(NotesRepository.getNotesByFolder(folderId))
        }

        post("/notes") {
            val request = call.receive<CreateNoteRequest>()
            val note = GroceryNote(
                id = java.util.UUID.randomUUID().toString(),
                title = request.title,
                items = request.items,
                folderId = request.folderId,
                createdAt = java.time.LocalDateTime.now().toString()
            )
            NotesRepository.addNote(note)
            call.respond(HttpStatusCode.Created, note)
        }

        put("/notes/{id}") {
            val id = call.parameters["id"] ?: ""
            val request = call.receive<CreateNoteRequest>()
            val existingNote = NotesRepository.getNoteById(id)

            if (existingNote != null) {
                val updatedNote = GroceryNote(
                    id = id,
                    title = request.title,
                    items = request.items,
                    folderId = request.folderId,
                    createdAt = existingNote.createdAt
                )
                NotesRepository.updateNote(id, updatedNote)
                call.respond(HttpStatusCode.OK, updatedNote)
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }

        delete("/notes/{id}") {
            val id = call.parameters["id"] ?: ""
            val success = NotesRepository.deleteNote(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Note deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }
    }
}