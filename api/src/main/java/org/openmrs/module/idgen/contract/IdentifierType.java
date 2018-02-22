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
package org.openmrs.module.idgen.contract;

import java.util.List;

public class IdentifierType {
    private String uuid;
    private String name;
    private String description;
    private String format;
    private boolean required;
    private boolean primary;
    private List<IdentifierSource> identifierSources;

    public IdentifierType(){

    }
    public IdentifierType(String uuid, String name, String description, String format, boolean required, boolean primary, List<IdentifierSource> identifierSources) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.format = format;
        this.required = required;
        this.primary = primary;
        this.identifierSources = identifierSources;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public List<IdentifierSource> getIdentifierSources() {
        return identifierSources;
    }

    public void setIdentifierSources(List<IdentifierSource> identifierSources) {
        this.identifierSources = identifierSources;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifierType that = (IdentifierType) o;

        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;

    }

}