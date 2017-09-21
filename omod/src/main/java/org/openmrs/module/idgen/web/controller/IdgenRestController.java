package org.openmrs.module.idgen.web.controller;

import org.springframework.stereotype.Controller;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE)
public class IdgenRestController extends MainResourceController {

 public static final String IDGEN_NAMESPACE = "/idgen";

 @Override
 public String getNamespace() {
  return RestConstants.VERSION_1 + IDGEN_NAMESPACE;
 }

}
