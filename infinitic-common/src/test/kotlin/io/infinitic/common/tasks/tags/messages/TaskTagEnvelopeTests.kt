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

package io.infinitic.common.tasks.tags.messages

import com.github.avrokotlin.avro4k.Avro
import io.infinitic.common.checkBackwardCompatibility
import io.infinitic.common.checkCurrentFileIsUpToDate
import io.infinitic.common.createSchemaFileIfAbsent
import io.infinitic.common.fixtures.TestFactory
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TaskTagEnvelopeTests : StringSpec({
    TaskTagMessage::class.sealedSubclasses.map {
        val msg = TestFactory.random(it)

        "TaskTagMessage(${msg::class.simpleName}) should be avro-convertible" {
            shouldNotThrowAny {
                val envelope = TaskTagEnvelope.from(msg)
                val ser = TaskTagEnvelope.serializer()
                val byteArray = Avro.default.encodeToByteArray(ser, envelope)
                val envelope2 = Avro.default.decodeFromByteArray(ser, byteArray)
                envelope shouldBe envelope2
            }
        }
    }

    "Create TaskTagEnvelope schema file for the current version" {
        createSchemaFileIfAbsent(TaskTagEnvelope.serializer())
    }

    "Saved TaskTagEnvelope schema should be up-to-date with for the current version" {
        checkCurrentFileIsUpToDate(TaskTagEnvelope.serializer())
    }

    "We should be able to read TaskTagEnvelope from any previous version since 0.9.0" {
        checkBackwardCompatibility(TaskTagEnvelope.serializer())
    }
})
