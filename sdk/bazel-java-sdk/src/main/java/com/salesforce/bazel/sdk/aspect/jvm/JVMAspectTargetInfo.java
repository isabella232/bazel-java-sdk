/**
 * Copyright (c) 2019, Salesforce.com, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */
package com.salesforce.bazel.sdk.aspect.jvm;

 import java.io.File;
 import java.util.ArrayList;
 import java.util.List;

 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.json.simple.parser.JSONParser;

 import com.salesforce.bazel.sdk.aspect.AspectTargetInfo;

 /**
  * The subclass of AspectTargetInfo for JVM based rules.
  * <p>
  * The JSON document format is like this for a java_library rule:
  *
  * <pre>
 {
   "build_file_artifact_location":"helloworld/BUILD",
   "dependencies":["//proto:helloworld_java_proto"],
   "generated_jars":[],
   "jars":[
     {"interface_jar":"bazel-out/darwin-fastbuild/bin/helloworld/libhelloworld-hjar.jar",
      "jar":"bazel-out/darwin-fastbuild/bin/helloworld/libhelloworld.jar",
      "source_jar":"bazel-out/darwin-fastbuild/bin/helloworld/libhelloworld-src.jar"
     }
    ],
    "kind":"java_library",
    "label":"//helloworld:helloworld",
    "sources":["helloworld/src/main/java/helloworld/HelloWorld.java"]
 }
  * </pre>
  */
 public class JVMAspectTargetInfo extends AspectTargetInfo {
     protected String mainClass;
     protected List<JVMAspectOutputJarSet> generatedJars;
     protected List<JVMAspectOutputJarSet> jars;

     JVMAspectTargetInfo(File aspectDataFile, JSONObject aspectObject, JSONParser jsonParser,
         String workspaceRelativePath, String kind, String label, List<String> deps) throws Exception {
         super(aspectDataFile, workspaceRelativePath, kind, label, deps, null);

         JSONObject ideInfoObj = (JSONObject) aspectObject.get("java_ide_info");
         if (ideInfoObj != null) {
             sources = loadSources(ideInfoObj);

             List<JVMAspectOutputJarSet> jarsList = jsonArrayToJarArray(ideInfoObj.get("jars"), jsonParser);
             jars = jarsList;

             List<JVMAspectOutputJarSet> generatedJarsList =
                     jsonArrayToJarArray(ideInfoObj.get("generated_jars"), jsonParser);
             generatedJars = generatedJarsList;

             String mainClass = (String) ideInfoObj.get("main_class");
             this.mainClass = mainClass;
         } else {
             sources = new ArrayList<>();
             jars = new ArrayList<>();
             generatedJars = new ArrayList<>();
         }
     }

     /**
      * List of jars generated by annotations processors when building this target.
      */
     public List<JVMAspectOutputJarSet> getGeneratedJars() {
         return generatedJars;
     }

     /**
      * List of jars generated by building this target.
      */
     public List<JVMAspectOutputJarSet> getJars() {
         return jars;
     }

     /**
      * The value of the "main_class" attribute of this target, may be null if this target doesn't specify a main_class.
      */
     public String getMainClass() {
         return mainClass;
     }

     private static List<String> loadSources(JSONObject ideInfoObj) throws Exception {
         List<String> list = new ArrayList<>();
         if (ideInfoObj == null) {
             return list;
         }
         JSONArray sourceArray = (JSONArray) ideInfoObj.get("sources");
         if (sourceArray != null) {
             for (Object sourceObject : sourceArray) {
                 JSONObject sourceObj = (JSONObject) sourceObject;
                 Object pathObj = sourceObj.get("relative_path");
                 if (pathObj != null) {
                     list.add(pathObj.toString());
                 }
             }
         }
         return list;
     }

     private static List<JVMAspectOutputJarSet> jsonArrayToJarArray(Object arrayObject, JSONParser jsonParser)
             throws Exception {
         List<JVMAspectOutputJarSet> jarList = new ArrayList<>();
         if (arrayObject == null) {
             return jarList;
         }
         if (!(arrayObject instanceof JSONArray)) {
             return jarList;
         }

         JSONArray array = (JSONArray) arrayObject;
         for (Object jarSet : array) {
             JSONObject jarSetObject = (JSONObject) jsonParser.parse(jarSet.toString());
             jarList.add(new JVMAspectOutputJarSet(jarSetObject));
         }
         return jarList;
     }

     @Override
     public String toString() {
         StringBuffer builder = new StringBuffer();
         builder.append("AspectTargetInfo(\n");
         builder.append("  label = ").append(label).append(",\n");
         builder.append("  build_file_artifact_location = ").append(workspaceRelativePath).append(",\n");
         builder.append("  kind = ").append(kind).append(",\n");
         builder.append("  jars = [").append(commaJoiner(jars)).append("],\n");
         builder.append("  generated_jars = [").append(commaJoiner(generatedJars)).append("],\n");
         builder.append("  dependencies = [").append(commaJoiner(deps)).append("],\n");
         builder.append("  sources = [").append(commaJoiner(sources)).append("]),\n");
         builder.append("  main_class = ").append(mainClass).append("),\n");
         return builder.toString();
     }

 }
