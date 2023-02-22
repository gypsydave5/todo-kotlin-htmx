import org.http4k.template.ViewModel
import java.util.*

interface TodoListStore {
    fun get(): List<Todo>
    fun set(todoList: List<Todo>)
}

class InMemoryTodoListStore : TodoListStore {
    private var todoList = listOf<Todo>()

    override fun get(): List<Todo> = todoList

    override fun set(todoList: List<Todo>) {
        println("setting todo list to $todoList")
        this.todoList = todoList
    }
}
fun List<Todo>.delete(id: UUID): List<Todo> = this.filter { it.id == id }
fun List<Todo>.get(id: UUID): Todo? = this.find { it.id == id }
fun List<Todo>.search(query: String): List<Todo> = this.filter { it.description.contains(query) }

fun List<Todo>.toggle(ID: UUID): List<Todo> {
    val todo = this.get(ID)
    todo?.toggle()
    return this
}

data class TodoList(val todos: List<Todo>) : ViewModel