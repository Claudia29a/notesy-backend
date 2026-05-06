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

        // Get all notes
        get("/notes") {
            call.respond(NotesRepository.getAllNotes())
        }

        // Get a specific note by ID
        get("/notes/{id}") {
            val id = call.parameters["id"]
            val note = NotesRepository.getNoteById(id ?: "")
            if (note != null) {
                call.respond(note)
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }

        // Create a new note
        post("/notes") {
            val note = call.receive<GroceryNote>()
            NotesRepository.addNote(note)
            call.respond(HttpStatusCode.Created, note)
        }

        // Update a note
        put("/notes/{id}") {
            val id = call.parameters["id"] ?: ""
            val updatedNote = call.receive<GroceryNote>()
            val success = NotesRepository.updateNote(id, updatedNote)
            if (success) {
                call.respond(HttpStatusCode.OK, updatedNote)
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }

        // Delete a note
        delete("/notes/{id}") {
            val id = call.parameters["id"] ?: ""
            val success = NotesRepository.deleteNote(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Note deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Note not found")
            }
        }

        // ===== Preferred Items Endpoints =====

        // Get all preferred items
        get("/preferred-items") {
            call.respond(PreferredItemsRepository.getAllItems())
        }

        // Add a preferred item
        post("/preferred-items") {
            val item = call.receive<PreferredItem>()
            val success = PreferredItemsRepository.addItem(item)
            if (success) {
                call.respond(HttpStatusCode.Created, item)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Maximum 10 preferred items allowed")
            }
        }

        // Delete a preferred item
        delete("/preferred-items/{id}") {
            val id = call.parameters["id"] ?: ""
            val success = PreferredItemsRepository.removeItem(id)
            if (success) {
                call.respond(HttpStatusCode.OK, "Preferred item deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Preferred item not found")
            }
        }
    }
}