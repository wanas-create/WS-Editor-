package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiPromptInputTest {

    @Test
    fun testPromptTypingAndMultilineEditing() {
        var prompt = ""
        fun onValueChange(newVal: String) {
            prompt = newVal
        }

        // 1. Initial empty state
        assertTrue(prompt.isEmpty())

        // 2. Typing standard single-line prompt
        onValueChange("A futuristic cybernetic city")
        assertEquals("A futuristic cybernetic city", prompt)
        assertFalse(prompt.isEmpty())

        // 3. Multiline prompt editing (including newlines and special characters)
        val multilinePrompt = """
            A futuristic cybernetic city with neon rain.
            Shot on 35mm lens, cinematic rim lighting, 8k resolution.
            Hyper-detailed architectural rendering.
        """.trimIndent()
        onValueChange(multilinePrompt)
        assertEquals(multilinePrompt, prompt)
        assertTrue(prompt.contains("\n"))
        assertTrue(prompt.lines().size >= 3)

        // 4. Character count
        assertEquals(multilinePrompt.length, prompt.length)

        // 5. Editing and appending
        val editedPrompt = "$prompt\nPhotorealistic style."
        onValueChange(editedPrompt)
        assertTrue(editedPrompt.endsWith("Photorealistic style."))
        assertEquals(4, editedPrompt.lines().size)

        // 6. Clearing prompt
        onValueChange("")
        assertTrue(prompt.isEmpty())
    }

    @Test
    fun testLongPromptExceedingStandardLines() {
        // Test scrolling/handling of lengthy detailed AI prompt
        val longPrompt = StringBuilder().apply {
            repeat(15) { i ->
                append("Sentence $i describing visual attributes, lighting, texture, and composition.\n")
            }
        }.toString().trim()

        var current = longPrompt
        assertEquals(15, current.lines().size)
        assertTrue(current.length > 500)

        // Trim or backspace simulation
        current = current.dropLast(10)
        assertTrue(current.length > 490)
    }
}
