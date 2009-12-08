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
package org.openmrs.module.idgen.advice;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleException;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.aop.Advisor;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

/**
 * Advises the PatientService to auto-generate an ID if necessary
 */
public class PatientServiceIdGenAdvisor extends StaticMethodMatcherPointcutAdvisor implements Advisor {

	private static final long serialVersionUID = 3333L;

	/**
	 * @see MethodMatcher#matches(Method, Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean matches(Method method, Class targetClass) {
		return method.getName().equals("savePatient");
	}

	/**
	 * @see StaticMethodMatcherPointcutAdvisor#getAdvice()
	 */
	public Advice getAdvice() {
		return new MethodInterceptor() {
			public Object invoke(MethodInvocation invocation) throws Throwable {
						
				Object[] obj = invocation.getArguments();
				Patient patient = (Patient)obj[0];

				// This seems to be necessary due to various hibernate oddities
				Set<PatientIdentifier> identifiers = new HashSet<PatientIdentifier>();
				for (Iterator<PatientIdentifier> iter = patient.getIdentifiers().iterator(); iter.hasNext();) {
					PatientIdentifier pi = iter.next();
					iter.remove();
					Context.evictFromSession(pi);
					identifiers.add(pi);
				}

				IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
				
				for (PatientIdentifier pi : identifiers) {
					
					AutoGenerationOption option = iss.getAutoGenerationOption(pi.getIdentifierType());
					
					if (pi.getIdentifier().contains("TEMPID_WILL_BE_REPLACED")) {
						if (option != null && option.isAutomaticGenerationEnabled()) {
							String generatedIdentifier = iss.generateIdentifier(option.getSource());
							pi.setIdentifier(generatedIdentifier);
						}
						else {
							String msg = pi.getIdentifierType().getName() + " does not support auto-generation.";
							throw new PatientIdentifierException(msg);
						}
					}
					else {
						if (StringUtils.isNotBlank(pi.getIdentifier())) {
							if (option != null && !option.isManualEntryEnabled()) {
								String msg = pi.getIdentifierType().getName() + " does not support manual entry.";
								throw new PatientIdentifierException(msg); 
							}
						}
					}
					patient.addIdentifier(pi);
				}
				
				try {
					Object o = invocation.proceed();
					return o;
				}
				catch (Exception e) {
					throw new ModuleException(e.getMessage(), e);				
				}
			}
		};
	}
}