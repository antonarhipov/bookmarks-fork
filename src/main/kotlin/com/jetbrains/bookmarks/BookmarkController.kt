package com.jetbrains.bookmarks

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Instant
import java.util.function.Supplier

@RestController
@RequestMapping("/api/bookmarks")
internal class BookmarkController(private val bookmarkRepository: BookmarkRepository) {

    @GetMapping
    fun bookmarks() = bookmarkRepository.findAllByOrderByCreatedAtDesc()

    @GetMapping("/{id}")
    fun getBookmarkById(@PathVariable id: Long): ResponseEntity<BookmarkInfo> {
        val bookmark = bookmarkRepository.findBookmarkById(id)
            ?: throw BookmarkNotFoundException("Bookmark not found")

        return ResponseEntity.ok<BookmarkInfo>(bookmark)
    }

    @JvmRecord
    internal data class CreateBookmarkPayload(
        val title: @NotEmpty(message = "Title is required") String,
        val url: @NotEmpty(message = "Url is required") String
    )

    @PostMapping
    fun createBookmark(
        @RequestBody payload: @Valid CreateBookmarkPayload
    ): ResponseEntity<Void> {
        val bookmark = Bookmark()
        bookmark.title = payload.title
        bookmark.url = payload.url
        bookmark.createdAt = Instant.now()
        val savedBookmark = bookmarkRepository.save<Bookmark>(bookmark)
        val url = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .build(savedBookmark.id)

        return ResponseEntity.created(url).build<Void>()
    }

    @JvmRecord
    internal data class UpdateBookmarkPayload(
        val title: @NotEmpty(message = "Title is required") String,
        val url: @NotEmpty(message = "Url is required") String
    )

    @PutMapping("/{id}")
    fun updateBookmark(
        @PathVariable id: Long,
        @RequestBody payload: @Valid UpdateBookmarkPayload
    ): ResponseEntity<Void> {
        val bookmark =
            bookmarkRepository.findById(id).orElseThrow { BookmarkNotFoundException("Bookmark not found") }

        bookmark.title = payload.title
        bookmark.url = payload.url
        bookmark.updatedAt = Instant.now()
        bookmarkRepository.save(bookmark)
        return ResponseEntity.noContent().build<Void>()
    }

    @DeleteMapping("/{id}")
    fun deleteBookmark(@PathVariable id: Long) {
        val bookmark = bookmarkRepository.findById(id)
            .orElseThrow<BookmarkNotFoundException?>(Supplier { BookmarkNotFoundException("Bookmark not found") })
        bookmarkRepository.delete(bookmark)
    }

    @ExceptionHandler(BookmarkNotFoundException::class)
    fun handle(e: BookmarkNotFoundException?): ResponseEntity<Void?> {
        return ResponseEntity.notFound().build<Void?>()
    }
}
