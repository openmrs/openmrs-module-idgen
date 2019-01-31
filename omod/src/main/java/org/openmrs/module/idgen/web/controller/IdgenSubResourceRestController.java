package org.openmrs.module.idgen.web.controller;

import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE)
public class IdgenSubResourceRestController extends MainSubResourceController {
	
    @Override
    public String getNamespace() {
        return RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE;
    }

}
