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

import org.junit.Before;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.rest.resource.AutoGenerationOptionResource;

import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AutoGenerationOptionResourceTest extends BaseDelegatingResourceTest<AutoGenerationOptionResource, AutoGenerationOption> {
	
	public static final int AUTO_GENERATION_OPTION_ID = 2;
	
	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}
	
	@Override
	public AutoGenerationOption newObject() {
		return Context.getService(IdentifierSourceService.class).getAutoGenerationOption(AUTO_GENERATION_OPTION_ID);
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("uuid", getUuidProperty());
		assertPropPresent("location");
		assertPropPresent("identifierType");
		assertPropPresent("source");
		assertPropEquals("manualEntryEnabled", getObject().isManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().isAutomaticGenerationEnabled());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("uuid", getUuidProperty());
		assertPropPresent("location");
		assertPropPresent("identifierType");
		assertPropPresent("source");
		assertPropEquals("manualEntryEnabled", getObject().isManualEntryEnabled());
		assertPropEquals("automaticGenerationEnabled", getObject().isAutomaticGenerationEnabled());
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropEquals("uuid", getUuidProperty());
		assertPropEquals("display", getDisplayProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return getObject().getIdentifierType() + " - " + getObject().getSource() + " - " + getObject().getLocation();
	}
	
	@Override
	public String getUuidProperty() {
		return getObject().getUuid();
	}
	
}
