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

package io.infinitic.tasks.tag.storage

import io.infinitic.common.data.MessageId
import io.infinitic.common.storage.keySet.KeySetStorage
import io.infinitic.common.storage.keySet.WrappedKeySetStorage
import io.infinitic.common.storage.keyValue.KeyValueStorage
import io.infinitic.common.storage.keyValue.WrappedKeyValueStorage
import io.infinitic.common.tasks.data.TaskId
import io.infinitic.common.tasks.data.TaskName
import io.infinitic.common.tasks.data.TaskTag
import io.infinitic.common.tasks.tags.storage.TaskTagStorage
import org.jetbrains.annotations.TestOnly

/**
 * TaskTagStorage implementation
 *
 * LastMessageId is saved in a key value store in a binary format
 * TaskIds are saved in a key set store in a binary format
 *
 * Any exception thrown by the storage is wrapped into KeyValueStorageException
 */
class BinaryTaskTagStorage(
    keyValueStorage: KeyValueStorage,
    keySetStorage: KeySetStorage
) : TaskTagStorage {

    private val keyValueStorage = WrappedKeyValueStorage(keyValueStorage)
    private val keySetStorage = WrappedKeySetStorage(keySetStorage)

    override suspend fun getLastMessageId(tag: TaskTag, taskName: TaskName): MessageId? {
        val key = getTagMessageIdKey(tag, taskName)

        return keyValueStorage.get(key)?.let { MessageId.fromByteArray(it) }
    }

    override suspend fun setLastMessageId(tag: TaskTag, taskName: TaskName, messageId: MessageId) {
        val key = getTagMessageIdKey(tag, taskName)
        keyValueStorage.put(key, messageId.toByteArray())
    }

    override suspend fun getTaskIds(tag: TaskTag, taskName: TaskName): Set<TaskId> {
        val key = getTagSetIdsKey(tag, taskName)
        return keySetStorage
            .get(key)
            .map { TaskId(String(it)) }
            .toSet()
    }

    override suspend fun addTaskId(tag: TaskTag, taskName: TaskName, taskId: TaskId) {
        val key = getTagSetIdsKey(tag, taskName)
        keySetStorage.add(key, taskId.toString().toByteArray())
    }

    override suspend fun removeTaskId(tag: TaskTag, taskName: TaskName, taskId: TaskId) {
        val key = getTagSetIdsKey(tag, taskName)
        keySetStorage.remove(key, taskId.toString().toByteArray())
    }

    private fun getTagMessageIdKey(tag: TaskTag, taskName: TaskName) = "task:$taskName|tag:$tag|messageId"

    private fun getTagSetIdsKey(tag: TaskTag, taskName: TaskName) = "task:$taskName|tag:$tag|setIds"

    @TestOnly
    override fun flush() {
        keyValueStorage.flush()
        keySetStorage.flush()
    }
}
