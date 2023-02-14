import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.*
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.renderToResponse
import org.http4k.template.viewModel
import java.util.*

data class App(var todoList: TodoList) : ViewModel

data class TodoList(var todos: List<Todo>) : ViewModel {
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

data class Todo(val description: String) : ViewModel {
    var done: Boolean = false
    val id: UUID = UUID.randomUUID()
    fun toggle(): Todo {
        this.done = !this.done
        return this
    }
}

fun main(args: Array<String>) {
    val renderer = HandlebarsTemplates().HotReload("src/docs")
    val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    val todoList = TodoList(listOf(Todo("feed cat"), Todo("eat food")))
    val viewModel = App(todoList)

    val idLens = Path.uuid().of("id")
    val todoField = FormField.nonEmptyString().map(::Todo, Todo::description).required("description")
    val formLens = Body.webForm(Validator.Feedback, todoField).toLens()
    val queryLens = Query.string().defaulted("search", "")

    val app: HttpHandler = routes(
        "/todos" bind routes(
            "/" bind Method.GET to {
                queryLens(it)
                    .let(todoList::search)
                    .let(renderer::renderToResponse)
            },
            "/" bind Method.POST to {
                formLens(it)
                    .let(todoField)
                    .let(todoList::add)
                    .let(renderer::renderToResponse)
            },
            "/{id}/toggle" bind Method.POST to {
                idLens(it)
                    .let(todoList::get)
                    .toggle()
                    .let(renderer::renderToResponse)
            },
            "/{id}" bind Method.DELETE to {
                val id = idLens(it)
                todoList.delete(id)
                Response(OK)

                // vs.

                idLens(it)
                    .let(todoList::delete)
                    .let { Response(OK) }

                // vs.
                idLens(it).let(todoList::delete)
                Response(OK)

            },
        ),
        "/" bind Method.GET to { Response(OK).body(renderer(viewModel)) },
    )


    app.asServer(Undertow(9000)).start()
}