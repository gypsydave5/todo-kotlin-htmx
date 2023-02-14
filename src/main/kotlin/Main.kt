import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.lens.Path
import org.http4k.lens.uuid
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import java.util.*

data class App(var todoList: TodoList) : ViewModel

data class TodoList(var todos: List<Todo>) : ViewModel {
    fun add(todo: Todo) {
        this.todos = this.todos + todo
    }

    fun delete(id: UUID) {
        this.todos = this.todos.filterNot { it.id == id }
    }

    fun get(id: UUID): Todo = this.todos.find { it.id == id }!!

    fun search(query: String): TodoList = TodoList(this.todos.filter { it.description.contains(query) })
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
        "/todos" bind routes(
            "/" bind Method.GET to { Response(OK).body(renderer(todoList.search(it.query("search") ?: ""))) },
            "/" bind Method.POST to {
                val desc = it.form("description") ?: "wtf"
                todoList.add(Todo(desc))
                Response(OK).body(renderer(todoList))
            },
            "/{id}/toggle" bind Method.POST to {
                val todo = todoList.get(Path.uuid().of("id").extract(it))
                todo.toggle()
                Response(OK).body(renderer(todo))
            },
            "/{id}" bind Method.DELETE to {
                todoList.delete(Path.uuid().of("id").extract(it))
                Response(OK)
            },
        ),
        "/" bind Method.GET to  { Response(OK).body(renderer(viewModel)) },
    )


    val server = app.asServer(Undertow(9000)).start()
}