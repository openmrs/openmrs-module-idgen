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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.EmptyIdentifierPoolException;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  Hibernate Implementation of the IdentifierSourceDAO Interface
 */
public class HibernateIdentifierSourceDAO implements IdentifierSourceDAO {
	
	protected Log log = LogFactory.getLog(getClass());
	
	//***** PROPERTIES *****
	
	private DbSessionFactory sessionFactory;
	
	//***** INSTANCE METHODS *****

	private Session getCurrentSession() {
		return sessionFactory.getHibernateSessionFactory().getCurrentSession();
	}

	/** 
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	public IdentifierSource getIdentifierSource(Integer id) throws APIException {
		return getCurrentSession().get(IdentifierSource.class, id);
	}

	/** 
	 * @see IdentifierSourceDAO#getAllIdentifierSources(boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException {
		Session session = getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<IdentifierSource> cq = cb.createQuery(IdentifierSource.class);
		Root<IdentifierSource> root = cq.from(IdentifierSource.class);
		if (!includeRetired) {
			cq.where(cb.equal(root.get("retired"), false));
		}
		cq.orderBy(cb.asc(root.get("name")));
		return session.createQuery(cq).list();
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
	 */
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException {
		Session currentSession = getCurrentSession();
		identifierSource = HibernateUtil.saveOrUpdate(currentSession, identifierSource);
		currentSession.flush();
		refreshIdentifierSource(identifierSource);
		return identifierSource;
	}

	/** 
	 * @see IdentifierSourceService#purgeIdentifierSource(IdentifierSource)
	 */
	public void purgeIdentifierSource(IdentifierSource identifierSource) {
		getCurrentSession().remove(identifierSource);
	}
	
	/**
	 * 
	 * @see IdentifierSourceDAO#getAvailableIdentifiers(IdentifierPool, int)
	 */
	@SuppressWarnings("unchecked")
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) {
		Session session = getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<PooledIdentifier> cq = cb.createQuery(PooledIdentifier.class);
		Root<PooledIdentifier> root = cq.from(PooledIdentifier.class);
		cq.where(cb.isNull(root.get("dateUsed")), cb.equal(root.get("pool"), pool));
		if (pool.isSequential()) {
			cq.orderBy(cb.asc(root.get("identifier")));
		}
		else {
			cq.orderBy(cb.asc(root.get("uuid")));
		}
		List<PooledIdentifier> results = session.createQuery(cq).setMaxResults(quantity).list();
		if (results.size() < quantity) {
			throw new EmptyIdentifierPoolException("Unable to retrieve " + quantity + " available identifiers from Pool " + pool + ".  Maybe you need to add more identifiers to your pool first.");
		}
		return results;
	}
	
	/**
	 * @see IdentifierSourceDAO#getQuantityInPool(IdentifierPool, boolean, boolean)
	 */
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) {
		String hql = "select count(*) from PooledIdentifier where pool.id = :poolId";
		if (availableOnly) {
			hql += " and dateUsed is null";
		}
		if (usedOnly) {
			hql += " and dateUsed is not null";
		}
		Long count = getCurrentSession().createQuery(hql, Long.class).setParameter("poolId", pool.getId()).uniqueResult();
		return count.intValue();
	}

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(Integer)
     */
    @Override
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws DAOException {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AutoGenerationOption> cq = cb.createQuery(AutoGenerationOption.class);
        Root<AutoGenerationOption> root = cq.from(AutoGenerationOption.class);
        cq.where(cb.equal(root.get("id"), autoGenerationOptionId));
        return session.createQuery(cq).uniqueResult();
    }

    /**
	 * @see IdentifierSourceService#getAutoGenerationOptionByUuid(String)
	 */
    @Override
	public AutoGenerationOption getAutoGenerationOptionByUuid(String uuid) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AutoGenerationOption> cq = cb.createQuery(AutoGenerationOption.class);
        Root<AutoGenerationOption> root = cq.from(AutoGenerationOption.class);
        cq.where(cb.equal(root.get("uuid"), uuid));
        return session.createQuery(cq).uniqueResult();
	}
    
    /**
	 * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType,Location)
	 */
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws APIException {
		Session session = getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<AutoGenerationOption> cq = cb.createQuery(AutoGenerationOption.class);
		Root<AutoGenerationOption> root = cq.from(AutoGenerationOption.class);
		cq.where(cb.equal(root.get("identifierType"), type),
		        cb.or(cb.equal(root.get("location"), location), cb.isNull(root.get("location"))));
		return session.createQuery(cq).uniqueResult();
	}

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
     */
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws APIException {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AutoGenerationOption> cq = cb.createQuery(AutoGenerationOption.class);
        Root<AutoGenerationOption> root = cq.from(AutoGenerationOption.class);
        cq.where(cb.equal(root.get("identifierType"), type));
        return session.createQuery(cq).list();
    }

    /**
     * @see IdentifierSourceDAO#getAutoGenerationOption(PatientIdentifierType)
     */
    public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws APIException {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<AutoGenerationOption> cq = cb.createQuery(AutoGenerationOption.class);
        Root<AutoGenerationOption> root = cq.from(AutoGenerationOption.class);
        cq.where(cb.equal(root.get("identifierType"), type));
        return session.createQuery(cq).uniqueResult();
    }

	/** 
	 * @see IdentifierSourceDAO#saveAutoGenerationOption(AutoGenerationOption)
	 */
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws APIException {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), option);
	}

	/** 
	 * @see IdentifierSourceDAO#purgeAutoGenerationOption(AutoGenerationOption)
	 */
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws APIException {
		getCurrentSession().remove(option);
	}

	/** 
	 * @see IdentifierSourceDAO#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	@SuppressWarnings("unchecked")
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws DAOException {
		Session session = getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<LogEntry> cq = cb.createQuery(LogEntry.class);
		Root<LogEntry> root = cq.from(LogEntry.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (source != null) {
			predicates.add(cb.equal(root.get("source"), source));
		}
		if (fromDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(fromDate);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			predicates.add(cb.greaterThanOrEqualTo(root.<Date>get("dateGenerated"), fromDate));
		}
		if (toDate != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(toDate);
			c.add(Calendar.DATE, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			predicates.add(cb.lessThan(root.<Date>get("dateGenerated"), c.getTime()));
		}
		if (identifier != null) {
			predicates.add(cb.like(root.<String>get("identifier"), "%" + identifier + "%"));
		}	
		if (generatedBy != null) {
			predicates.add(cb.equal(root.get("generatedBy"), generatedBy));
		}
		if (comment != null) {
			predicates.add(cb.like(root.<String>get("comment"), "%" + comment + "%"));
		}	
		cq.where(predicates.toArray(new Predicate[0]));
		cq.orderBy(cb.desc(root.get("dateGenerated")));
		return session.createQuery(cq).list();
	}

	/**
	 * @see IdentifierSourceDAO#getLogEntries(IdentifierSource, Date, Date, String, User, String)
	 */
	@SuppressWarnings("unchecked")
	public LogEntry getMostRecentLogEntry(IdentifierSource source) throws DAOException {
		if (source == null) {
			throw new DAOException("You must specify the Identifier Source that you wish to query");
		}
		Session session = getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<LogEntry> cq = cb.createQuery(LogEntry.class);
		Root<LogEntry> root = cq.from(LogEntry.class);
		cq.where(cb.equal(root.get("source"), source));
		cq.orderBy(cb.desc(root.get("dateGenerated")), cb.desc(root.get("id")));
		return session.createQuery(cq).setMaxResults(1).uniqueResult();
	}

    /**
     * @see IdentifierSourceDAO#getIdentifierSourceByUuid(String)
     */
    @Override
    public IdentifierSource getIdentifierSourceByUuid(String uuid) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<IdentifierSource> cq = cb.createQuery(IdentifierSource.class);
        Root<IdentifierSource> root = cq.from(IdentifierSource.class);
        cq.where(cb.equal(root.get("uuid"), uuid));
        return session.createQuery(cq).uniqueResult();
    }
    
    /**
     * @see IdentifierSourceDAO#getIdentifierSourcesByType(PatientIdentifierType)
     */
    @Override
    public List<IdentifierSource> getIdentifierSourcesByType(PatientIdentifierType patientIdentifierType) {
        Session session = getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<IdentifierSource> cq = cb.createQuery(IdentifierSource.class);
        Root<IdentifierSource> root = cq.from(IdentifierSource.class);
        cq.where(cb.equal(root.get("identifierType"), patientIdentifierType), cb.equal(root.get("retired"), false));
        return session.createQuery(cq).list();
    }    

    /**
	 * @see org.openmrs.module.idgen.service.db.IdentifierSourceDAO#saveLogEntry(LogEntry)
	 */
	public LogEntry saveLogEntry(LogEntry logEntry) throws DAOException {
		return HibernateUtil.saveOrUpdate(getCurrentSession(), logEntry);
	}

    /**
     * @see IdentifierSourceDAO#saveSequenceValue(org.openmrs.module.idgen.SequentialIdentifierGenerator, long)
     */
    @Override
    public void saveSequenceValue(SequentialIdentifierGenerator generator, long sequenceValue) {
        int updated = getCurrentSession()
                .createNativeMutationQuery("update idgen_seq_id_gen set next_sequence_value = :val where id = :id")
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
        Number val = (Number) getCurrentSession()
                .createNativeQuery("select next_sequence_value from idgen_seq_id_gen where id = :id", Object.class)
		        // Added IntegerType.INSTANCE because hibernate in case of PostgreSQL converts null ids to 
		        // bytea type and causes error. So had to add explicit type.
		        .setParameter("id", generator.getId(), StandardBasicTypes.INTEGER)
                .uniqueResult();
        return val == null ? null : val.longValue();
	}


    public void refreshIdentifierSource(IdentifierSource source) {
		// Hibernate 7 refuses to refresh a detached instance, and callers may pass a source loaded in an earlier session
		Session session = getCurrentSession();
		if (session.contains(source)) {
			session.refresh(source);
		}
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
