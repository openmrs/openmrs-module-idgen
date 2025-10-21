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

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.Date;
import java.util.List;

/**
 * Interface for IdentifierSource Service Methods
 */
public interface IdentifierSourceDAO {
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return the IdentifierSource that matches the given type and id
	 */
	IdentifierSource getIdentifierSource(Integer id) throws DAOException;
	
	/**
	 * @param includeRetired if true, also returns retired IdentifierSources
	 * @return all IdentifierSources
	 * @should return all identifier sources
	 */
	List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException;

	/**
	 * Persists a IdentifierSource, either as a save or update.
	 * @param identifierSource
	 * @return the IdentifierSource that was passed in
	 */
	IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws DAOException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 */
	void purgeIdentifierSource(IdentifierSource identifierSource) throws DAOException;
	
	/**
	 * Returns available identifiers from a pool 
	 */
	List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) throws DAOException;
	
	/**
	 * Returns Pooled Identifiers for the given source, with the given status options
	 */
	int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) throws DAOException;

    /**
     * @param autoGenerationOptionId
     * @return AutoGenerationOption
     */
    AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws DAOException;

    /**
	 * @see IdentifierSourceService#getAutoGenerationOptionByUuid(String)
	 */
    AutoGenerationOption getAutoGenerationOptionByUuid(String uuid);
    
	/**
	 * @param type
	 * @param location
     * @return the AutoGenerationOption that matches the given PatientIdentifierType and location
     * @should return AutoGenerationOptions that do not have an associated location
	 */
	AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws DAOException;

    /**
     * @param type
     * @return the AutoGenerationOption that matches the given PatientIdentifierType
     * @throws DAOException
     */
    List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws DAOException;

    /**
     * @param type
     * @return the AutoGenerationOption that matches the given PatientIdentifierType
     */
    AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws DAOException;

    /**
	 * Persists a AutoGenerationOption, either as a save or update.
	 * @param option
	 * @return the AutoGenerationOption that was passed in
	 */
	AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws DAOException;
	
	/**
	 * Deletes a AutoGenerationOption from the database.
	 * @param option the AutoGenerationOption to purge
	 * @should delete an AutoGenerationOption from the system
	 */
	void purgeAutoGenerationOption(AutoGenerationOption option) throws DAOException;
	
	/**
	 * Saves a new Log Entry
	 * @throws DAOException
	 */
	LogEntry saveLogEntry(LogEntry logEntry) throws DAOException;
	
	/**
	 * Retrieves the Log Entries that match the supplied parameters.  All parameters are optional.
	 * @throws DAOException
	 */
	List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate,
										String identifier, User generatedBy, String comment) throws DAOException;

	/**
	 * Retrieves the most recent Log Entry for the given source, based on generation date and auto incremented id
	 * @param source - the identifier source for which to return the log entry
	 * @return LogEntry - the most recent LogEntry that matches the given source
	 * @throws DAOException
	 */
	LogEntry getMostRecentLogEntry(IdentifierSource source) throws DAOException;

    /**
     * @param uuid
     * @return the IdentifierSource with the given uuid
     */
    IdentifierSource getIdentifierSourceByUuid(String uuid);
    
    /**
     * @see IdentifierSourceService#getIdentifierSourcesByType(PatientIdentifierType)
     */
    List<IdentifierSource> getIdentifierSourcesByType(PatientIdentifierType patientIdentifierType);

    /**
     * Updates generator's sequenceValue in the database via SQL, bypassing Hibernate caches
     * @param generator
     * @param sequenceValue
     */
    void saveSequenceValue(SequentialIdentifierGenerator generator, long sequenceValue);

    /**
     * Gets generator's sequenceValue from the database via SQL, bypassing Hibernate caches
     * @param generator
     * @return
     */
    Long getSequenceValue(SequentialIdentifierGenerator generator);

    /**
     * Refresh an identifier source
     * @param source
     */
    void refreshIdentifierSource(IdentifierSource source);
}
