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

import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Evaluates a SequentialIdentifierSource
 */
public class SequentialIdentifierGeneratorProcessor implements IdentifierSourceProcessor {

    private IdentifierSourceService identifierSourceService;

    /**
     * @param identifierSourceService
     */
    public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
        this.identifierSourceService = identifierSourceService;
    }

    /**
	 * @see IdentifierSourceProcessor#getIdentifiers(IdentifierSource, int)
	 */
	public synchronized List<String> getIdentifiers(IdentifierSource source, int batchSize) {
		
		SequentialIdentifierGenerator seq = (SequentialIdentifierGenerator) source;
        Long sequenceValue = identifierSourceService.getSequenceValue(seq);
    	if (sequenceValue == null || sequenceValue < 0) {
    		if (seq.getFirstIdentifierBase() != null) {
    			sequenceValue = IdgenUtil.convertFromBase(seq.getFirstIdentifierBase(), seq.getBaseCharacterSet().toCharArray());
    		}
    		else {
    			sequenceValue = 1L;
    		}
    	}

    	Set<String> reservedIdentifiers = source.getReservedIdentifiers();
    	
    	List<String> identifiers = new ArrayList<String>();
    	
    	for (int i=0; i<batchSize;) {
    		String val = seq.getIdentifierForSeed(sequenceValue);
    		if (!reservedIdentifiers.contains(val)) {
    			identifiers.add(val);
    			i++;
    		}
	    	sequenceValue++;
    	}

        identifierSourceService.saveSequenceValue(seq, sequenceValue);

    	return identifiers;
	}
}