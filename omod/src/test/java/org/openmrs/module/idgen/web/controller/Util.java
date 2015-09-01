/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.util.OpenmrsUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Utilities requires for unit tests
 */
public class Util {
    /**
     * @param result the SimpleObject that contains results
     * @return
     * @throws Exception
     */
    public static List<Object> getResultsList(SimpleObject result) throws Exception {
        return (List<Object>) PropertyUtils.getProperty(result, "results");
    }

    /**
     * @param result
     * @return
     * @throws Exception
     */
    public static int getResultsSize(SimpleObject result) throws Exception {
        return getResultsList(result).size();
    }

}
