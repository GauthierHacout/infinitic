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

package io.infinitic.common.workflows.engine.messages

import com.github.avrokotlin.avro4k.Avro
import io.infinitic.common.checkBackwardCompatibility
import io.infinitic.common.checkCurrentFileIsUpToDate
import io.infinitic.common.createSchemaFileIfAbsent
import io.infinitic.common.fixtures.TestFactory
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class WorkflowEngineEnvelopeTests : StringSpec({

    WorkflowEngineMessage::class.sealedSubclasses.map {
        val msg = TestFactory.random(it)

        "WorkflowEngineEnvelope(${msg::class.simpleName}) should be avro-convertible" {
            shouldNotThrowAny {
                val envelope = WorkflowEngineEnvelope.from(msg)
                val ser = WorkflowEngineEnvelope.serializer()
                val byteArray = Avro.default.encodeToByteArray(ser, envelope)
                val envelope2 = Avro.default.decodeFromByteArray(ser, byteArray)
                envelope shouldBe envelope2
            }
        }
    }

    "Create WorkflowEngineEnvelope schema file for the current version" {
        createSchemaFileIfAbsent(WorkflowEngineEnvelope.serializer())
    }

    "Saved WorkflowEngineEnvelope schema should be up-to-date with for the current version" {
        checkCurrentFileIsUpToDate(WorkflowEngineEnvelope.serializer())
    }

    "We should be able to read WorkflowEngineEnvelope from any previous version since 0.9.0" {
        checkBackwardCompatibility(WorkflowEngineEnvelope.serializer())
    }
})
