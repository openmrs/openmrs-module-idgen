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
package org.openmrs.module.idgen;

import java.util.Set;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.PatientIdentifierType;

/**
 * An IdentifierSource is a construct which can supply identifiers of a particular type
 */
public abstract class IdentifierSource implements OpenmrsMetadata {
    
	/**
	 * @return the PatientIdentifierType that this source supplies
	 */
	public abstract PatientIdentifierType getIdentifierType();
	
	/**
	 * @param the PatientIdentifierType that this source supplies
	 */
	public abstract void setIdentifierType(PatientIdentifierType type);

	/**
	 * @return Set of reserved identifiers
	 */
	public abstract Set<String> getReservedIdentifiers();
	
	/**
	 * @param - the reserved identifiers to set
	 */
	public abstract void setReservedIdentifiers(Set<String> reservedIdentifiers);
	
	/**
	 * @param - the reserved identifier to add
	 */
	public abstract void addReservedIdentifier(String reservedIdentifier);
}	
