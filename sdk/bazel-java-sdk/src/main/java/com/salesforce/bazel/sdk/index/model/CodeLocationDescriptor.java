/**
 * Copyright (c) 2020, Salesforce.com, Inc. All rights reserved.
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
 */
package com.salesforce.bazel.sdk.index.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Location on disk of a file (e.g. foo.jar, foo.class or foo.java) that contains type(s) in either source or compiled
 * form.
 */
public class CodeLocationDescriptor {
    public CodeLocationIdentifier id; // e.g. org.slf4j:slf4j-api:1.3.4
    public String bazelLabel; // e.g. @maven//:org_slf4j_slf4j_api
    public File locationOnDisk;
    public List<ClassIdentifier> containedClasses;

    public CodeLocationDescriptor(File locationOnDisk, CodeLocationIdentifier id) {
        this.locationOnDisk = locationOnDisk;
        this.id = id;
    }

    public CodeLocationDescriptor(File locationOnDisk, CodeLocationIdentifier id, String bazelLabel) {
        this.locationOnDisk = locationOnDisk;
        this.id = id;
        this.bazelLabel = bazelLabel;
    }

    public void addClass(ClassIdentifier classId) {
        if (containedClasses == null) {
            containedClasses = new ArrayList<>(5);
        }
        containedClasses.add(classId);
    }
}
