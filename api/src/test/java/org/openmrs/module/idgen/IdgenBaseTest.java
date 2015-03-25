package org.openmrs.module.idgen;

import org.hibernate.cfg.Environment;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Properties;

/**
 * Base class that can be used for an common funcationality or modifications to the standard base module context sensitive test
 */
public abstract class IdgenBaseTest extends BaseModuleContextSensitiveTest {

    @Override
    public Properties getRuntimeProperties() {
        Properties props = super.getRuntimeProperties();
        String url = props.getProperty(Environment.URL);
        if (url.contains("jdbc:h2:") && !url.toLowerCase().contains(";mvcc=true")) {
            props.setProperty(Environment.URL, url + ";mvcc=true");
        }
        return props;
    }
}
