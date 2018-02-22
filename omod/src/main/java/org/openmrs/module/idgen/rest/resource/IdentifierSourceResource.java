/* * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.idgen.rest.resource;

import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.web.controller.IdgenRestController;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + IdgenRestController.IDGEN_NAMESPACE + "/identifiersource", supportedClass = IdentifierSource.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*" , "2.1.*" })
public class IdentifierSourceResource extends DelegatingCrudResource<IdentifierSource>{
	
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {

        DelegatingResourceDescription description = null;

        if (rep instanceof RefRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addSelfLink();
            return description;
        }
        else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
            description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("name");
            description.addProperty("description");
            description.addProperty("identifierType");
            description.addSelfLink();
            if (rep instanceof DefaultRepresentation) {
                description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            }
        }
        return description;
    }
	
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        description.addProperty("description");
        description.addProperty("identifierType");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("name");
        description.addProperty("description");
        return description;
    }
	
    @PropertyGetter("display")
    public String getDisplayString(IdentifierSource identifierSource) {
        return identifierSource.getIdentifierType() + " - " 
                + identifierSource.getName() + " - "
                + identifierSource.getClass().getName();
    }
    
    @PropertyGetter("identifiers")
    public String getIdentifiers(IdentifierSource identifierSource) {
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getIdentifiers().toString();
        }
        return  null;
    }
    
    @PropertyGetter("usedIdentifiers")
    public Integer getUsedIdentifiers(IdentifierSource identifierSource) {
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getUsedIdentifiers().size();
        }
        return  null;
    }
    
    @PropertyGetter("availableIdentifiers")
    public Integer getAvailableIdentifiers(IdentifierSource identifierSource) {
        if(identifierSource instanceof IdentifierPool){
            IdentifierPool pool = (IdentifierPool) identifierSource;
            return pool.getAvailableIdentifiers().size();
        }
        return  null;
    }

    @Override
    public IdentifierSource newDelegate() {
        return new SequentialIdentifierGenerator();
    }
	
    @Override
    public Object create(SimpleObject postBody, RequestContext context) throws ResponseException {
		
        final String Sequential_Identifier_Generator = "SequentialIdentifierGenerator";
        final String Remote_Identifier_Generator = "RemoteIdentifierSource";
        final String Identifier_Pool = "IdentifierPool";
        Object savedIdentifierSource = null;
        ArrayList<String> errors = new ArrayList<String>();
        PatientIdentifierType patientIdentifierType = null;
        Object identifierSourceType = postBody.get("sourceType");
        Object identifierTypeUuid = postBody.get("identifierType");
        Object name = postBody.get("name");
        Object description = postBody.get("description");
        Object generateIdentifiers = postBody.get("generateIdentifiers");
        
        if (generateIdentifiers != null) {
            Object comment = postBody.get("comment");
            Object numberToGenerate = postBody.get("numberToGenerate");
            Object sourceUuid = postBody.get("sourceUuid");
            SimpleObject identifiersToExport = new SimpleObject();
            if (comment == null) {
                comment = "Batch Export of " + numberToGenerate + " to file";
            }
            if(numberToGenerate == null){
                numberToGenerate = "0";
            }
            if(sourceUuid != null){
                IdentifierSource identifierSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
                if(identifierSource != null){
                    List<String> identifiers = Context.getService(IdentifierSourceService.class).
                        generateIdentifiers(identifierSource, Integer.parseInt(numberToGenerate.toString()), comment.toString());
                    identifiersToExport.add("identifiers", identifiers);
                    return identifiersToExport;
                }      
            }
        }
        if (identifierSourceType == null || StringUtils.isBlank(identifierSourceType.toString())) {
            errors.add("source type"); 
        }
        if (name == null || StringUtils.isBlank(name.toString())) {
            errors.add("name"); 
        }
        if (identifierTypeUuid == null || StringUtils.isBlank(identifierTypeUuid.toString())) {
            errors.add("patient identifier type"); 
        }else{
            patientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierTypeUuid.toString());
            if(patientIdentifierType == null){
                errors.add("patient identifier type");
            }
        }
        if(errors.size() > 0) {
            throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
        }

        if(identifierSourceType.equals(Sequential_Identifier_Generator)) {
            SequentialIdentifierGenerator identifierSource = new SequentialIdentifierGenerator();
            Object firstIdentifierBase = postBody.get("firstIdentifierBase");
            Object baseCharacterSet = postBody.get("baseCharacterSet");
            Object prefix = postBody.get("prefix");
            Object suffix = postBody.get("suffix");
            Object minLength = postBody.get("minLength");
            Object maxLength = postBody.get("maxLength");

            if (firstIdentifierBase == null || StringUtils.isBlank(firstIdentifierBase.toString())) { 
                errors.add("first identifier base"); 
            }
            if (baseCharacterSet == null || StringUtils.isBlank(baseCharacterSet.toString())) { 
                errors.add("base character set"); 
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (prefix != null && StringUtils.isNotBlank(prefix.toString())) { 
                identifierSource.setPrefix(prefix.toString()); 
            }
            if (suffix != null && StringUtils.isNotBlank(suffix.toString())) { 
                identifierSource.setSuffix(suffix.toString()); 
            }
            if (minLength != null && StringUtils.isNotBlank(minLength.toString())) {
                if(StringUtils.isNumeric(minLength.toString())) {
                        identifierSource.setMinLength(Integer.parseInt(minLength.toString()));
                }
            }
            if (maxLength != null && StringUtils.isNotBlank(maxLength.toString())) {
                if(StringUtils.isNumeric(maxLength.toString())) {
                        identifierSource.setMaxLength(Integer.parseInt(maxLength.toString()));
                }
            }
            if(errors.size() > 0) {
                throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
            }

            identifierSource.setIdentifierType(patientIdentifierType);
            identifierSource.setName(name.toString());
            identifierSource.setBaseCharacterSet(baseCharacterSet.toString());
            identifierSource.setFirstIdentifierBase(firstIdentifierBase.toString());
            savedIdentifierSource =  save(identifierSource);
        }
        else if(identifierSourceType.equals(Remote_Identifier_Generator)) {
            RemoteIdentifierSource identifierSource = new RemoteIdentifierSource();
            Object username = postBody.get("username");
            Object password = postBody.get("password");
            Object url = postBody.get("url");

            if (url == null || StringUtils.isBlank(url.toString())) { 
                    errors.add("url"); 
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (username!= null && StringUtils.isNotBlank(username.toString())) { 
                    identifierSource.setUser(username.toString()); 
            }
            if (password != null && StringUtils.isNotBlank(password.toString())) {
                if(username == null || StringUtils.isBlank(username.toString())) {
                    errors.add("username");
                }
                else {
                    identifierSource.setPassword(password.toString());
                }
            }
            if(errors.size() > 0) {
                throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
            }

            identifierSource.setIdentifierType(patientIdentifierType);
            identifierSource.setName(name.toString());
            identifierSource.setUrl(url.toString());
            savedIdentifierSource =  save(identifierSource);
        }
        else if(identifierSourceType.equals(Identifier_Pool)){
            IdentifierPool identifierSource = new IdentifierPool();
            Object batchSize = postBody.get("batchSize");
            Object minPoolSize = postBody.get("minPoolSize");
            Object sourceUuid = postBody.get("sourceUuid");
            Object sequential = postBody.get("sequential");
            Object refillWithScheduledTask = postBody.get("refillWithScheduledTask");

            if (sourceUuid != null && StringUtils.isNotBlank(sourceUuid.toString())) {
                IdentifierSource poolIdentifierSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
                if(poolIdentifierSource != null){
                    identifierSource.setSource(poolIdentifierSource);
                }else{
                    errors.add("identifier souce");
                }
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (batchSize != null && StringUtils.isNotBlank(batchSize.toString())) {
                if(StringUtils.isNumeric(batchSize.toString())) {
                    identifierSource.setBatchSize(Integer.parseInt(batchSize.toString()));
                }
            }
            if (minPoolSize != null && StringUtils.isNotBlank(minPoolSize.toString())) {
                if(StringUtils.isNumeric(minPoolSize.toString())) {
                    identifierSource.setMinPoolSize(Integer.parseInt(minPoolSize.toString()));
                }
            }
            if (sequential != null && StringUtils.isNotBlank(sequential.toString())) {
                identifierSource.setSequential(parseBoolean(sequential));
            }
            if (refillWithScheduledTask != null && StringUtils.isNotBlank(refillWithScheduledTask.toString())) {
                identifierSource.setRefillWithScheduledTask(parseBoolean(refillWithScheduledTask));
            }
            if(errors.size() > 0) {
                throw new ValidationException("The values of the following inputs are missing or invalid: " + errors.toString());
            }

            identifierSource.setIdentifierType(patientIdentifierType);
            identifierSource.setName(name.toString());
            savedIdentifierSource =  save(identifierSource);
        }
        return ConversionUtil.convertToRepresentation(savedIdentifierSource, Representation.DEFAULT);         
    }
    
    @Override
    public Object update(String uuid, SimpleObject updateBody, RequestContext context) throws ResponseException {
        Object updatedIdentifierSource = null;
        Object reservedIdentifiersToUpload = updateBody.get("reservedIdentifiers");
        Object operation = updateBody.get("operation");
        Object generateIdentifiers = updateBody.get("generateIdentifiers");
        IdentifierSource identifierSourceToUpdate = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(uuid);
        if (reservedIdentifiersToUpload != null) {
            List<String> reservedIdentifiersList = new ArrayList<String>(Arrays.asList(reservedIdentifiersToUpload.toString().split(",")));
            if(identifierSourceToUpdate != null){
               for (int counter = 0; counter < reservedIdentifiersList.size(); counter++) {
                    if (StringUtils.isNotBlank(reservedIdentifiersList.get(counter))) {
                        identifierSourceToUpdate.addReservedIdentifier(reservedIdentifiersList.get(counter));
                    }
                }
                Context.getService(IdentifierSourceService.class).saveIdentifierSource(identifierSourceToUpdate);
            }
        }
        if (operation != null && operation.toString().equals("uploadFromSource")) {
            if(identifierSourceToUpdate != null){
                IdentifierPool pool = (IdentifierPool) identifierSourceToUpdate;
                Object batchSize = updateBody.get("batchSize");
                if(pool != null && batchSize != null && StringUtils.isNumeric(batchSize.toString())){
                    Context.getService(IdentifierSourceService.class).addIdentifiersToPool(pool, Integer.parseInt(batchSize.toString()));
                }
            }
        }
        if (operation != null&& operation.toString().equals("uploadFromFile")) {
            Object identifiers = updateBody.get("identifiers");
            if(identifierSourceToUpdate != null && identifiers != null){
                List<String> ids = new ArrayList<String>(Arrays.asList(identifiers.toString().split(",")));
                IdentifierPool pool = (IdentifierPool) identifierSourceToUpdate;
                if(pool != null && ids != null){
                    Context.getService(IdentifierSourceService.class).addIdentifiersToPool(pool, ids);
                } 
            }
        }
        if(identifierSourceToUpdate instanceof SequentialIdentifierGenerator){
            SequentialIdentifierGenerator identifierSource = (SequentialIdentifierGenerator) identifierSourceToUpdate;
            Object firstIdentifierBase = updateBody.get("firstIdentifierBase");
            Object baseCharacterSet = updateBody.get("baseCharacterSet");
            Object prefix = updateBody.get("prefix");
            Object suffix = updateBody.get("suffix");
            Object minLength = updateBody.get("minLength");
            Object maxLength = updateBody.get("maxLength");
            Object name = updateBody.get("name");
            Object description = updateBody.get("description");
            
            if (firstIdentifierBase != null && StringUtils.isNotBlank(firstIdentifierBase.toString())) { 
                identifierSource.setFirstIdentifierBase(firstIdentifierBase.toString());
            }
            if (baseCharacterSet != null&& StringUtils.isNotBlank(baseCharacterSet.toString())) { 
                identifierSource.setBaseCharacterSet(baseCharacterSet.toString());
            }
            if (name != null && StringUtils.isNotBlank(name.toString())) {
                identifierSource.setName(name.toString());
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (prefix != null && StringUtils.isNotBlank(prefix.toString())) { 
                identifierSource.setPrefix(prefix.toString()); 
            }
            if (suffix != null && StringUtils.isNotBlank(suffix.toString())) { 
                identifierSource.setSuffix(suffix.toString()); 
            }
            if (minLength != null && StringUtils.isNotBlank(minLength.toString())) {
                if(StringUtils.isNumeric(minLength.toString())) {
                    identifierSource.setMinLength(Integer.parseInt(minLength.toString()));
                }
            }
            if (maxLength != null && StringUtils.isNotBlank(maxLength.toString())) {
                if(StringUtils.isNumeric(maxLength.toString())) {
                    identifierSource.setMaxLength(Integer.parseInt(maxLength.toString()));
                }
            }
            updatedIdentifierSource =  save(identifierSource);
        }
        if(identifierSourceToUpdate instanceof IdentifierPool){
            IdentifierPool identifierSource = (IdentifierPool) identifierSourceToUpdate;
            Object name = updateBody.get("name");
            Object description = updateBody.get("description");
            Object batchSize = updateBody.get("batchSize");
            Object minPoolSize = updateBody.get("minPoolSize");
            Object sourceUuid = updateBody.get("sourceUuid");
            Object sequential = updateBody.get("sequential");
            Object refillWithScheduledTask = updateBody.get("refillWithScheduledTask");
            
            if (sourceUuid != null && StringUtils.isNotBlank(sourceUuid.toString())) {
                IdentifierSource poolIdentifierSource = Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(sourceUuid.toString());
                if(poolIdentifierSource != null){
                    identifierSource.setSource(poolIdentifierSource);
                }
            }
            else{
                identifierSource.setSource(null);
            }
            if (name != null && StringUtils.isNotBlank(name.toString())) {
                identifierSource.setName(name.toString());
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (batchSize != null && StringUtils.isNotBlank(batchSize.toString())) {
                if(StringUtils.isNumeric(batchSize.toString())) {
                    identifierSource.setBatchSize(Integer.parseInt(batchSize.toString()));
                }
            }
            if (minPoolSize != null && StringUtils.isNotBlank(minPoolSize.toString())) {
                if(StringUtils.isNumeric(minPoolSize.toString())) {
                    identifierSource.setMinPoolSize(Integer.parseInt(minPoolSize.toString()));
                }
            }
            if (sequential != null && StringUtils.isNotBlank(sequential.toString())) {
                identifierSource.setSequential(parseBoolean(sequential));
            }
            if (refillWithScheduledTask != null && StringUtils.isNotBlank(refillWithScheduledTask.toString())) {
                identifierSource.setRefillWithScheduledTask(parseBoolean(refillWithScheduledTask));
            }
            updatedIdentifierSource =  save(identifierSource);
        }
        if(identifierSourceToUpdate instanceof RemoteIdentifierSource){
            RemoteIdentifierSource identifierSource = (RemoteIdentifierSource) identifierSourceToUpdate;
            Object username = updateBody.get("user");
            Object password = updateBody.get("password");
            Object url = updateBody.get("url");
            Object name = updateBody.get("name");
            Object description = updateBody.get("description");

            if (url != null && StringUtils.isNotBlank(url.toString())) { 
                identifierSource.setUrl(url.toString());
            }
            if (name != null && StringUtils.isNotBlank(name.toString())) {
                identifierSource.setName(name.toString());
            }
            if (description != null && StringUtils.isNotBlank(description.toString())) {
                identifierSource.setDescription(description.toString());
            }
            if (username!= null && StringUtils.isNotBlank(username.toString())) { 
                identifierSource.setUser(username.toString());
            }
            if (password != null && StringUtils.isNotBlank(password.toString())) {
                identifierSource.setPassword(password.toString());
            }
            updatedIdentifierSource = save(identifierSource);         
        }
        return ConversionUtil.convertToRepresentation(updatedIdentifierSource, Representation.DEFAULT);
    }
	
    @Override
    public IdentifierSource save(IdentifierSource identifierSource) {
        return Context.getService(IdentifierSourceService.class).saveIdentifierSource(identifierSource);
    }

    @Override
    public IdentifierSource getByUniqueId(String uniqueId) {
        return Context.getService(IdentifierSourceService.class).getIdentifierSourceByUuid(uniqueId);
    }
	
    @Override
    protected void delete(IdentifierSource identifierSource, String reason, RequestContext context) throws ResponseException {
        Context.getService(IdentifierSourceService.class).retireIdentifierSource(identifierSource, reason);
    }
	
    @Override
    public void purge(IdentifierSource identifierSource, RequestContext context) throws ResponseException {
        Context.getService(IdentifierSourceService.class).purgeIdentifierSource(identifierSource);
    }
    
    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<IdentifierSource>(Context.getService(IdentifierSourceService.class).getAllIdentifierSources(false), context);
    }
    
    @Override
    protected PageableResult doSearch(RequestContext context) throws ResponseException {
        String identifierType = context.getRequest().getParameter("identifierType");
        if(identifierType != null){
            PatientIdentifierType requestedPatientIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(identifierType);
            if(requestedPatientIdentifierType != null){  
                List<IdentifierSource> requestedIdentifierSources = Context.getService(IdentifierSourceService.class)
                        .getIdentifierSourcesByType(requestedPatientIdentifierType);
                return new NeedsPaging<IdentifierSource>(requestedIdentifierSources, context);
            }
        }
        return new EmptySearchResult();
    }
    
    private Boolean parseBoolean(Object value) {
        List<String> trueValues = Arrays.asList("true", "1", "on", "yes");
        List<String> falseValues = Arrays.asList("false", "0", "off", "no"); 
        String val = value.toString().trim().toLowerCase();
        
        if (trueValues.contains(val)) {
            return Boolean.TRUE;
        }
        else if (falseValues.contains(val)) {
            return Boolean.FALSE;
        }
        return null;
    }
}
