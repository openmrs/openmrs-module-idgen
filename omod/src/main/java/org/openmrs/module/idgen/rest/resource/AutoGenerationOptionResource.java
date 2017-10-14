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
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
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

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE
        + "/autogenerationoption", supportedClass = AutoGenerationOption.class, supportedOpenmrsVersions = { "1.9.*",
                "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" })
public class AutoGenerationOptionResource extends DelegatingCrudResource<AutoGenerationOption> {
	
	IdentifierSourceService identifierSourceService = Context.getService(IdentifierSourceService.class);
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = null;
		if (rep instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
		} else if (rep instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("identifierType");
			description.addProperty("location");
			description.addProperty("source");
			description.addProperty("manualEntryEnabled");
			description.addProperty("automaticGenerationEnabled");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		} else if (rep instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("identifierType");
			description.addProperty("location");
			description.addProperty("source");
			description.addProperty("manualEntryEnabled");
			description.addProperty("automaticGenerationEnabled");
			description.addSelfLink();
		}
		return description;
	}
	
	@PropertyGetter("location")
	public String getLocation(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getLocation().getUuid();
	}
	
	@PropertyGetter("identifierType")
	public String getIdentifierType(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getIdentifierType().getUuid();
	}
	
	@PropertyGetter("source")
	public String getSource(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getSource().getUuid();
	}
	
	@PropertyGetter("uuid")
	public String getUuid(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getUuid();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(AutoGenerationOption autoGenerationOption) {
		return autoGenerationOption.getIdentifierType() + " - " + autoGenerationOption.getSource() + " - "
		        + autoGenerationOption.getLocation();
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
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
	public SimpleObject create(SimpleObject postBody, RequestContext context) throws ResponseException {
		String manualEntryEnabled = ((postBody.get("manualEntryEnabled") != null)
		        && (!postBody.get("manualEntryEnabled").equals(""))) ? postBody.get("manualEntryEnabled").toString()
		                : "false";
		String automaticGenerationEnabled = ((postBody.get("automaticGenerationEnabled") != null)
		        && (!postBody.get("automaticGenerationEnabled").equals("")))
		                ? postBody.get("automaticGenerationEnabled").toString()
		                : "false";
		String sourceUuid = ((postBody.get("source") != null) && (!postBody.get("source").equals("")))
		        ? postBody.get("source").toString()
		        : null;
		String identifierUuid = ((postBody.get("identifierType") != null) && (!postBody.get("identifierType").equals("")))
		        ? postBody.get("identifierType").toString()
		        : null;
		String locationUuid = ((postBody.get("location") != null) && (!postBody.get("location").equals("")))
		        ? postBody.get("location").toString()
		        : Context.getLocationService().getDefaultLocation().getUuid();
		AutoGenerationOption autoGenerationOption = new AutoGenerationOption();
		IdentifierSource identifierSource = (identifierUuid != null)
		        ? identifierSourceService.getIdentifierSourceByUuid(sourceUuid)
		        : null;
		if (identifierSource == null) {
			throw new ResourceDoesNotSupportOperationException(
			        "Creating an autoGenerationOption with an invalid identifierSource is not supported");
		}
		PatientIdentifierType patientIdentifierType = (identifierUuid != null)
		        ? Context.getPatientService().getPatientIdentifierTypeByUuid(identifierUuid)
		        : null;
		if (patientIdentifierType == null) {
			throw new ResourceDoesNotSupportOperationException(
			        "Creating an autoGenerationOption with an invalid patientIdentifierType  is not supported");
		}
		Location location = Context.getLocationService().getLocationByUuid(locationUuid);
		if (location == null) {
			throw new ResourceDoesNotSupportOperationException(
			        "Creating an autoGenerationOption with an invalid location is not supported");
		}
		autoGenerationOption.setSource(identifierSource);
		autoGenerationOption.setIdentifierType(patientIdentifierType);
		autoGenerationOption.setManualEntryEnabled(Boolean.parseBoolean(manualEntryEnabled));
		autoGenerationOption.setAutomaticGenerationEnabled(Boolean.parseBoolean(automaticGenerationEnabled));
		autoGenerationOption.setLocation(location);
		return (SimpleObject) ConversionUtil.convertToRepresentation(save(autoGenerationOption), Representation.FULL);
		
	}
	
	@Override
	public SimpleObject update(String uuid, SimpleObject postBody, RequestContext context) throws ResponseException {
		String manualEntryEnabled = ((postBody.get("manualEntryEnabled") != null)
		        && !(postBody.get("manualEntryEnabled").equals(""))) ? postBody.get("manualEntryEnabled").toString() : null;
		String automaticGenerationEnabled = ((postBody.get("automaticGenerationEnabled") != null)
		        && !(postBody.get("automaticGenerationEnabled").equals("")))
		                ? postBody.get("automaticGenerationEnabled").toString()
		                : null;
		String sourceUuid = ((postBody.get("source") != null) && !(postBody.get("source").equals("")))
		        ? postBody.get("source").toString()
		        : null;
		String locationUuid = ((postBody.get("location") != null) && !(postBody.get("location").equals("")))
		        ? postBody.get("location").toString()
		        : null;
		AutoGenerationOption autoGenerationOption = Context.getService(IdentifierSourceService.class)
		        .getAutoGenerationOption(Integer.parseInt(uuid));
		if (manualEntryEnabled != null) {
			autoGenerationOption.setManualEntryEnabled(Boolean.parseBoolean(manualEntryEnabled));
		}
		if (automaticGenerationEnabled != null) {
			autoGenerationOption.setAutomaticGenerationEnabled(Boolean.parseBoolean(automaticGenerationEnabled));
		}
		if (sourceUuid != null) {
			IdentifierSource identifierSource = identifierSourceService.getIdentifierSourceByUuid(sourceUuid);
			autoGenerationOption.setSource(identifierSource);
		}
		if (locationUuid != null) {
			Location location = Context.getLocationService().getLocationByUuid(locationUuid);
			autoGenerationOption.setLocation(location);
		}
		if (locationUuid == null && sourceUuid == null && automaticGenerationEnabled == null && manualEntryEnabled == null) {
			throw new ResourceDoesNotSupportOperationException(
			        "updating an autoGenerationOption with missing parameters is not supported");
		}
		return (SimpleObject) ConversionUtil.convertToRepresentation(save(autoGenerationOption), Representation.REF);
	}
	
	@Override
	public AutoGenerationOption newDelegate() {
		return new AutoGenerationOption();
	}
	
	@Override
	public AutoGenerationOption save(AutoGenerationOption autoGenerationOption) {
		return identifierSourceService.saveAutoGenerationOption(autoGenerationOption);
	}
	
	@Override
	public AutoGenerationOption getByUniqueId(String uniqueId) {
		return identifierSourceService.getAutoGenerationOption(Integer.parseInt(uniqueId));
	}
	
	@Override
	public void purge(AutoGenerationOption delegate, RequestContext context) throws ResponseException {
		identifierSourceService.purgeAutoGenerationOption(delegate);
	}
	
	@Override
	public void delete(AutoGenerationOption delegate, String id, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Deleting of AutoGenerationOption is not supported");
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		List<AutoGenerationOption> autoGenerationOptionList = new ArrayList<AutoGenerationOption>();
		for (PatientIdentifierType patientIdentifierType : Context.getPatientService().getAllPatientIdentifierTypes()) {
			List<AutoGenerationOption> autoGenerationOptions = identifierSourceService
			        .getAutoGenerationOptions(patientIdentifierType);
			if (autoGenerationOptions != null && autoGenerationOptions.size() > 0) {
				autoGenerationOptionList.addAll(autoGenerationOptions);
			}
		}
		return new NeedsPaging<AutoGenerationOption>(autoGenerationOptionList, context);
	}
}
