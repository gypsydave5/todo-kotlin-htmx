import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.withFirst


class TodoListTest {
    @Test
    fun `can add a todo`() {
        val todoList = TodoList()
        expectThat(todoList).hasSize(0)

        todoList.add(Todo("Buy milk"))
        expect {
            that(todoList).hasSize(1)
            that(todoList.first().description).isEqualTo("Buy milk")
        }
    }
}