/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;


import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.junit.Before;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;


public class IdentifierSourceResourceTest extends BaseDelegatingResourceTest<IdentifierSourceResource, IdentifierSource> {
    
    public static final String IDENTIFIER_SOURCE_UUID = "0d47284f-9e9b-4a81-a88b-8bb42bc0a901";

    @Before
    public void setup() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    @Override
    public String getDisplayProperty() {
        return getObject().getIdentifierType() + " - " 
                + getObject().getName() + " - "
                + getObject().getClass().getName();
    }

    @Override
    public String getUuidProperty() {
        return IDENTIFIER_SOURCE_UUID;
    }

    @Override
    public IdentifierSource newObject() {
        return Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(getUuidProperty());
    }
    
    @Override
    public void validateRefRepresentation() throws Exception {
        super.validateRefRepresentation();
        assertPropEquals("uuid", getUuidProperty());
        assertPropEquals("display", getDisplayProperty());
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("uuid", getUuidProperty());
        assertPropPresent("name");
        assertPropPresent("description");
        assertPropPresent("identifierType");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("uuid", getUuidProperty());
        assertPropPresent("name");
        assertPropPresent("description");
        assertPropPresent("identifierType");
    }
    
}
