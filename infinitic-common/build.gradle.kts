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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-test-fixtures")
}

dependencies {
    api(Libs.Serialization.json)
    api(Libs.JsonPath.jayway)
    implementation(Libs.Hoplite.core)
    implementation(Libs.Jackson.databind)
    implementation(Libs.Jackson.kotlin)
    implementation(Libs.Jackson.jsr310)
    implementation(Libs.Avro4k.core)
    implementation(Libs.Coroutines.core)
    implementation(Libs.Coroutines.jdk8)

    testImplementation(files("${project.rootDir}/buildSrc/build/libs/buildSrc.jar"))

    testFixturesImplementation(Libs.Kotlin.reflect)
    testFixturesImplementation(Libs.EasyRandom.core)
    testFixturesImplementation(Libs.Coroutines.core)
}

tasks.withType<KotlinCompile> {
    doFirst {
        // current version is put in resources
        File(project.projectDir.absolutePath, "/src/main/resources/version.properties").writeText(Ci.version)
    }
}

apply("../publish.gradle.kts")
