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

package io.infinitic.dashboard

import io.infinitic.dashboard.config.DashboardConfig
import io.infinitic.dashboard.modals.Modal
import io.infinitic.dashboard.panels.infrastructure.AllJobsPanel
import io.infinitic.dashboard.panels.infrastructure.task.TaskPanel
import io.infinitic.dashboard.panels.infrastructure.workflow.WorkflowPanel
import io.infinitic.dashboard.panels.settings.SettingsPanel
import io.infinitic.dashboard.panels.tasks.TasksPanel
import io.infinitic.dashboard.panels.workflows.WorkflowsPanel
import io.infinitic.dashboard.plugins.images.imagesPlugin
import io.infinitic.dashboard.plugins.tailwind.tailwindPlugin
import io.infinitic.pulsar.PulsarInfiniticAdmin
import io.infinitic.transport.pulsar.config.Pulsar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kweb.ElementCreator
import kweb.Kweb
import kweb.WebBrowser
import kweb.route
import mu.KotlinLogging
import org.apache.pulsar.client.admin.PulsarAdmin

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class DashboardServer(
    val pulsarAdmin: PulsarAdmin,
    val pulsar: Pulsar,
    val port: Int,
    val debug: Boolean,
) {
    init {
        Infinitic.admin = PulsarInfiniticAdmin(pulsarAdmin, pulsar)
    }

    private val logger = KotlinLogging.logger {}

    companion object {

        internal val scope = CoroutineScope(Dispatchers.IO + Job())

        /**
         * Create Dashboard from a custom PulsarAdmin and a DashboardConfig instance
         */
        @JvmStatic
        fun from(pulsarAdmin: PulsarAdmin, dashboardConfig: DashboardConfig) = DashboardServer(
            pulsarAdmin,
            dashboardConfig.pulsar,
            dashboardConfig.port,
            dashboardConfig.debug
        )

        /**
         * Create Dashboard from a DashboardConfig
         */
        @JvmStatic
        fun fromConfig(dashboardConfig: DashboardConfig): DashboardServer =
            from(dashboardConfig.pulsar.admin, dashboardConfig)

        /**
         * Create InfiniticWorker from file in resources directory
         */
        @JvmStatic
        fun fromConfigResource(vararg resources: String) =
            fromConfig(DashboardConfig.fromResource(*resources))

        /**
         * Create InfiniticWorker from file in system file
         */
        @JvmStatic
        fun fromConfigFile(vararg files: String) =
            fromConfig(DashboardConfig.fromFile(*files))
    }

    /**
     * Start dashboard server
     */
    fun start() {
        logger.info { "Starting dashboard server on port $port" }

        Kweb(debug = debug, port = port, plugins = listOf(tailwindPlugin, imagesPlugin)) {
            doc.body {
                route {
                    path(WorkflowsPanel.url) {
                        display(WorkflowsPanel)
                    }
                    path(TasksPanel.url) {
                        display(TasksPanel)
                    }
                    path(AllJobsPanel.url) {
                        display(AllJobsPanel)
                    }
                    path("/infra/t/{name}") {
                        display(TaskPanel.from(it.getValue("name").value))
                    }
                    path("/infra/w/{name}") {
                        display(WorkflowPanel.from(it.getValue("name").value))
                    }
                    path(SettingsPanel.url) {
                        display(SettingsPanel)
                    }
                    path("/") {
                        url.value = AllJobsPanel.url
                    }
                    notFound {
                        NotFound.render(this)
                    }
                }
            }
        }
    }

    private fun ElementCreator<*>.display(panel: Panel) {
        // selecting panel
        AppPanel.appState.selectPanel(panel)
        // rendering the app
        AppPanel.render(this)

        Modal.render(this)
    }
}

internal fun WebBrowser.routeTo(to: Panel) {
    // update current url
    url.value = to.url
}

internal object Infinitic {
    lateinit var admin: PulsarInfiniticAdmin
    val topicName by lazy { admin.topicName }
    val topics by lazy { admin.pulsarAdmin.topics() }
}
