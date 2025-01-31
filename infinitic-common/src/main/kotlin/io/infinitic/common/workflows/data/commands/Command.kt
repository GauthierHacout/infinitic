/**
 * "Commons Clause" License Condition v1.0
 *
 * The Software is provided to you by the Licensor under the License, as defined
 * below, subject to the following condition.
 *
 * Without limiting other conditions in the License, the grant of rights under the
 * License will not include, and the License does not grant to you, the right to
 * Sell the Software.
 *
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights
 * granted to you under the License to provide to third parties, for a fee or
 * other consideration (including without limitation fees for hosting or
 * consulting/ support services related to the Software), a product or service
 * whose value derives, entirely or substantially, from the functionality of the
 * Software. Any license notice or attribution required by the License must also
 * include this Commons Clause License Condition notice.
 *
 * Software: Infinitic
 *
 * License: MIT License (https://opensource.org/licenses/MIT)
 *
 * Licensor: infinitic.io
 */

package io.infinitic.common.workflows.data.commands

import com.github.avrokotlin.avro4k.AvroDefault
import com.github.avrokotlin.avro4k.AvroName
import io.infinitic.common.data.MillisDuration
import io.infinitic.common.data.MillisInstant
import io.infinitic.common.data.methods.MethodName
import io.infinitic.common.data.methods.MethodParameterTypes
import io.infinitic.common.data.methods.MethodParameters
import io.infinitic.common.tasks.data.TaskMeta
import io.infinitic.common.tasks.data.TaskName
import io.infinitic.common.tasks.data.TaskTag
import io.infinitic.common.workflows.data.channels.ChannelFilter
import io.infinitic.common.workflows.data.channels.ChannelName
import io.infinitic.common.workflows.data.channels.ChannelType
import io.infinitic.common.workflows.data.channels.SignalData
import io.infinitic.common.workflows.data.workflows.WorkflowId
import io.infinitic.common.workflows.data.workflows.WorkflowMeta
import io.infinitic.common.workflows.data.workflows.WorkflowName
import io.infinitic.common.workflows.data.workflows.WorkflowTag
import io.infinitic.tasks.TaskOptions
import io.infinitic.workflows.WorkflowOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Command {
    abstract fun isSameThan(other: Command): Boolean
}

/**
 * Commands are asynchronously processed
 */

@Serializable @SerialName("Command.DispatchTask")
data class DispatchTaskCommand(
    val taskName: TaskName,
    val methodName: MethodName,
    val methodParameterTypes: MethodParameterTypes,
    val methodParameters: MethodParameters,
    val taskTags: Set<TaskTag>,
    val taskMeta: TaskMeta,
    val taskOptions: TaskOptions
) : Command() {
    override fun isSameThan(other: Command): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as DispatchTaskCommand

        return taskName == other.taskName &&
            methodName == other.methodName &&
            methodParameterTypes == other.methodParameterTypes &&
            methodParameters == other.methodParameters
    }
}

@Serializable @SerialName("Command.DispatchWorkflow")
data class DispatchWorkflowCommand(
    val workflowName: WorkflowName,
    val methodName: MethodName,
    val methodParameterTypes: MethodParameterTypes,
    val methodParameters: MethodParameters,
    val workflowTags: Set<WorkflowTag>,
    val workflowMeta: WorkflowMeta,
    val workflowOptions: WorkflowOptions
) : Command() {
    override fun isSameThan(other: Command): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as DispatchWorkflowCommand

        return workflowName == other.workflowName &&
            methodName == other.methodName &&
            methodParameterTypes == other.methodParameterTypes &&
            methodParameters == other.methodParameters
    }
}

@Serializable @SerialName("Command.DispatchMethod")
data class DispatchMethodCommand(
    val workflowName: WorkflowName,
    val workflowId: WorkflowId?,
    val workflowTag: WorkflowTag?,
    val methodName: MethodName,
    val methodParameterTypes: MethodParameterTypes,
    val methodParameters: MethodParameters
) : Command() {
    override fun isSameThan(other: Command): Boolean {
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as DispatchMethodCommand

        return workflowName == other.workflowName &&
            workflowId == other.workflowId &&
            workflowTag == other.workflowTag &&
            methodName == other.methodName &&
            methodParameterTypes == other.methodParameterTypes &&
            methodParameters == other.methodParameters
    }
}

@Serializable @SerialName("Command.SendSignal")
data class SendSignalCommand(
    val workflowName: WorkflowName,
    val workflowId: WorkflowId?,
    val workflowTag: WorkflowTag?,
    val channelName: ChannelName,
    @AvroName("channelSignal")
    val signalData: SignalData,
    @AvroName("channelSignalTypes")
    val channelTypes: Set<ChannelType>
) : Command() {
    companion object {
        fun simpleName() = CommandSimpleName("SEND_SIGNAL")
    }

    override fun isSameThan(other: Command) = other == this
}

@Serializable @SerialName("Command.ReceiveSignal")
data class ReceiveSignalCommand(
    val channelName: ChannelName,
    @AvroName("channelSignalType")
    val channelType: ChannelType?,
    @AvroName("channelEventFilter")
    val channelFilter: ChannelFilter?,
    @AvroDefault("1")
    val receivedSignalLimit: Int?
) : Command() {
    companion object {
        fun simpleName() = CommandSimpleName("RECEIVE_SIGNAL")
    }

    override fun isSameThan(other: Command) = other == this
}

@Serializable @SerialName("Command.InlineTask")
data class InlineTaskCommand(
    val task: String = "inline"
) : Command() {
    companion object {
        fun simpleName() = CommandSimpleName("INLINE_TASK")
    }

    override fun hashCode() = task.hashCode()

    override fun equals(other: Any?) = other is InlineTaskCommand

    override fun isSameThan(other: Command) = other == this
}

@Serializable @SerialName("Command.StartDurationTimer")
data class StartDurationTimerCommand(
    val duration: MillisDuration
) : Command() {
    companion object {
        fun simpleName() = CommandSimpleName("START_DURATION_TIMER")
    }

    override fun isSameThan(other: Command) = other == this
}

@Serializable @SerialName("Command.StartInstantTimer")
data class StartInstantTimerCommand(
    val instant: MillisInstant
) : Command() {
    companion object {
        fun simpleName() = CommandSimpleName("START_INSTANT_TIMER")
    }

    override fun isSameThan(other: Command) = other == this
}
