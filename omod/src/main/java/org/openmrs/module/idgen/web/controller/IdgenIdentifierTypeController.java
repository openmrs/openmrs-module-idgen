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