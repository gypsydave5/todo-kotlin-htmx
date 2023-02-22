import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.*
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

        expectThat(todoList) {
            hasSize(3)
            contains(todo1)
            contains(todo2)
            contains(todo3)
        }
    }


    @Test
    fun `can delete todos`() {
        val todoList = TodoList()

        val todo1 = Todo("party hard")
        val todo2 = Todo("party on")
        val todo3 = Todo("parteee")

        todoList.add(todo1)
        todoList.add(todo2)
        todoList.add(todo3)

        todoList.delete(todo1.id)

        expectThat(todoList) {
            hasSize(2)
            contains(todo2)
            contains(todo3)
            doesNotContain(todo1)
        }
    }

    @Test
    fun `can search for Todos`() {
        val todoList = TodoList()

        val todo1 = Todo("party hard")
        val todo2 = Todo("party on")
        val todo3 = Todo("parteee")

        todoList.add(todo1)
        todoList.add(todo2)
        todoList.add(todo3)

        expectThat(todoList.search("hard")) {
            hasSize(1)
            contains(todo1)
            doesNotContain(todo2)
            doesNotContain(todo3)
        }
    }
}