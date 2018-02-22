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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.contract.IdentifierType;
import org.openmrs.module.idgen.serialization.ObjectMapperRepository;
import org.openmrs.module.idgen.web.IdgenWsConstants;
import org.openmrs.module.idgen.web.services.IdentifierTypeServiceWrapper;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/" + IdgenWsConstants.PATH_IDGEN_IDTYPE)
public class IdgenIdentifierTypeController {
	
	public final static String encoding = StandardCharsets.UTF_8.toString();
	public final static String contentType = "application/json;charset=" + encoding;

    @Autowired
    IdentifierTypeServiceWrapper identifierTypeServiceWrapper;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPrimaryAndExtraIdentifierTypes() throws IOException {
        if(!Context.isAuthenticated() || !Context.hasPrivilege("Get Identifier Types")){
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);
        }

        final List<IdentifierType> allIdentifierType = identifierTypeServiceWrapper.getPrimaryAndExtraIdentifierTypes();
        ObjectMapperRepository objectMapperRepository = new ObjectMapperRepository();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.set("content-type", contentType);
	
        return new ResponseEntity<String>(objectMapperRepository.writeValueAsString(allIdentifierType), headers, HttpStatus.OK);
    }
}