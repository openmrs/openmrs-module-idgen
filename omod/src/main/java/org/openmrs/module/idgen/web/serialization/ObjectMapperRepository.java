package org.openmrs.module.idgen.web.serialization;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.idgen.web.contract.IdentifierSource;

import java.io.IOException;
import java.util.List;

public class ObjectMapperRepository {
    public static ObjectMapper objectMapper = new ObjectMapper();

    public String writeValueAsString(List<IdentifierSource> result) throws IOException {
        return objectMapper.writeValueAsString(result);
    }
}