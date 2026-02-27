package org.openmrs.module.idgen.editor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.propertyeditor.AutoGenerationOptionEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AutoGenerationOptionEditorTest extends IdgenBaseTest {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @BeforeEach
    public void beforeEachTest() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    @Test
    public void testPropertyEditorConversion() {

        AutoGenerationOptionEditor editor = new AutoGenerationOptionEditor();
        editor.setAsText("1");

        AutoGenerationOption expectedOption = identifierSourceService.getAutoGenerationOption(1);
        assertEquals(expectedOption, editor.getValue());
        assertEquals("1", editor.getAsText());

    }


}
