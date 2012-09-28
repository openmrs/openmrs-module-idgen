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

import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.idgen.RemoteIdentifierSource;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * We don't guarantee that this endpoint will always be up
 */
@Ignore
public class RemoteIdentifierSourceProcessorTest extends TestCase {

    @Test
    public void test() {
        RemoteIdentifierSourceProcessor processor = new RemoteIdentifierSourceProcessor();
        RemoteIdentifierSource source = new RemoteIdentifierSource();
        source.setUrl("http://bamboo.pih-emr.org:8080/mirebalais/module/idgen/exportIdentifiers.form?source=3&comment=Testing+Mirebalais");
        source.setUser("testidgen");
        source.setPassword("Testing123");

        List<String> identifiers = processor.getIdentifiers(source, 1);
        assertThat(identifiers.size(), is(1));
        assertThat(identifiers.get(0).length(), is(6));
    }

}
