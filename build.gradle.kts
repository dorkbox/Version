/*
 * Copyright 2020 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.Instant

gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS   // always show the stacktrace!
gradle.startParameter.warningMode = WarningMode.All

plugins {
    java

    id("com.dorkbox.CrossCompile") version "1.0.1"
    id("com.dorkbox.GradleUtils") version "1.8"
    id("com.dorkbox.Licensing") version "2.1"
    id("com.dorkbox.VersionUpdate") version "1.7"
    id("com.dorkbox.GradlePublish") version "1.3"

    kotlin("jvm") version "1.3.72"
}

object Extras {
    // set for the project
    const val description = "Java Semantic Versioning with exceptions. Minor/Patch number optional and build-after-final-dot (minor/patch) permitted."
    const val group = "com.dorkbox"
    const val version = "2.4"

    // set as project.ext
    const val name = "Version"
    const val id = "Version"
    const val vendor = "Dorkbox LLC"
    const val vendorUrl = "https://dorkbox.com"
    const val url = "https://git.dorkbox.com/dorkbox/Version"
    val buildDate = Instant.now().toString()

    val JAVA_VERSION = JavaVersion.VERSION_1_6.toString()
}

///////////////////////////////
/////  assign 'Extras'
///////////////////////////////
GradleUtils.load("$projectDir/../../gradle.properties", Extras)
GradleUtils.fixIntellijPaths()

licensing {
    license(License.MIT) {
        description(Extras.description)
        author(Extras.vendor)
        url(Extras.url)

        author("G. Richard Bellamy")
        author("Kenduck")
        author("Larry Bordowitz <lbordowitz@yahoo-inc.com>")
        author("Martin Rüegg <martin.rueegg@bristolpound.org> <martin.rueegg@metaworx.ch>")
        author("Zafar Khaja <zafarkhaja@gmail.com>")
    }
}


sourceSets {
    main {
        java {
            setSrcDirs(listOf("src"))

            // want to include java files for the source. 'setSrcDirs' resets includes...
            include("**/*.java")
        }
    }

    test {
        java {
            setSrcDirs(listOf("test"))

            // want to include java files for the source. 'setSrcDirs' resets includes...
            include("**/*.java")
        }
    }
}

repositories {
    mavenLocal() // this must be first!
    jcenter()
}

///////////////////////////////
//////    Task defaults
///////////////////////////////
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    sourceCompatibility = Extras.JAVA_VERSION
    targetCompatibility = Extras.JAVA_VERSION
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

tasks.compileJava.get().apply {
    println("\tCompiling classes to Java $sourceCompatibility")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.FAIL
}

tasks.jar.get().apply {
    duplicatesStrategy = DuplicatesStrategy.FAIL

    manifest {
        // https://docs.oracle.com/javase/tutorial/deployment/jar/packageman.html
        attributes["Name"] = Extras.name

        attributes["Specification-Title"] = Extras.name
        attributes["Specification-Version"] = Extras.version
        attributes["Specification-Vendor"] = Extras.vendor

        attributes["Implementation-Title"] = "${Extras.group}.${Extras.id}"
        attributes["Implementation-Version"] = Extras.buildDate
        attributes["Implementation-Vendor"] = Extras.vendor

        attributes["Automatic-Module-Name"] = Extras.id
    }
}

configurations.all {
    resolutionStrategy {
        // fail eagerly on version conflict (includes transitive dependencies)
        // e.g. multiple different versions of the same dependency (group and name are equal)
        failOnVersionConflict()

        // if there is a version we specified, USE THAT VERSION (over transitive versions)
        preferProjectModules()

        // cache dynamic versions for 10 minutes
        cacheDynamicVersionsFor(10 * 60, "seconds")

        // don't cache changing modules at all
        cacheChangingModulesFor(0, "seconds")
    }
}


dependencies {
    testImplementation("junit:junit:4.12")
}

publishToSonatype {
    groupId = Extras.group
    artifactId = Extras.id
    version = Extras.version

    name = Extras.name
    description = Extras.description
    url = Extras.url

    vendor = Extras.vendor
    vendorUrl = Extras.vendorUrl

    issueManagement {
        url = "${Extras.url}/issues"
        nickname = "Gitea Issues"
    }

    developer {
        id = "dorkbox"
        name = Extras.vendor
        email = "email@dorkbox.com"
    }
}
