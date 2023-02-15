import org.http4k.template.ViewModel
import java.util.*

data class Todo(val description: String) : ViewModel {
    var done: Boolean = false
    val id: UUID = UUID.randomUUID()
    fun toggle(): Todo {
        this.done = !this.done
        return this
    }
}