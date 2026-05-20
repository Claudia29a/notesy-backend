package com.example.notesy

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

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
            val id = call.parameters["id"] ?: ""
            val folder = FoldersRepository.getFolderById(id)

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
            val id = call.parameters["id"] ?: ""
            val note = NotesRepository.getNoteById(id)

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

            val note = Note(
                id = java.util.UUID.randomUUID().toString(),
                title = request.title,
                content = request.content,
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
                val updatedNote = Note(
                    id = id,
                    title = request.title,
                    content = request.content,
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

        // ===== AI Endpoint =====

        post("/ai/suggest-groceries") {
            try {
                val request = call.receive<AiSuggestRequest>()

                if (request.noteText.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        AiErrorResponse(error = "noteText cannot be blank")
                    )
                    return@post
                }

                val items = suggestGroceriesWithOllama(request.noteText)

                call.respond(
                    HttpStatusCode.OK,
                    AiSuggestResponse(items = items)
                )
            } catch (e: IllegalStateException) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = e.message ?: "AI configuration error")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    AiErrorResponse(error = e.message ?: "Unknown AI error")
                )
            }
        }
    }
}