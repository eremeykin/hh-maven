package pete.eremeykin;


import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;
import static pete.eremeykin.Dictionary.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pete.eremeykin.DumpMojo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RunWith(Parameterized.class)
public class ConfigTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Parameterized.Parameters
    public static Iterable<Object[]> configs() {

        ///////////////////Default Config/////////////////////////////////////

        File defaultConfig = new File("target/test-classes/default-config-project/");

        Map<String, Object> defaultFieldsValues = new HashMap<>();
        defaultFieldsValues.put(PARAM_EXEC, "mysqldump");
        defaultFieldsValues.put(PARAM_QUOTE_NAMES, true);
        defaultFieldsValues.put(PARAM_COMPLETE_INSERT, true);
        defaultFieldsValues.put(PARAM_EXTEND_INSERT, true);
        defaultFieldsValues.put(PARAM_SINGLE_TRANSACTION, true);
        defaultFieldsValues.put(PARAM_USER_NAME, "root");
        defaultFieldsValues.put(PARAM_DB_NAME, "database");
        defaultFieldsValues.put(PARAM_PASSWORD, "mysql");
        defaultFieldsValues.put(PARAM_HOST, "localhost");
        defaultFieldsValues.put(PARAM_PORT, 3306);

        Map<String, String> defaultFilePaths = new HashMap<>();
        defaultFilePaths.put(PARAM_OUTPUT_FILE, "/dump.sql");
        defaultFilePaths.put(PARAM_BASE_DIR, "target/test-classes/default-config-project");

        ///////////////////Custom Config/////////////////////////////////////

        File customConfig = new File("target/test-classes/custom-config-project/");

        Map<String, Object> customFieldsValues = new HashMap<>();

        customFieldsValues.put(PARAM_QUOTE_NAMES, false);
        customFieldsValues.put(PARAM_COMPLETE_INSERT, false);
        customFieldsValues.put(PARAM_EXTEND_INSERT, false);
        customFieldsValues.put(PARAM_SINGLE_TRANSACTION, false);
        customFieldsValues.put(PARAM_USER_NAME, "admin");
        customFieldsValues.put(PARAM_DB_NAME, "company");
        customFieldsValues.put(PARAM_PASSWORD, "test123");
        customFieldsValues.put(PARAM_HOST, "mycompany.com");
        customFieldsValues.put(PARAM_PORT, 3137);

        Map<String, String> customFilePaths = new HashMap<>();
        customFilePaths.put(PARAM_OUTPUT_FILE, "/company.dump");
        customFilePaths.put(PARAM_BASE_DIR, "/tmp");
        customFilePaths.put(PARAM_EXEC, "cmd/mysqldump.sh");

        return asList(new Object[][]{{defaultConfig, defaultFieldsValues, defaultFilePaths},
                {customConfig, customFieldsValues, customFilePaths}});
    }

    @Parameterized.Parameter(0)
    public File config;

    @Parameterized.Parameter(1)
    public Map<String, Object> expectedFieldsValues;

    @Parameterized.Parameter(2)
    public Map<String, String> expectedFilesPaths;


    @Test
    public void testConfig() throws Exception {
        assertNotNull(config);
        assertTrue(config.exists());
        DumpMojo dumpMojo = (DumpMojo) rule.lookupConfiguredMojo(config, GOAL_DUMP);
        assertNotNull(dumpMojo);
        Map<String, Object> actualFields = rule.getVariablesAndValuesFromObject(DumpMojo.class, dumpMojo);

        for (Map.Entry<String, Object> expectedFieldValue : expectedFieldsValues.entrySet()) {
            String expectedKey = expectedFieldValue.getKey();
            Object expectedValue = expectedFieldValue.getValue();
            assertThat(actualFields, hasEntry(expectedKey, expectedValue));
        }

        for (Map.Entry<String, String> expectedFile : expectedFilesPaths.entrySet()) {
            String expectedKey = expectedFile.getKey();
            String expectedValue = expectedFile.getValue();
            String actualFilePath;
            try {
                actualFilePath = ((File) actualFields.get(expectedKey)).getCanonicalPath();
            } catch (ClassCastException exc) {
                actualFilePath = (String) actualFields.get(expectedKey);
            }
            assertThat(actualFilePath, endsWith(expectedValue));
        }
    }
}

