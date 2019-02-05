package de.viadee.anchorj.server.application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import de.viadee.anchorj.server.configuration.AppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnchorServerApplicationTests {

    @Autowired
    private AppConfiguration configuration;

    @Test
    public void testConfiguration() {
        assertEquals("http://localhost:54321", this.configuration.getConnectionName("local-H2O"));
        assertEquals("spark://localhost:7077", this.configuration.getSparkMasterUrl());
        assertTrue(this.configuration.getSparkLibFolder().endsWith("anchor-h2o-spark/target/libs"));
    }

}
