package com.zenaton.pulsar.workflows

import com.zenaton.engine.decisions.DecisionDispatched
import com.zenaton.engine.tasks.Message.TaskDispatched
import com.zenaton.engine.workflows.DispatcherInterface
import com.zenaton.pulsar.workflows.serializers.MessageConverter
import com.zenaton.pulsar.workflows.serializers.MessageConverterInterface
import org.apache.pulsar.functions.api.Context

class Dispatcher(private val context: Context) : DispatcherInterface {

    // MessageConverter injection
    var converter: MessageConverterInterface = MessageConverter

    override fun dispatchTask(msg: TaskDispatched) {

        TODO("Not yet implemented")
    }

    override fun dispatchDecision(msg: DecisionDispatched) {
        TODO("Not yet implemented")
    }
}
