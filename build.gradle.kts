/*
 * Copyright 2026 dorkbox, llc
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

gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS   // always show the stacktrace!

plugins {
    id("com.dorkbox.GradleUtils") version "4.8"
    id("com.dorkbox.Licensing") version "3.1"
    id("com.dorkbox.VersionUpdate") version "3.2"
    id("com.dorkbox.GradlePublish") version "2.2"

    kotlin("jvm") version "2.3.0"
}


GradleUtils.load {
    group = "com.dorkbox"
    id = "Version"

    // - Minor/Patch number optional
    // - underscore permitted for prerelease status
    // - build-after-final-dot (minor/patch)
    description = "Java Semantic Versioning with exceptions"
    name = "Version"
    version = "3.2"

    vendor = "Dorkbox LLC"
    vendorUrl = "https://dorkbox.com"

    url = "https://git.dorkbox.com/dorkbox/Version"

    issueManagement {
        url = "${url}/issues"
        nickname = "Gitea Issues"
    }

    developer {
        id = "dorkbox"
        name = vendor
        email = "email@dorkbox.com"
    }
}
GradleUtils.defaults()
GradleUtils.compileConfiguration(JavaVersion.VERSION_25)


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


dependencies {
    api("com.dorkbox:Updates:1.3")

    testImplementation("junit:junit:4.13.2")
}
