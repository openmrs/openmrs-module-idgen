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
package org.openmrs.module.idgen.web.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/idgen")
public class IdgenIdentifierSourceController extends BaseRestController {

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String generateIdentifier(@RequestBody GenerateIdentifierRequest request) {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);
        for (IdentifierSource identifierSource : allIdentifierSources) {
            if (((SequentialIdentifierGenerator)identifierSource).getPrefix().equals(request.getIdentifierSourceName())) {
                return identifierSourceService.generateIdentifier(identifierSource, request.getComment());
            }
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/identifiersources")
    @ResponseBody
    public String getAllIdentifierSources() throws IOException {
        IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
        List<IdentifierSource> allIdentifierSources = identifierSourceService.getAllIdentifierSources(false);

        List<IdentifierSourceContract> result = new ArrayList<IdentifierSourceContract>();

        for (IdentifierSource identifierSource : allIdentifierSources) {
            String prefix = null;
            if (identifierSource instanceof SequentialIdentifierGenerator) {
                prefix = ((SequentialIdentifierGenerator) (identifierSource)).getPrefix();
            }
            result.add(new IdentifierSourceContract(identifierSource.getUuid(), identifierSource.getName(), prefix));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(result);
    }
}

