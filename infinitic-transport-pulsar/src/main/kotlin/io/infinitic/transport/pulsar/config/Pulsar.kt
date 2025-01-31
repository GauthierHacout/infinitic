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

package io.infinitic.transport.pulsar.config

import com.sksamuel.hoplite.Secret
import io.infinitic.common.serDe.json.Json
import io.infinitic.transport.pulsar.config.auth.AuthenticationAthenz
import io.infinitic.transport.pulsar.config.auth.AuthenticationOAuth2
import io.infinitic.transport.pulsar.config.auth.AuthenticationSasl
import io.infinitic.transport.pulsar.config.auth.AuthenticationToken
import io.infinitic.transport.pulsar.config.auth.ClientAuthentication
import io.infinitic.transport.pulsar.config.policies.Policies
import org.apache.pulsar.client.admin.PulsarAdmin
import org.apache.pulsar.client.api.AuthenticationFactory
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.impl.auth.oauth2.AuthenticationFactoryOAuth2

data class Pulsar(
    val tenant: String,
    val namespace: String,
    val brokerServiceUrl: String = "pulsar://localhost:6650/",
    val webServiceUrl: String = "http://localhost:8080",
    val allowedClusters: Set<String>? = null,
    val adminRoles: Set<String>? = null,
    val tlsAllowInsecureConnection: Boolean = false,
    val tlsEnableHostnameVerification: Boolean = false,
    val tlsTrustCertsFilePath: String? = null,
    val useKeyStoreTls: Boolean = false,
    val tlsTrustStoreType: TlsTrustStoreType = TlsTrustStoreType.JKS,
    val tlsTrustStorePath: String? = null,
    val tlsTrustStorePassword: Secret? = null,
    val authentication: ClientAuthentication? = null,
    val policies: Policies = Policies()
) {
    init {
        require(
            brokerServiceUrl.startsWith("pulsar://") ||
                brokerServiceUrl.startsWith("pulsar+ssl://")
        ) { "brokerServiceUrl MUST start with pulsar:// or pulsar+ssl://" }

        require(
            webServiceUrl.startsWith("http://") ||
                webServiceUrl.startsWith("https://")
        ) { "webServiceUrl MUST start with http:// or https://" }

        require(tenant.isNotEmpty()) { "tenant can NOT be empty" }

        require(namespace.isNotEmpty()) { "namespace can NOT be empty" }

        if (useKeyStoreTls) {
            require(tlsTrustStorePath != null) { "tlsTrustStorePath MUST be defined if useKeyStoreTls is true" }
            require(tlsTrustStorePassword != null) { "tlsTrustStorePassword MUST be defined if useKeyStoreTls is true" }
        }
    }

    val admin: PulsarAdmin by lazy {
        PulsarAdmin
            .builder()
            .serviceHttpUrl(webServiceUrl)
            .allowTlsInsecureConnection(tlsAllowInsecureConnection)
            .enableTlsHostnameVerification(tlsEnableHostnameVerification)
            .also {
                if (tlsTrustCertsFilePath != null) it.tlsTrustCertsFilePath(tlsTrustCertsFilePath)
            }
            .also {
                if (useKeyStoreTls) with(it) {
                    useKeyStoreTls(true)
                    tlsTrustStoreType(tlsTrustStoreType.toString())
                    tlsTrustStorePath(tlsTrustStorePath!!)
                    tlsTrustStorePassword(tlsTrustStorePassword!!.value)
                }
            }
            .also {
                when (authentication) {
                    is AuthenticationToken -> it.authentication(
                        AuthenticationFactory.token(authentication.token.value)
                    )
                    is AuthenticationAthenz -> it.authentication(
                        AuthenticationFactory.create(
                            org.apache.pulsar.client.impl.auth.AuthenticationAthenz::class.java.name,
                            Json.stringify(authentication)
                        )
                    )
                    is AuthenticationSasl -> it.authentication(
                        AuthenticationFactory.create(
                            org.apache.pulsar.client.impl.auth.AuthenticationSasl::class.java.name,
                            Json.stringify(authentication)
                        )
                    )
                    is AuthenticationOAuth2 -> it.authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(
                            authentication.issuerUrl,
                            authentication.privateKey,
                            authentication.audience
                        )
                    )
                    null -> Unit
                }
            }
            .build()
    }

    val client: PulsarClient by lazy {
        PulsarClient
            .builder()
            .serviceUrl(brokerServiceUrl)
            .allowTlsInsecureConnection(tlsAllowInsecureConnection)
            .enableTlsHostnameVerification(tlsEnableHostnameVerification)
            .also { if (tlsTrustCertsFilePath != null) it.tlsTrustCertsFilePath(tlsTrustCertsFilePath) }
            .also {
                if (useKeyStoreTls) with(it) {
                    useKeyStoreTls(true)
                    tlsTrustStoreType(tlsTrustStoreType.toString())
                    tlsTrustStorePath(tlsTrustStorePath!!)
                    tlsTrustStorePassword(tlsTrustStorePassword!!.value)
                }
            }
            .also {
                when (authentication) {
                    is AuthenticationToken -> it.authentication(
                        AuthenticationFactory.token(authentication.token.value)
                    )
                    is AuthenticationAthenz -> it.authentication(
                        AuthenticationFactory.create(
                            org.apache.pulsar.client.impl.auth.AuthenticationAthenz::class.java.name,
                            Json.stringify(authentication)
                        )
                    )
                    is AuthenticationSasl -> it.authentication(
                        AuthenticationFactory.create(
                            org.apache.pulsar.client.impl.auth.AuthenticationSasl::class.java.name,
                            Json.stringify(authentication)
                        )
                    )
                    is AuthenticationOAuth2 -> it.authentication(
                        AuthenticationFactoryOAuth2.clientCredentials(
                            authentication.issuerUrl,
                            authentication.privateKey,
                            authentication.audience
                        )
                    )
                    null -> Unit
                }
            }
            .build()
    }
}
