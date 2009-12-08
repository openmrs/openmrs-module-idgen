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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.PooledIdentifier;
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
	@Transactional(readOnly=true)
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return (IdentifierSource) sessionFactory.getCurrentSession().get(IdentifierSource.class, id);
	}

	/** 
	 * @see IdentifierSourceDAO#getAllIdentifierSources(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IdentifierSource.class);
		if (!includeRetired) {
			criteria.add(Expression.like("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
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
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		sessionFactory.getCurrentSession().delete(identifierSource);
	}
	
	/**
	 * 
	 * @see IdentifierSourceDAO#getAvailableIdentifiers(IdentifierPool, boolean, boolean)
	 */
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PooledIdentifier.class);
		criteria.add(Expression.isNull("dateUsed"));
		criteria.add(Expression.eq("pool", pool));
		criteria.setMaxResults(quantity);
		if (pool.isSequential()) {
			criteria.addOrder(Order.asc("identifier"));
		}
		else {
			criteria.addOrder(Order.asc("uuid"));
		}
		List<PooledIdentifier> results = (List<PooledIdentifier>) criteria.list();
		if (results.size() < quantity) {
			throw new RuntimeException("Unable to retrieve " + quantity + " available identifiers from Pool " + pool);
		}
		return results;
	}
	
	/**
	 * @see IdentifierSourceDAO#getQuantityInPool(IdentifierPool, boolean, boolean)
	 */
	@Transactional(readOnly=true)
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) {
		String hql = "select count(*) from PooledIdentifier where pool_id = " + pool.getId();
		if (availableOnly) {
			hql += " and date_used is null";
		}
		if (usedOnly) {
			hql += " and date_used is not null";
		}
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		return Integer.parseInt(query.uniqueResult().toString());
	}
	
	/** 
	 * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
	 */
	@Transactional(readOnly=true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws APIException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
		criteria.add(Expression.eq("identifierType", type));
		return (AutoGenerationOption)criteria.uniqueResult();
	}

	/** 
	 * @see IdentifierSourceDAO#saveAutoGenerationOption(AutoGenerationOption)
	 */
	@Transactional
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws APIException {
		sessionFactory.getCurrentSession().saveOrUpdate(option);
		return option;
	}

	/** 
	 * @see IdentifierSourceDAO#purgeAutoGenerationOption(AutoGenerationOption)
	 */
	@Transactional
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws APIException {
		sessionFactory.getCurrentSession().delete(option);
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
