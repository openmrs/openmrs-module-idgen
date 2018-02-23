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
package org.openmrs.module.idgen.mapper;

import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.contract.IdentifierSource;

import java.util.ArrayList;
import java.util.List;

public class IdentifierSourceListMapper {
    public static List<IdentifierSource> map(List<org.openmrs.module.idgen.IdentifierSource> identifierSourcesList) {
        List<IdentifierSource> result = new ArrayList<IdentifierSource>();
        for (org.openmrs.module.idgen.IdentifierSource identifierSource : identifierSourcesList) {
            String prefix = null;
            if (identifierSource instanceof SequentialIdentifierGenerator) {
                prefix = ((SequentialIdentifierGenerator) (identifierSource)).getPrefix();
            }
            result.add(new IdentifierSource(identifierSource.getUuid(), identifierSource.getName(), prefix));
        }
        return result;
    }
}
