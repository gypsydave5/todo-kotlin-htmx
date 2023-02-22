import org.http4k.template.ViewModel
import java.util.*

interface TodoListStore {
    fun get(): List<Todo>
    fun add(Todo: Todo)
    fun delete(id: UUID)
    fun toggle(id: UUID)
}

class InMemoryTodoListStore(initialList: List<Todo> = emptyList()) : TodoListStore {
    private var todoList = initialList

    override fun get(): List<Todo> = todoList
    override fun add(todo: Todo) {
        todoList+=todo
    }

    override fun delete(id: UUID) {
        todoList = todoList.delete(id)
    }

    override fun toggle(id: UUID) {
        todoList = todoList.toggle(id)
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