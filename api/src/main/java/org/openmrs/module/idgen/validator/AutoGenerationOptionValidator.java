package org.openmrs.module.idgen.validator;

import java.util.List;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Handler(supports = { AutoGenerationOption.class }, order = 50)
public class AutoGenerationOptionValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
      return AutoGenerationOption.class.isAssignableFrom(clazz);
	}
	@Override
	public void validate(Object object, Errors errors) {
		
		if (!(object instanceof AutoGenerationOption)) {
			return;
		}
		AutoGenerationOption autoGenerationOption = (AutoGenerationOption) object;
		if(!(autoGenerationOption.isManualEntryEnabled() || autoGenerationOption.isAutomaticGenerationEnabled())){
			errors.reject("Either automatic or manual generation must be enabled");
		}

		List<AutoGenerationOption> generationOptions= Context.getService(IdentifierSourceService.class).getAutoGenerationOptions(autoGenerationOption.getIdentifierType());
		
		for(AutoGenerationOption generationOption:generationOptions) {
		   
		 if(autoGenerationOption.getIdentifierType().equals(generationOption.getIdentifierType())) {
				
			if(autoGenerationOption.getLocation().equals(generationOption.getLocation())){

				if(autoGenerationOption.getSource().equals(generationOption.getSource())) {
					errors.reject("An autogeneration option is already defined for this identifier type, location, and identifier source");
					break;	
				   }
				}	  
		    }	 
	    }
	}
}
