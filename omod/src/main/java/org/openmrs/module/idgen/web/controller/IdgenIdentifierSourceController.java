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
package org.openmrs.module.idgen.web.controller;

import org.openmrs.module.idgen.serialization.ObjectMapperRepository;
import org.openmrs.module.idgen.web.services.IdentifierSourceServiceWrapper;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/idgen")
public class IdgenIdentifierSourceController extends BaseRestController {

    @Autowired
    private IdentifierSourceServiceWrapper identifierSourceServiceWrapper;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String generateIdentifier(@RequestBody GenerateIdentifierRequest request) throws Exception {
        return identifierSourceServiceWrapper.generateIdentifier(request.getIdentifierSourceName(), request.getComment());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/identifiersources")
    @ResponseBody
    public String getAllIdentifierSourcesOfPrimaryIdentifierType() throws IOException {
        List<org.openmrs.module.idgen.contract.IdentifierSource> result = identifierSourceServiceWrapper.getAllIdentifierSourcesOfPrimaryIdentifierType();
        ObjectMapperRepository objectMapperRepository = new ObjectMapperRepository();
        return objectMapperRepository.writeValueAsString(result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/latestidentifier")
    @ResponseBody
    public String getSequenceValue(@RequestParam(value = "sourceName") String sourceName) throws Exception {
        return identifierSourceServiceWrapper.getSequenceValue(sourceName);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/latestidentifier")
    @ResponseBody
    public Long saveSequenceValue(@RequestBody SetLatestIdentifierRequest request) throws Exception {
        identifierSourceServiceWrapper.saveSequenceValue(request.getIdentifier(), request.getSourceName());
        return request.getIdentifier();
    }
}
