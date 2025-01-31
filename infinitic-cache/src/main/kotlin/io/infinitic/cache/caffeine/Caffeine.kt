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

package io.infinitic.cache.caffeine

import com.github.benmanes.caffeine.cache.RemovalCause
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import com.github.benmanes.caffeine.cache.Caffeine as CaffeineCache

internal val logger = KotlinLogging.logger {}

data class Caffeine(
    @JvmField val maximumSize: Int? = null,
    @JvmField val expireAfterAccess: Int? = null,
    @JvmField val expireAfterWrite: Int? = null
) {
    init {
        maximumSize?. let { require(it > 0) { "maximumSize MUST be >0" } }
        expireAfterAccess?. let { require(it > 0) { "expireAfterAccess MUST be >0" } }
        expireAfterWrite?. let { require(it > 0) { "expireAfterWrite MUST be >0" } }
    }
}

fun <S, T> CaffeineCache<S, T>.setup(config: Caffeine): CaffeineCache<S, T> {
    if (config.maximumSize is Int) {
        this.maximumSize(config.maximumSize.toLong())
    }
    if (config.expireAfterAccess is Int) {
        this.expireAfterAccess(config.expireAfterAccess.toLong(), TimeUnit.SECONDS)
    }
    if (config.expireAfterWrite is Int) {
        this.expireAfterWrite(config.expireAfterWrite.toLong(), TimeUnit.SECONDS)
    }
    this.removalListener<S, T> { key, _, cause ->
        when (cause) {
            RemovalCause.SIZE -> logger.debug { "Cache size exceeded, removing $key" }
            RemovalCause.EXPIRED -> logger.debug { "Cache expired, removing $key" }
            else -> Unit // Do nothing
        }
    }

    return this
}
