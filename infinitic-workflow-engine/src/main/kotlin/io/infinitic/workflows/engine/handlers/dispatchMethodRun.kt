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

package io.infinitic.workflows.engine.handlers

import io.infinitic.common.workflows.data.methodRuns.MethodRun
import io.infinitic.common.workflows.data.methodRuns.MethodRunPosition
import io.infinitic.common.workflows.engine.messages.DispatchMethod
import io.infinitic.common.workflows.engine.state.WorkflowState
import io.infinitic.workflows.engine.helpers.dispatchWorkflowTask
import io.infinitic.workflows.engine.output.WorkflowEngineOutput
import kotlinx.coroutines.CoroutineScope

internal fun CoroutineScope.dispatchMethodRun(
    workflowEngineOutput: WorkflowEngineOutput,
    state: WorkflowState,
    message: DispatchMethod
) {
    val methodRun = MethodRun(
        methodRunId = message.methodRunId,
        waitingClients = when (message.clientWaiting) {
            true -> mutableSetOf(message.emitterName)
            false -> mutableSetOf()
        },
        parentWorkflowId = message.parentWorkflowId,
        parentWorkflowName = message.parentWorkflowName,
        parentMethodRunId = message.parentMethodRunId,
        methodName = message.methodName,
        methodParameterTypes = message.methodParameterTypes,
        methodParameters = message.methodParameters,
        workflowTaskIndexAtStart = state.workflowTaskIndex,
        propertiesNameHashAtStart = state.currentPropertiesNameHash.toMap()
    )

    state.methodRuns.add(methodRun)

    dispatchWorkflowTask(workflowEngineOutput, state, methodRun, MethodRunPosition())
}
