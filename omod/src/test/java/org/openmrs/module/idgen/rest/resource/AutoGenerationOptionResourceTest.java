/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.idgen.rest.resource;

import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.api.context.Context;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.rest.resource.AutoGenerationOptionResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.Hyperlink;
//import org.openmrs.module.idgen.web.RestTestConstants;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AutoGenerationOptionResourceTest extends BaseDelegatingResourceTest<AutoGenerationOptionResource, AutoGenerationOption> {
	
	@Before
	public void setUp() throws Exception {
    executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
  }

	@Override
	public AutoGenerationOption newObject() {
		return Context.getService(IdentifierSourceService.class).getAutoGenerationOption(2);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("identifierType", getObject().getIdentifierType());
		assertPropEquals("source", getObject().getSource());
		assertPropEquals("manualEntryEnabled", getObject().isManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().isAutomaticGenerationEnabled());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("identifierType", getObject().getIdentifierType());
		assertPropEquals("source", getObject().getSource());
		assertPropEquals("manualEntryEnabled", getObject().isManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().isAutomaticGenerationEnabled());
	}
	
	@Override
	public String getDisplayProperty() {
		return getObject().getIdentifierType() +" - "+ getObject().getSource();
	}

	@Override
	public String getUuidProperty() {
		return getObject().getSource().getUuid();
	}
	
}
