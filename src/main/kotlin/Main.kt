import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel

data class App(var todoList: TodoList) : ViewModel

data class TodoList(var todos: List<Todo>) : ViewModel {
    fun add(todo: Todo) {
        this.todos = this.todos + todo
    }
}

data class Todo(val description: String)

fun main(args: Array<String>) {
    val renderer = HandlebarsTemplates().HotReload("src/docs")
    val todoList = TodoList(listOf(Todo("feed cat"), Todo("eat food")))

    val viewModel = App(todoList)

    val app: HttpHandler = routes(
        "/" bind Method.GET to  { Response(OK).body(renderer(viewModel)) },
        "/todos" bind Method.POST to {
            val desc = it.form("description")!!
            todoList.add(Todo(desc))
            Response(OK).body(renderer(todoList))
        }
    )


    val server = app.asServer(Undertow(9000)).start()
}