/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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
