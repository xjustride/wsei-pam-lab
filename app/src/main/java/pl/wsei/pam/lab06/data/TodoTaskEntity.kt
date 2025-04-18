package pl.wsei.pam.lab06.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.wsei.pam.lab06.Lab06Activity.TodoTask
import pl.wsei.pam.lab06.Lab06Activity.Priority
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean,
    val priority: Priority
) {
    fun toModel(): TodoTask {
        return TodoTask(
            id = id,
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = priority
        )
    }

    companion object {
        fun fromModel(model: TodoTask): TodoTaskEntity {
            return TodoTaskEntity(
                id = model.id,
                title = model.title,
                deadline = model.deadline,
                isDone = model.isDone,
                priority = model.priority
            )
        }
    }
}
