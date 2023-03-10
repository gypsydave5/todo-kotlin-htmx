import org.http4k.core.*
import org.http4k.lens.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.TemplateRenderer
import org.http4k.template.ViewModel
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
                ?.toggle()
                .let(renderer::renderOrNotFound)
        },
        "/{id}" bind Method.DELETE to {
            idLens(it).let(todoList::delete)
            Response(Status.OK)
        },
    )
}

fun TemplateRenderer.renderOrNotFound(
    viewModel: ViewModel?,
    status: Status = Status.OK,
    contentType: ContentType = ContentType.TEXT_HTML,
    ifNullStatus: Status = Status.NOT_FOUND
): Response = viewModel?.let { this.renderToResponse(it, status, contentType) }
    ?: Response(ifNullStatus)