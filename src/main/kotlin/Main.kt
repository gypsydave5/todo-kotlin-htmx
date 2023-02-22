import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Undertow
import org.http4k.server.asServer
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel

data class App(var todoList: TodoList) : ViewModel

fun main(args: Array<String>) {
    val renderer = HandlebarsTemplates().HotReload("src/docs")
    val view = Body.viewModel(renderer, TEXT_HTML).toLens()

    val todoList = TodoList(listOf(Todo("feed cat"), Todo("eat food")))
    val todoApp = App(todoList)

    val server: HttpHandler = routes(
        "/todos" bind todoRouter(todoList, renderer),

        "/" bind Method.GET to { Response(OK).body(renderer(todoApp)) },
    )


    println("Starting server on http://localhost:9000")
    server.asServer(Undertow(9000)).start()
}

