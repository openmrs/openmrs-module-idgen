/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.idgen.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
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