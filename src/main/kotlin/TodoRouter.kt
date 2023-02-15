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
    todoList: TodoList,
    renderer: TemplateRenderer
): RoutingHttpHandler {
    val idLens = Path.uuid().of("id")
    val todoField = FormField.nonEmptyString().map(::Todo, Todo::description).required("description")
    val formLens = Body.webForm(Validator.Feedback, todoField).toLens()
    val queryLens = Query.string().defaulted("search", "")

    return routes(
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
            Response(Status.OK)

            // vs.

            idLens(it)
                .let(todoList::delete)
                .let { Response(Status.OK) }

            // vs.
            idLens(it).let(todoList::delete)
            Response(Status.OK)

        },
    )
}