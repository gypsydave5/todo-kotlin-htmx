import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.*

data class App(private val store: TodoListStore) : ViewModel {
    val todos: List<Todo> = store.get()
}

fun main(args: Array<String>) {
    val renderer = HandlebarsTemplates().HotReload("src/docs")
    val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    val todoListStore = InMemoryTodoListStore(listOf(Todo("feed cat"), Todo("eat food")))

    val todoApp = App(todoListStore)

    val server: HttpHandler = routes(
        "/todos" bind todoRouter(todoListStore, renderer),

        "/" bind Method.GET to { Response(OK).body(renderer(todoApp)) },
    )


    server.asServer(Undertow(9000)).start()
}

