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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;
import org.openmrs.module.idgen.service.db.IdentifierSourceDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Base Implementation of the IdentifierSourceService API
 */
@Transactional
public class BaseIdentifierSourceService extends BaseOpenmrsService implements IdentifierSourceService {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * Registry of Processors for Identifier Sources
	 */
	private static Map<Class<? extends IdentifierSource>, IdentifierSourceProcessor> processors = null;

    /**
     * A map from the id of an identifier source, to an object we can lock on for that identifier source
     */
    private ConcurrentHashMap<Integer, Object> syncLocks = new ConcurrentHashMap<Integer, Object>();
	
	//***** PROPERTIES *****
	
	private IdentifierSourceDAO dao = null;
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see IdentifierSourceService#getIdentifierSourceTypes()
	 */
	@Transactional(readOnly = true)
	public List<Class<? extends IdentifierSource>> getIdentifierSourceTypes() {
		List<Class<? extends IdentifierSource>> sourceTypes = new ArrayList<Class<? extends IdentifierSource>>();
		sourceTypes.add(SequentialIdentifierGenerator.class);
		sourceTypes.add(RemoteIdentifierSource.class);
		sourceTypes.add(IdentifierPool.class);
		return sourceTypes;
	}

	/** 
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	@Transactional(readOnly = true)
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return dao.getIdentifierSource(id);
	}
	
	/** 
	 * @see IdentifierSourceService#getAllIdentifierSources(boolean)
	 */
	@Transactional(readOnly = true)
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws APIException {
		return dao.getAllIdentifierSources(includeRetired);
	}

	/** 
	 * @see IdentifierSourceService#getIdentifierSourcesByType(boolean)
	 */
	@Transactional(readOnly = true)
	public Map<PatientIdentifierType, List<IdentifierSource>> getIdentifierSourcesByType(boolean includeRetired) throws APIException {
		Map<PatientIdentifierType, List<IdentifierSource>> m = new LinkedHashMap<PatientIdentifierType, List<IdentifierSource>>();
		for (PatientIdentifierType t : Context.getPatientService().getAllPatientIdentifierTypes()) {
			m.put(t, new ArrayList<IdentifierSource>());
		}
		for (IdentifierSource s : getAllIdentifierSources(includeRetired)) {
			m.get(s.getIdentifierType()).add(s);
		}
		return m;
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException {
		if (identifierSource.getName() == null) {
			throw new APIException("Identifier Source name is required");
		}
		if (identifierSource.getUuid() == null) {
			identifierSource.setUuid(UUID.randomUUID().toString());
		}
		User u = Context.getAuthenticatedUser();
		Date today = new Date();
		if (identifierSource.getDateCreated() == null) {
			identifierSource.setDateCreated(today);
		}
		if (identifierSource.getCreator() == null) {
			identifierSource.setCreator(u);
		}
		if (identifierSource.getId() != null) {
			identifierSource.setChangedBy(u);
			identifierSource.setDateChanged(today);
		}
		return dao.saveIdentifierSource(identifierSource);
	}

	/** 
	 * @see IdentifierSourceService#purgeIdentifierSource(IdentifierSource)
	 */
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		dao.purgeIdentifierSource(identifierSource);
	}
	
	/**
	 * 
	 * @see IdentifierSourceService#getProcessor(IdentifierSource)
	 */
	@Transactional(readOnly=true)
	public IdentifierSourceProcessor getProcessor(IdentifierSource source) {
		return getProcessors().get(source.getClass());
	}
	
	/**
	 * @see IdentifierSourceService#registerProcessor(Class, IdentifierSourceProcessor)
	 */
	public void registerProcessor(Class<? extends IdentifierSource> type, IdentifierSourceProcessor processorToRegister) throws APIException {
		getProcessors().put(type, processorToRegister);
	}
	
	/** 
	 * @see IdentifierSourceService#generateIdentifiers(IdentifierSource, Integer, String)
	 */
	public List<String> generateIdentifiers(IdentifierSource source, Integer batchSize, String comment) throws APIException {

        if (log.isDebugEnabled()) {
            log.debug("About to enter synchronized block for " + source.getName());
        }
        Object syncLock = getSyncLock(source.getId());
        synchronized (syncLock) {
            // note that we pass the *id* of the source here, not the source itself; this is because we don't want to pass
            // Hibernate-managed objects between Hibernate sessions--and the  @Transactional(propagation = Propagation.REQUIRES_NEW)
            // starts a new transaction (which Hibernate handles by opening a new session)
            // we refresh the source afterwards to pick up any changes made during the inner transaction
            // *note that because of this refresh any changes made to the identifier source up to this point will be lost*
            List<String> identifiers = Context.getService(IdentifierSourceService.class).generateIdentifiersInternal(source.getId(), batchSize, comment);
            dao.refreshIdentifierSource(source);
            return identifiers;
        }
	}

    /**
     * This method exists because we want a transaction to be opened and closed inside the synchronized block in generateIdentifiers
     * @param source
     * @param batchSize
     * @param comment
     * @param processor
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> generateIdentifiersInternal(Integer sourceId, Integer batchSize, String comment) {

        IdentifierSource source = getIdentifierSource(sourceId);
        IdentifierSourceProcessor processor = getProcessor(source);

        if (processor == null) {
            throw new APIException("No registered processor found for source: " + source);
        }

        List<String> identifiers = processor.getIdentifiers(source, batchSize);

        Date now = new Date();
        User currentUser = Context.getAuthenticatedUser();

        for (String s : identifiers) {
            LogEntry logEntry = new LogEntry(source, s, now, currentUser, comment);
            dao.saveLogEntry(logEntry);
        }

        return identifiers;
    }

    private Object getSyncLock(Integer identifierSourceId) {
        // this method does not need to be synchronized, because putIfAbsent is atomic
        syncLocks.putIfAbsent(identifierSourceId, new Object());
        return syncLocks.get(identifierSourceId);
    }

    /**
     * @see org.openmrs.module.idgen.service.IdentifierSourceService#generateIdentifier(org.openmrs.PatientIdentifierType, java.lang.String)
     */
    public String generateIdentifier(PatientIdentifierType type, String comment) {
        AutoGenerationOption option = getAutoGenerationOption(type);

        if (option != null && option.isAutomaticGenerationEnabled()) {
            return generateIdentifier(option.getSource(), comment);
        }
        else {
            return null;
        }
    }

    /**
	 * @see org.openmrs.module.idgen.service.IdentifierSourceService#generateIdentifier(org.openmrs.PatientIdentifierType, org.openmrs.Location, java.lang.String)
	 */
	public String generateIdentifier(PatientIdentifierType type, Location location, String comment) {
		AutoGenerationOption option = getAutoGenerationOption(type, location);
	
		if (option != null && option.isAutomaticGenerationEnabled()) {
			return generateIdentifier(option.getSource(), comment);
		}
		else {
			return null;
		}
	}
	
	/** 
	 * @see IdentifierSourceService#generateIdentifier(IdentifierSource, String)
	 */
	public String generateIdentifier(IdentifierSource source, String comment) throws APIException {
		List<String> l = generateIdentifiers(source, 1, comment);
		if (l == null || l.size() != 1) {
			throw new RuntimeException("Generate identifier method did not return only one identifier");
		}
		return l.get(0);
	}

	/** 
	 * @see IdentifierSourceService#getAvailableIdentifiers(IdentifierPool, int)
	 */
	@Transactional(readOnly=true)
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) throws APIException {
	    return dao.getAvailableIdentifiers(pool, quantity);
	}

	/** 
	 * @see IdentifierSourceService#getQuantityInPool(IdentifierPool, boolean, boolean)
	 */
	@Transactional(readOnly=true)
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) throws APIException {
		return dao.getQuantityInPool(pool, availableOnly, usedOnly);
	}

	/** 
	 * @see IdentifierSourceService#addIdentifiersToPool(IdentifierPool, List)
	 */
	@Transactional
	public void addIdentifiersToPool(IdentifierPool pool, List<String> identifiers) throws APIException {
		for (String identifier : identifiers) {
			pool.addIdentifierToPool(identifier);
		}
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(pool);
	}
	
	/** 
	 * @see IdentifierSourceService#addIdentifiersToPool(IdentifierPool, Integer)
	 */
	@Transactional
	public void addIdentifiersToPool(IdentifierPool pool, Integer batchSize) throws APIException {
		List<String> identifiers = generateIdentifiers(pool.getSource(), batchSize, "Generating identifier for pool " + pool.getName());
		addIdentifiersToPool(pool, identifiers);
	}

    /**
     * @see IdentifierSourceService#getAutoGenerationOption(Integer)
     */
    @Transactional(readOnly=true)
    @Override
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws APIException {
        return dao.getAutoGenerationOption(autoGenerationOptionId);
    }

    /**
     * @see IdentifierSourceService#getAutoGenerationOption(PatientIdentifierType,Location)
     */
    @Transactional(readOnly=true)
    public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws APIException {
        return dao.getAutoGenerationOption(type, location);
    }

    /**
     * @see IdentifierSourceService#getAutoGenerationOption(PatientIdentifierType,Location)
     */
    @Transactional(readOnly = true)
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws APIException {
        return dao.getAutoGenerationOptions(type);
    }

    /**
	 * @see IdentifierSourceService#getAutoGenerationOption(PatientIdentifierType)
	 */
	@Transactional(readOnly=true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws APIException {
		return dao.getAutoGenerationOption(type);
	}

	/** 
	 * @see IdentifierSourceService#saveAutoGenerationOption(AutoGenerationOption)
	 */
	@Transactional
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws APIException {
		return dao.saveAutoGenerationOption(option);
	}

	/** 
	 * @see .IdentifierSourceService#purgeAutoGenerationOption(AutoGenerationOption)
	 */
	@Transactional
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws APIException {
		dao.purgeAutoGenerationOption(option);
		
	}
	
	//***** PROPERTY ACCESS *****

	/**
	 * @return the dao
	 */
	public IdentifierSourceDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(IdentifierSourceDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @return the processors
	 */
	public Map<Class<? extends IdentifierSource>, IdentifierSourceProcessor> getProcessors() {
		if (processors == null) {
			processors = new HashMap<Class<? extends IdentifierSource>, IdentifierSourceProcessor>();
		}
		return processors;
	}

	/**
	 * Adds to processors, rather than replacing.
	 * @param processorsToAdd the processors to add
	 */
	public void setProcessors(Map<Class<? extends IdentifierSource>, IdentifierSourceProcessor> processorsToAdd) {
		if (processorsToAdd != null) {
			for (Map.Entry<Class<? extends IdentifierSource>, IdentifierSourceProcessor> entry : processorsToAdd.entrySet()) {
				registerProcessor(entry.getKey(), entry.getValue());
			}
		}
	}

	/** 
	 * @see IdentifierSourceService#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws APIException {
		return dao.getLogEntries(source, fromDate, toDate, identifier, generatedBy, comment);
	}

    /**
     * Adds identifiers to a pool presumably from a pool's sequential source in batches of 100 until the min
     * pool quantity of unused is reached.
     * @param pool
     */
    @Transactional
    public void checkAndRefillIdentifierPool(IdentifierPool pool){
        if (pool.getSource() != null && (pool.getSource() instanceof SequentialIdentifierGenerator || pool.getSource() instanceof RemoteIdentifierSource)) {
            while (pool.getMinPoolSize() > getQuantityInPool(pool, true, false)){
                addIdentifiersToPool(pool, Integer.valueOf(pool.getBatchSize()));
            }
        }
    }
    
    /**
     * @see org.openmrs.module.idgen.service.IdentifierSourceService#getPatientIdentifierTypesByAutoGenerationOption(java.lang.Boolean, java.lang.Boolean)
     */
    public List<PatientIdentifierType> getPatientIdentifierTypesByAutoGenerationOption(Boolean manualEntryEnabled, Boolean autoGenerationEnabled) {
    	
    	List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
    	
    	for (PatientIdentifierType type : Context.getPatientService().getAllPatientIdentifierTypes()) {
    		AutoGenerationOption option = getAutoGenerationOption(type);
    		if (option != null && option.isManualEntryEnabled() == manualEntryEnabled.booleanValue() && option.isAutomaticGenerationEnabled() == autoGenerationEnabled.booleanValue()) {
    			identifierTypes.add(type);
    		}
    	}
    	return identifierTypes;
   }

    /**
     * @see IdentifierSourceService#getIdentifierSourceByUuid(String)
     */
    @Override
    public IdentifierSource getIdentifierSourceByUuid(String uuid) {
        return dao.getIdentifierSourceByUuid(uuid);
    }
    
    /**
     * @see IdentifierSourceService#getIdentifierSourcesByType(PatientIdentifierType)
     */
    @Override
    public List<IdentifierSource> getIdentifierSourcesByType(PatientIdentifierType patientIdentifierType){
        return dao.getIdentifierSourcesByType(patientIdentifierType);
    }

    /**
     * @see IdentifierSourceService#saveSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator, long)
     */
    @Override
    public void saveSequenceValue(SequentialIdentifierGenerator seq, long sequenceValue) {
        dao.saveSequenceValue(seq, sequenceValue);
    }

    /**
     * @see IdentifierSourceService#getSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator)
     */
    @Override
    public Long getSequenceValue(SequentialIdentifierGenerator seq) {
        return dao.getSequenceValue(seq);
    }

    /**
     * @see IdentifierSourceService#retireIdentifierSource(org.openmrs.module.idgen.IdentifierSource, String)
     */
	@Override
	public void retireIdentifierSource(IdentifierSource identifierSource, String reason) throws APIException {
		identifierSource.setRetired(true);
		identifierSource.setRetireReason(reason);
		dao.saveIdentifierSource(identifierSource);		
	}

}
