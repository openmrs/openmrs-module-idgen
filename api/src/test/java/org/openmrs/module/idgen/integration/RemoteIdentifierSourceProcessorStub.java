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
package org.openmrs.module.idgen.integration;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifiersMessage;
import org.openmrs.module.idgen.processor.RemoteIdentifierSourceProcessor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RemoteIdentifierSourceProcessorStub extends RemoteIdentifierSourceProcessor {

	Stack<String> identifiers;
	int timesCalled = 0;
	private Integer batchSize;

	public RemoteIdentifierSourceProcessorStub() {
		identifiers = new Stack<String>();
		for (int i = 10; i > 0; --i) {
			identifiers.add("" + i);
		}
	}

	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	protected String doHttpPost(RemoteIdentifierSource source, int batchSize) throws IOException {
		timesCalled++;

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < batchSize; i++) {
			list.add(identifiers.pop());
		}
		StringWriter writer = new StringWriter();
		new ObjectMapper().writeValue(writer, new RemoteIdentifiersMessage(list));
		return writer.toString();
	}

	public int getTimesCalled() {
		return timesCalled;
	}
}
