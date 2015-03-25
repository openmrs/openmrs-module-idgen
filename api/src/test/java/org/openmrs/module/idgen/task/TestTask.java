/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.task;

import org.openmrs.api.context.Context;

/**
 * Just to test that we're using the spring scheduler right
 */
public class TestTask extends IdgenTask {

    static boolean hasRun = false;
    static boolean wasAuthenticated = false;

    @Override
    public Runnable getRunnableTask() {
        return new Runnable() {
            @Override
            public void run() {
                hasRun = true;
                wasAuthenticated = Context.isAuthenticated();
            }
        };
    }

    public static boolean hasRun() {
        return hasRun;
    }

    public static boolean wasAuthenticated() {
        return wasAuthenticated;
    }

    public static void reset() {
        hasRun = false;
        wasAuthenticated = false;
    }
}
