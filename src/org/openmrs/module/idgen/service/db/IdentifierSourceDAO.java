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

import java.util.List;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.PooledIdentifier;
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
	 * @param includeRetired if true, also returns retired IdentifierSources
	 * @return all IdentifierSources
	 * @should return all identifier sources
	 */
	@Transactional(readOnly = true)
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException;

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
	
	/**
	 * Returns available identifiers from a pool 
	 */
	@Transactional(readOnly=true)
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) throws DAOException;
	
	/**
	 * Returns Pooled Identifiers for the given source, with the given status options
	 */
	@Transactional(readOnly=true)
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) throws DAOException;
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return the AutoGenerationOption that matches the given PatientIdentifierType
	 */
	@Transactional(readOnly = true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws DAOException;
	
	/**
	 * Persists a AutoGenerationOption, either as a save or update.
	 * @param option
	 * @return the AutoGenerationOption that was passed in
	 */
	@Transactional
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws DAOException;
	
	/**
	 * Deletes a AutoGenerationOption from the database.
	 * @param option the AutoGenerationOption to purge
	 * @should delete an AutoGenerationOption from the system
	 */
	@Transactional
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws DAOException;
}
