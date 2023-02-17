import org.http4k.template.ViewModel
import java.util.*

data class TodoList(private var todos: Collection<Todo> = listOf()) : ViewModel, Collection<Todo> {

    fun add(todo: Todo): TodoList {
        this.todos = this.todos + todo
        return this
    }

    fun delete(id: UUID): TodoList {
        this.todos = this.todos.filter { it.id == id }
        return this
    }

    fun get(id: UUID): Todo? = this.todos.find { it.id == id }

    fun search(query: String): TodoList = TodoList(this.todos.filter { it.description.contains(query) }.toMutableList())

    override val size: Int get() = this.todos.size

    override fun isEmpty(): Boolean {
        return this.todos.isEmpty()
    }

    override fun iterator(): Iterator<Todo> {
        return this.todos.iterator()
    }

    override fun containsAll(elements: Collection<Todo>): Boolean {
        return this.todos.containsAll(elements)
    }

    override fun contains(element: Todo): Boolean {
        return this.contains(element)
    }
}