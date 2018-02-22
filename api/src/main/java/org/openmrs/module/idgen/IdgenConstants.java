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
package org.openmrs.module.idgen;

/**
 * Constants that are useful for this module
 */
public class IdgenConstants {
	
	/**
	 * Privilege which grants users permission to manage identifier sources
	 */
	public static final String PRIV_MANAGE_IDENTIFIER_SOURCES = "Manage Identifier Sources";
	
	/**
	 * Privilege which grants users permission to manage auto-generation options
	 */
	public static final String PRIV_MANAGE_AUTOGENERATION_OPTIONS = "Manage Auto Generation Options";

	/**
	 * Privilege which grants users permission to generate a batch of identifiers to a file for offline use
	 */
	public static final String PRIV_GENERATE_BATCH_OF_IDENTIFIERS = "Generate Batch of Identifiers";
	
	/**
	 * Privilege which grants users permission to upload a batch of identifiers
	 */
	public static final String PRIV_UPLOAD_BATCH_OF_IDENTIFIERS = "Upload Batch of Identifiers";
}
