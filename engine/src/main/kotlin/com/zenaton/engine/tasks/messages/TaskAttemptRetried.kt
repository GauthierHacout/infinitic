package com.zenaton.engine.tasks.messages

import com.zenaton.engine.interfaces.data.DateTime
import com.zenaton.engine.taskAttempts.data.TaskAttemptId
import com.zenaton.engine.tasks.data.TaskId
import com.zenaton.engine.tasks.interfaces.TaskMessageInterface

data class TaskAttemptRetried(
    override var taskId: TaskId,
    override var sentAt: DateTime? = DateTime(),
    override var receivedAt: DateTime? = null,
    val taskAttemptId: TaskAttemptId,
    val taskAttemptIndex: Int
) : TaskMessageInterface
