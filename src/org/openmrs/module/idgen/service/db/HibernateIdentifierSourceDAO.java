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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openmrs.api.APIException;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Hibernate Implementation of the IdentifierSourceDAO Interface
 */
@Transactional
public class HibernateIdentifierSourceDAO implements IdentifierSourceDAO {
	
	protected Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	private SessionFactory sessionFactory;
	
	//***** INSTANCE METHODS *****

	/** 
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return (IdentifierSource) sessionFactory.getCurrentSession().get(IdentifierSource.class, id);
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException {		
		sessionFactory.getCurrentSession().saveOrUpdate(identifierSource);
		return identifierSource;
	}

	/** 
	 * @see IdentifierSourceService#purgeIdentifierSource(IdentifierSource)
	 */
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		sessionFactory.getCurrentSession().delete(identifierSource);
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
