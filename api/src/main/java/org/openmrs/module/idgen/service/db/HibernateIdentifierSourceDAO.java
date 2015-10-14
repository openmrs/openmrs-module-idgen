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
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.EmptyIdentifierPoolException;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  Hibernate Implementation of the IdentifierSourceDAO Interface
 */
@Transactional
public class HibernateIdentifierSourceDAO implements IdentifierSourceDAO {
	
	protected Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	private DbSessionFactory sessionFactory;
	
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
		DbSession currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(identifierSource);
		currentSession.flush();
		refreshIdentifierSource(identifierSource);
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
	 * @see IdentifierSourceDAO#getAvailableIdentifiers(IdentifierPool, int)
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
			throw new EmptyIdentifierPoolException("Unable to retrieve " + quantity + " available identifiers from Pool " + pool + ".  Maybe you need to add more identifiers to your pool first.");
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
     * @see IdentifierSourceDAO#getAutoGenerationOption(Integer)
     */
    @Transactional(readOnly=true)
    @Override
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq("id", autoGenerationOptionId));
        return (AutoGenerationOption)criteria.uniqueResult();
    }

    /**
	 * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType,Location)
	 */
	@Transactional(readOnly=true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws APIException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
		criteria.add(Expression.eq("identifierType", type));
        criteria.add(Restrictions.or(Expression.eq("location", location), Expression.isNull("location")));
		return (AutoGenerationOption) criteria.uniqueResult();
	}

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
     */
    @Transactional(readOnly=true)
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws APIException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AutoGenerationOption.class);
        criteria.add(Expression.eq("identifierType", type));
        return (List<AutoGenerationOption>) criteria.list();
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

	/** 
	 * @see IdentifierSourceDAO#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	@SuppressWarnings("unchecked")
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LogEntry.class);
		if (source != null) {
			criteria.add(Expression.eq("source", source));
		}
		if (fromDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			criteria.add(Expression.ge("dateGenerated", fromDate));
		}
		if (toDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(toDate);
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			criteria.add(Expression.lt("dateGenerated", c.getTime()));
		}
		if (identifier != null) {
			criteria.add(Expression.like("identifier", identifier, MatchMode.ANYWHERE));
		}	
		if (generatedBy != null) {
			criteria.add(Expression.eq("generatedBy", generatedBy));
		}
		if (comment != null) {
			criteria.add(Expression.like("comment", comment, MatchMode.ANYWHERE));
		}	
		criteria.addOrder(Order.desc("dateGenerated"));
		return (List<LogEntry>) criteria.list();
	}

    /**
     * @see IdentifierSourceDAO#getIdentifierSourceByUuid(String)
     */
    @Override
    public IdentifierSource getIdentifierSourceByUuid(String uuid) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(IdentifierSource.class);
        criteria.add(Restrictions.eq("uuid", uuid));
        return (IdentifierSource) criteria.uniqueResult();
    }

    /**
	 * @see org.openmrs.module.idgen.service.db.IdentifierSourceDAO#saveLogEntry(LogEntry)
	 */
	public LogEntry saveLogEntry(LogEntry logEntry) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(logEntry);
		return logEntry;
	}

    /**
     * @see IdentifierSourceDAO#saveSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator, long)
     */
    @Override
    public void saveSequenceValue(SequentialIdentifierGenerator generator, long sequenceValue) {
        int updated = sessionFactory.getCurrentSession()
                .createSQLQuery("update idgen_seq_id_gen set next_sequence_value = :val where id = :id")
                .setParameter("val", sequenceValue)
                .setParameter("id", generator.getId())
                .executeUpdate();
        if (updated != 1) {
            throw new APIException("Expected to update 1 row but updated " + updated + " rows instead!");
        }
    }

    /**
     * @see IdentifierSourceDAO#getSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator)
     */
    @Override
    public Long getSequenceValue(SequentialIdentifierGenerator generator) {
        Number val = (Number) sessionFactory.getCurrentSession()
                .createSQLQuery("select next_sequence_value from idgen_seq_id_gen where id = :id")
                .setParameter("id", generator.getId())
                .uniqueResult();
        return val == null ? null : val.longValue();
	}


    public void refreshIdentifierSource(IdentifierSource source) {
        sessionFactory.getCurrentSession().refresh(source);
    }


	//***** PROPERTY ACCESS *****

	/**
	 * @return the sessionFactory
	 */
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
