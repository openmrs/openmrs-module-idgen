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
	public static final String GP_PRIMARY_IDTYPE = "bahmni.primaryIdentifierType";

	public static final String GP_EXTRA_IDTYPES = "bahmni.extraPatientIdentifierTypes";
}