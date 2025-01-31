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

package io.infinitic.common.workflows.data.channels

internal fun getAllExtendedOrImplementedTypes(klass: Class<*>): Set<ChannelType> {
    var clazz = klass
    val all: MutableList<ChannelType> = mutableListOf()
    do {
        all.add(ChannelType(clazz.name))

        // First, add all the interfaces implemented by this class
        val interfaces = clazz.interfaces.toList()
        if (interfaces.isNotEmpty()) {
            all.addAll(interfaces.map { ChannelType(it.name) })
            for (interfaze in interfaces) {
                all.addAll(getAllExtendedOrImplementedTypes(interfaze))
            }
        }

        // Add the super class
        val superClass = clazz.superclass ?: break

        // Interfaces do not have java,lang.Object as superclass, they have null, so break the cycle and return

        // Now inspect the superclass
        clazz = superClass
    } while ("java.lang.Object" != clazz.canonicalName)

    return all.toSet()
}
