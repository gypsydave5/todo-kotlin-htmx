import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isContainedIn
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.util.*


class TodoListTest {
    @Test
    fun `can add a todo`() {
        val todoList = TodoList()
        expectThat(todoList).hasSize(0)

        val todo = Todo("Buy milk")

        todoList.add(todo)

        expectThat(todoList.size).isEqualTo(1)
        expectThat(todoList.first()).isEqualTo(todo)
    }

    @Test
    fun `can get a todo by its ID`() {
        val todoList = TodoList()
        val todo = Todo("party hard")
        val id = todo.id
        todoList.add(todo)

        val retrievedTodo = todoList.get(id)

        expectThat(retrievedTodo).isEqualTo(todo)
    }

    @Test
    fun `when getting a todo that's not in the list`() {
        val todoList = TodoList()
        val id = UUID.randomUUID()
        val todo = todoList.get(id)
        expectThat(todo).isNull()
    }

    @Test
    fun `iterate through all the todos`() {
        val todoList = TodoList()

        val todo1 = Todo("party hard")
        val todo2 = Todo("party on")
        val todo3 = Todo("parteee")

        todoList.add(todo1)
        todoList.add(todo2)
        todoList.add(todo3)

        expectThat(todoList).hasSize(3)

        for (todo in todoList) {
            expectThat(todo).isContainedIn(listOf(todo1, todo2, todo3))
        }
    }
}