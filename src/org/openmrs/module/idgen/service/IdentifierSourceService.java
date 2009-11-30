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
package org.openmrs.module.idgen.service;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.IdentifierSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for IdentifierSource Service Methods
 */
@Transactional
public interface IdentifierSourceService extends OpenmrsService {
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return the IdentifierSource that matches the given type and id
	 * @should return a saved sequential identifier generator
	 * @should return a saved rest identifier generator
	 * @should return a saved identifier pool
	 */
	@Transactional(readOnly = true)
	public IdentifierSource getIdentifierSource(Integer id) throws APIException;

	/**
	 * Persists a IdentifierSource, either as a save or update.
	 * @param identifierSource
	 * @return the IdentifierSource that was passed in
	 * @should save a sequential identifier generator for later retrieval
	 * @should save a rest identifier generator for later retrieval
	 * @should save an identifier pool for later retrieval
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 * @should delete an IdentifierSource from the system
	 */
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) throws APIException;
}
