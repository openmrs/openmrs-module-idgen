/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.web;

public class IdgenWsConstants  {
	
	/*
	 * Module ids
	 */
	public static final String MODULE_NAME = "Id Gen Web Services";
	
	public static final String MODULE_ARTIFACT_ID = "idgen-webservices";
	
	public static final String MODULE_SHORT_ID = "IdgenWs";
	
	public static final String MODULE_BASE_URL = "/" + MODULE_ARTIFACT_ID;
	
	/*
	 * URIs
	 */
	public static final String PATH_IDGEN_IDTYPE = "idgen/identifiertype";
	
	/*
	 * GPs
	 */
	public static final String GP_PRIMARY_IDTYPE = "openmrs.primaryIdentifierType";

	public static final String GP_EXTRA_IDTYPES = "openmrs.extraPatientIdentifierTypes";
}