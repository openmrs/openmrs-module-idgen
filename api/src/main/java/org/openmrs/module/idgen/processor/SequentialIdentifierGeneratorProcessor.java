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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenUtil;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;

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
    		sequenceValue = resetToFirstSequenceValue(seq);
    	}
    	return generateBatch(seq, sequenceValue, batchSize);
	}

	/**
	 * This sets the next sequence value to 1 or whatever number represents the first identifier base configured
	 * and saves this to the database
	 * @return the next sequence value, after it is reset
	 */
	protected Long resetToFirstSequenceValue(SequentialIdentifierGenerator seq) {
		Long sequenceValue = 1L;
		if (seq.getFirstIdentifierBase() != null) {
			sequenceValue = IdgenUtil.convertFromBase(seq.getFirstIdentifierBase(), seq.getBaseCharacterSet().toCharArray());
		}
		identifierSourceService.saveSequenceValue(seq, sequenceValue);
		return sequenceValue;
	}

	/**
	 * @return a batch of identifiers of the given batchSize, starting with the given sequenceValue
	 */
	protected List<String> generateBatch(SequentialIdentifierGenerator seq, Long sequenceValue, int batchSize) {
		List<String> identifiers = new ArrayList<String>();
		Set<String> reservedIdentifiers = seq.getReservedIdentifiers();
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
