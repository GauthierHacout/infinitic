package com.zenaton.taskmanager.messages.commands

import com.zenaton.commons.data.DateTime
import com.zenaton.taskmanager.data.TaskData
import com.zenaton.taskmanager.data.TaskId
import com.zenaton.taskmanager.data.TaskName
import com.zenaton.taskmanager.messages.TaskMessageInterface
import com.zenaton.workflowengine.data.WorkflowId

data class DispatchTask(
    override val taskId: TaskId,
    override val sentAt: DateTime = DateTime(),
    val taskName: TaskName,
    val taskData: TaskData?,
    val workflowId: WorkflowId? = null
) : TaskMessageInterface
