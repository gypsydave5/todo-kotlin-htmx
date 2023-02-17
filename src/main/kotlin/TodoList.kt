import org.http4k.template.ViewModel
import java.util.*

data class TodoList(private var todos: List<Todo> = listOf()) : ViewModel, Collection<Todo> by todos {
    fun add(todo: Todo): TodoList {
        this.todos = this.todos + todo
        return this
    }

    fun delete(id: UUID): TodoList {
        this.todos = this.todos.filterNot { it.id == id }
        return this
    }

    fun get(id: UUID): Todo = this.todos.find { it.id == id }!!

    fun search(query: String): TodoList = TodoList(this.todos.filter { it.description.contains(query) })
}