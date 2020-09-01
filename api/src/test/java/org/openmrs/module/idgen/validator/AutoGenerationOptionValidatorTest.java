package org.openmrs.module.idgen.validator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class AutoGenerationOptionValidatorTest extends IdgenBaseTest {
	
	@Autowired 
	IdentifierSourceService identifierService;
	
	AutoGenerationOptionValidator validator;
	
	private Errors errors;
	
	/**
	 * @throws Exception 
	 * @see AutoGenerationOptionValidator#validate(Object,Errors)
	 */
	@Test
	public void validate_shouldFailIfAutoAndManualOptionsAreDisabled() throws Exception {
		
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		validator = new AutoGenerationOptionValidator ();
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setId(1);
		
		IdentifierSource source = identifierService.getIdentifierSource(1);
		
		AutoGenerationOption autoGenerationOption= new AutoGenerationOption();
		autoGenerationOption.setAutomaticGenerationEnabled(false);
		autoGenerationOption.setManualEntryEnabled(false);
		autoGenerationOption.setIdentifierType(patientIdentifierType);
		autoGenerationOption.setSource(source);
		
	    errors = new BindException(autoGenerationOption, "autoGenerationOption");
	    validator.validate(autoGenerationOption,errors);
	    assertTrue(errors.hasErrors());
	}
	
	
	@Test
	public void validate_shouldPassIfAutoOrManualOptionIsEnabled() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		validator = new AutoGenerationOptionValidator ();
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setId(1);
		
		IdentifierSource source = identifierService.getIdentifierSource(1);
		
		AutoGenerationOption autoGenerationOption= new AutoGenerationOption();
		autoGenerationOption.setAutomaticGenerationEnabled(false);
		autoGenerationOption.setManualEntryEnabled(true);
		autoGenerationOption.setIdentifierType(patientIdentifierType);
		autoGenerationOption.setSource(source);
	  
		errors = new BindException(autoGenerationOption, "autoGenerationOption");
	    validator.validate(autoGenerationOption,errors);
	    assertFalse(errors.hasErrors());
	}

}
