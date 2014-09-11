package org.openmrs.module.idgen.propertyeditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class AutoGenerationOptionEditor extends PropertyEditorSupport {


    protected static Log log = LogFactory.getLog(AutoGenerationOptionEditor.class);

    /**
     * Default Constructor
     */
    public AutoGenerationOptionEditor() { }

    /**
     * @see PropertyEditorSupport#setAsText(String)
     */
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            try {
                IdentifierSourceService iss = Context.getService(IdentifierSourceService.class);
                setValue(iss.getAutoGenerationOption(Integer.valueOf(text)));
            }
            catch (Exception e) {
                throw new IllegalArgumentException("IdentifierSource not found for " + text, e);
            }
        }
        else {
            setValue(null);
        }
    }

    /**
     * @see PropertyEditorSupport#getAsText()
     */
    public String getAsText() {
        AutoGenerationOption option = (AutoGenerationOption) getValue();
        if (option != null) {
            return option.getId().toString();
        }
        return null;
    }


}
