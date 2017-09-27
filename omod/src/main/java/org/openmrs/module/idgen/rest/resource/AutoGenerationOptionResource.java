/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.api.context.Context;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE
        + "/autogenerationoption", supportedClass = AutoGenerationOption.class, supportedOpenmrsVersions = { "1.9.*",
                "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" })
public class AutoGenerationOptionResource extends DelegatingCrudResource<AutoGenerationOption> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			
		}
		
		// NOTE: description for ref representation is provided by MetaDataDelegatingCrudResource
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("identifierType");
			description.addProperty("source");
			description.addProperty("manualEntryEnabled");
			description.addProperty("automaticGenerationEnabled");
			description.addSelfLink();
			if (rep instanceof DefaultRepresentation) {
				description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			}
		}
		
		return description;
	}
	
	@PropertyGetter("uuid")
	public String getUuid(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getSource().getUuid();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getIdentifierType()+ " - "+ autoGenerationOption.getSource();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("identifierType");
		description.addProperty("location");
		description.addProperty("source");
		description.addProperty("manualEntryEnabled");
		description.addProperty("automaticGenerationEnabled");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("location");
		description.addProperty("source");
		description.addProperty("manualEntryEnabled");
		description.addProperty("automaticGenerationEnabled");
		return description;
	}
	
	@Override
	public AutoGenerationOption newDelegate() {
		return new AutoGenerationOption();
	}
	
	@Override
	public AutoGenerationOption save(AutoGenerationOption autoGenerationOption) {
		return Context.getService(IdentifierSourceService.class).saveAutoGenerationOption(autoGenerationOption);
	}
	
	@Override
	public AutoGenerationOption getByUniqueId(String uniqueId) {
		return Context.getService(IdentifierSourceService.class).getAutoGenerationOption(Integer.parseInt(uniqueId));
	}
	
	@Override
	public void purge(AutoGenerationOption delegate, RequestContext context) throws ResponseException {
		Context.getService(IdentifierSourceService.class).purgeAutoGenerationOption(delegate);
	}
	
	@Override
	public void delete(AutoGenerationOption delegate, String id, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Deleting of AutoGenerationOption is not supported");
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
		List<AutoGenerationOption> optionMap = new ArrayList<AutoGenerationOption>();
		List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
		Map<PatientIdentifierType, List<IdentifierSource>> availableSources = identifierSourceService.getIdentifierSourcesByType(false);
		
		for (PatientIdentifierType patientIdentifierType : Context.getPatientService().getAllPatientIdentifierTypes()) {
			List<AutoGenerationOption> options = identifierSourceService.getAutoGenerationOptions(patientIdentifierType);
			
			if (options != null && options.size() > 0) {
				optionMap.addAll(options);
			}
			
			if (availableSources.get(patientIdentifierType) != null
			        && availableSources.get(patientIdentifierType).size() > 0) {
				identifierTypes.add(patientIdentifierType);
			}
		}
		return new NeedsPaging<AutoGenerationOption>(optionMap, context);
	}
}