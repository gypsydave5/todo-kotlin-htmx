import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import java.util.UUID

data class App(var todoList: TodoList) : ViewModel

data class TodoList(var todos: List<Todo>) : ViewModel {
    fun add(todo: Todo) {
        this.todos = this.todos + todo
    }

    fun delete(id: UUID) {
        this.todos = this.todos.filterNot { it.id == id }
    }

    fun get(id: UUID): Todo {
        return this.todos.find { it.id == id }!!
    }

    fun search(form: String): TodoList {
        return TodoList(this.todos.filter { it.description.contains(form) })
    }
}

data class Todo(val description: String) : ViewModel {
    var done: Boolean = false
    val id: UUID = UUID.randomUUID()
    fun toggle() {
        this.done = !this.done
    }
}

fun main(args: Array<String>) {
    val renderer = HandlebarsTemplates().HotReload("src/docs")
    val todoList = TodoList(listOf(Todo("feed cat"), Todo("eat food")))

    val viewModel = App(todoList)

    val app: HttpHandler = routes(
        "/todos/{id}/toggle" bind Method.POST to {
            val id = it.path("id")?.let { UUID.fromString(it) }!!
            val todo = todoList.get(id)
            todo.toggle()
            Response(OK).body(renderer(todo))
        },
        "/todos/{id}" bind Method.DELETE to {
            todoList.delete(it.path("id")?.let { UUID.fromString(it) }!!)
            Response(OK)
        },
        "/todos" bind Method.POST to {
            val desc = it.form("description") ?: "wtf"
            todoList.add(Todo(desc))
            Response(OK).body(renderer(todoList))
        },
        "/todos" bind Method.GET to { Response(OK).body(renderer(todoList.search(it.query("search") ?: ""))) },
        "/" bind Method.GET to  { Response(OK).body(renderer(viewModel)) },
    )


    val server = app.asServer(Undertow(9000)).start()
}