package org.openmrs.module.idgen.web.contract;

public class IdentifierSource {

    private String uuid;
    private String name;
    private String prefix;

    public IdentifierSource(String uuid, String name, String prefix) {

        this.uuid = uuid;
        this.name = name;
        this.prefix = prefix;
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
