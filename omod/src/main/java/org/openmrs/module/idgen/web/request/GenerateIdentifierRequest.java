package org.openmrs.module.idgen.web.request;

public class GenerateIdentifierRequest {
    private String identifierSourceName;
    private String comment;

    public GenerateIdentifierRequest() {
    }

    public GenerateIdentifierRequest(String identifierSourceName, String comment) {
        this.identifierSourceName = identifierSourceName;
        this.comment = comment;
    }

    public String getIdentifierSourceName() {
        return identifierSourceName;
    }

    public void setIdentifierSourceName(String identifierSourceName) {
        this.identifierSourceName = identifierSourceName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
