package org.openmrs.module.idgen.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.request.GenerateIdentifierRequest;
import org.openmrs.module.idgen.web.serialization.ObjectMapperRepository;
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
            if (((SequentialIdentifierGenerator) identifierSource).getPrefix().equals(request.getIdentifierSourceName())) {
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

        List<org.openmrs.module.idgen.web.contract.IdentifierSource> result = new ArrayList<org.openmrs.module.idgen.web.contract.IdentifierSource>();

        for (IdentifierSource identifierSource : allIdentifierSources) {
            String prefix = null;
            if (identifierSource instanceof SequentialIdentifierGenerator) {
                prefix = ((SequentialIdentifierGenerator) (identifierSource)).getPrefix();
            }
            result.add(new org.openmrs.module.idgen.web.contract.IdentifierSource(identifierSource.getUuid(), identifierSource.getName(), prefix));
        }

        ObjectMapperRepository objectMapperRepository = new ObjectMapperRepository();
        return objectMapperRepository.writeValueAsString(result);
    }
}

