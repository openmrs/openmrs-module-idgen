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
package org.openmrs.module.idgen.service.db;

import org.openmrs.api.db.DAOException;
import org.openmrs.module.idgen.IdentifierSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for IdentifierSource Service Methods
 */
@Transactional
public interface IdentifierSourceDAO {
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return the IdentifierSource that matches the given type and id
	 */
	@Transactional(readOnly = true)
	public IdentifierSource getIdentifierSource(Integer id) throws DAOException;

	/**
	 * Persists a IdentifierSource, either as a save or update.
	 * @param identifierSource
	 * @return the IdentifierSource that was passed in
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws DAOException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 */
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) throws DAOException;
}
