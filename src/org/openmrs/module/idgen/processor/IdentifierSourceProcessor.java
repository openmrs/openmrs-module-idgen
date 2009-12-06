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
package org.openmrs.module.idgen.processor;

import java.util.List;

import org.openmrs.module.idgen.IdentifierSource;

/**
 * The interface provides the functionality for using an IdentifierSource
 */
public interface IdentifierSourceProcessor {
    
    /**
     * Return List of new identifiers from this source
     * @param batchSize the number of new identifiers to return
     * @return a list of new identifiers from this source
     */
    public List<String> getIdentifiers(IdentifierSource source, int batchSize);
}
