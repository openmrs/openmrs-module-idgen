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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.db.IdentifierSourceDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Base Implementation of the IdentifierSourceService API
 */
@Transactional
public class BaseIdentifierSourceService extends BaseOpenmrsService implements IdentifierSourceService {
	
	protected Log log = LogFactory.getLog(getClass());
	
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
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return dao.getIdentifierSource(id);
	}
	
	/** 
	 * @see IdentifierSourceService#getAllIdentifierSources(boolean)
	 */
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws APIException {
		return dao.getAllIdentifierSources(includeRetired);
	}

	/** 
	 * @see IdentifierSourceService#getIdentifierSourcesByType(boolean)
	 */
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
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		dao.purgeIdentifierSource(identifierSource);
	}
	
	/** 
	 * @see IdentifierSourceService#generateIdentifiers(IdentifierSource, Integer)
	 */
	public List<String> generateIdentifiers(IdentifierSource source, Integer batchSize) throws APIException {
		List<String> is = source.nextIdentifiers(batchSize);
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(source);
		return is;
	}

	/** 
	 * @see IdentifierSourceService#addIdentifiersToPool(IdentifierPool, List)
	 */
	public void addIdentifiersToPool(IdentifierPool pool, List<String> identifiers) throws APIException {
		for (String identifier : identifiers) {
			pool.addIdentifierToPool(identifier);
		}
		Context.getService(IdentifierSourceService.class).saveIdentifierSource(pool);
	}
	
	/** 
	 * @see IdentifierSourceService#addIdentifiersToPool(IdentifierPool, Integer)
	 */
	public void addIdentifiersToPool(IdentifierPool pool, Integer batchSize) throws APIException {
		List<String> identifiers = generateIdentifiers(pool.getSource(), batchSize);
		addIdentifiersToPool(pool, identifiers);
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
}
