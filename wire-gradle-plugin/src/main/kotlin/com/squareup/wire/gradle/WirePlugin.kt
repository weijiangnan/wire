/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.wire.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class WirePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.create("wire", WireExtension::class.java)

    project.afterEvaluate { project ->
      val sourceSet = extension.sourceSet ?: project.files("src/main/wire")

      val task = project.tasks.register("doWire", WireTask::class.java) {
        it.sourceFolders = sourceSet.files
        it.source(sourceSet)
        it.group = "wire"
        it.description = "Generate Wire protocol buffer implementation for .proto files"
      }

      project.tasks.named("compileKotlin").configure{ it.dependsOn(task) }
    }
  }
}