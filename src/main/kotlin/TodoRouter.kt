import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import org.http4k.template.renderToResponse

fun todoRouter(
    todoListStore: TodoListStore,
    renderer: TemplateRenderer
): RoutingHttpHandler {
    val idLens = Path.uuid().of("id")
    val todoField = FormField.nonEmptyString().map(::Todo, Todo::description).required("description")
    val formLens = Body.webForm(Validator.Feedback, todoField).toLens()
    val queryLens = Query.string().defaulted("search", "")

    println("only expecting to see this once, when the server starts up")


    return routes(
        "/" bind Method.GET to {
            val queryLens1: String = queryLens(it)
            queryLens1
                .let{query -> todoListStore.get().search(query)}
                .let{ todos -> TodoList(todos)}
                .let(renderer::renderToResponse)
        },
        "/" bind Method.POST to {
            formLens(it)
                .let(todoField)
                .also{ todo -> todoListStore.set(todoListStore.get() + todo)}
            println("todoListStore.get() = ${todoListStore.get()}")
            renderer.renderToResponse(TodoList(todoListStore.get()))
        },
        "/{id}/toggle" bind Method.POST to {
            idLens(it)
                .let{id -> todoListStore.set(todoListStore.get().toggle(id))}
            Response(Status.SEE_OTHER).header("Location", "/todos")
        },
        "/{id}" bind Method.DELETE to {
            val id = idLens(it)
            todoListStore.set(todoListStore.get().delete(id))
            Response(Status.OK)
        },
    )
}