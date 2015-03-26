package org.openmrs.module.idgen.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class IdgenIdentifierSourceControllerTest {

    @Mock
    private SequentialIdentifierGenerator identifierSource;

    @Mock
    private IdentifierSourceService identifierSourceService;

    private IdgenIdentifierSourceController controller;

    @Before
    public void before() throws Exception {
        initMocks(this);
        mockStatic(Context.class);
        controller = new IdgenIdentifierSourceController();
    }

    @Test
    public void shouldGenerateIdentifier() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);
        when(identifierSource.getPrefix()).thenReturn("OPD");
        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(new ArrayList<IdentifierSource>() {{
            this.add(identifierSource);
        }});
        controller.generateIdentifier(new GenerateIdentifierRequest("OPD", "New HIV Patient"));
        verify(identifierSourceService).generateIdentifier(identifierSource, "New HIV Patient");
    }

    @Test
    public void shouldReturnAllIdentifierSources() throws Exception {
        when(Context.getService(IdentifierSourceService.class)).thenReturn(identifierSourceService);
        ArrayList<IdentifierSource> identifierSources = new ArrayList<IdentifierSource>() {{
            this.add(new SequentialIdentifierGenerator());
        }};
        when(identifierSourceService.getAllIdentifierSources(false)).thenReturn(identifierSources);
        String resultIdentifierResources = controller.getAllIdentifierSources();

        System.out.println(resultIdentifierResources);
        assertThat(resultIdentifierResources, notNullValue());
    }
}
