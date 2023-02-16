import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import strikt.assertions.withFirst


class TodoListTest {
    @Test
    fun `can add a todo`() {
        val todoList = TodoList()
        expectThat(todoList.todos).hasSize(0)

        todoList.add(Todo("Buy milk"))
        expectThat(todoList) {
            get { todos }.hasSize(1).withFirst {
                get { description }.isEqualTo("Buy milk")
            }
        }
    }
}