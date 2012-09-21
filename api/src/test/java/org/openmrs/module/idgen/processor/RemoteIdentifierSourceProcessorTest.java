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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.idgen.RemoteIdentifierSource;

public class RemoteIdentifierSourceProcessorTest {
    @Test
    public void testGetIdentifiers() throws Exception {
        RemoteIdentifierSourceProcessor remoteIdentifierSourceProcessor = new RemoteIdentifierSourceProcessorStub();

        String url = "http://urlToTestSource/openmrs/idgen?batchSize={batchSize}";

        RemoteIdentifierSource remoteSource = new RemoteIdentifierSource();
        remoteSource.setUrl(url);

        List<String> idList = remoteIdentifierSourceProcessor.getIdentifiers(remoteSource, 5);

        Assert.assertEquals("1",idList.get(0));
        Assert.assertEquals("2",idList.get(1));
        Assert.assertEquals("3",idList.get(2));
        Assert.assertEquals("4",idList.get(3));
        Assert.assertEquals("5",idList.get(4));

    }

    private class RemoteIdentifierSourceProcessorStub extends RemoteIdentifierSourceProcessor{

        @Override
        protected InputStream getInputStreamFrom(String url) throws IOException {
            String ids = "1 \n 2 \n 3 \n 4 \n 5";
            return new ByteArrayInputStream(ids.getBytes());
        }
    }
}
