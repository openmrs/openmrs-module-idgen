package org.openmrs.module.idgen.editor;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.propertyeditor.AutoGenerationOptionEditor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;

public class AutoGenerationOptionEditorTest extends IdgenBaseTest {

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Before
    public void beforeEachTest() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    @Test
    public void testPropertyEditorConversion() {

        AutoGenerationOptionEditor editor = new AutoGenerationOptionEditor();
        editor.setAsText("1");

        AutoGenerationOption expectedOption = identifierSourceService.getAutoGenerationOption(1);
        Assert.assertEquals(expectedOption, editor.getValue());
        Assert.assertEquals("1", editor.getAsText());

    }


}
