package pete.eremeykin;


import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

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
        defaultFieldsValues.put("exec", "mysqldump");
        defaultFieldsValues.put("quoteNames", true);
        defaultFieldsValues.put("completeInsert", true);
        defaultFieldsValues.put("extendedInsert", true);
        defaultFieldsValues.put("singleTransaction", true);
        defaultFieldsValues.put("userName", "root");
        defaultFieldsValues.put("dbName", "database");
        defaultFieldsValues.put("password", "mysql");
        defaultFieldsValues.put("host", "localhost");
        defaultFieldsValues.put("port", 3306);

        Map<String, String> defaultFilePaths = new HashMap<>();
        defaultFilePaths.put("outputFile", "/dump.sql");
        defaultFilePaths.put("baseDir", "target/test-classes/default-config-project");

        ///////////////////Custom Config/////////////////////////////////////

        File customConfig = new File("target/test-classes/custom-config-project/");

        Map<String, Object> customFieldsValues = new HashMap<>();

        customFieldsValues.put("quoteNames", false);
        customFieldsValues.put("completeInsert", false);
        customFieldsValues.put("extendedInsert", false);
        customFieldsValues.put("singleTransaction", false);
        customFieldsValues.put("userName", "admin");
        customFieldsValues.put("dbName", "company");
        customFieldsValues.put("password", "test123");
        customFieldsValues.put("host", "mycompany.com");
        customFieldsValues.put("port", 3137);

        Map<String, String> customFilePaths = new HashMap<>();
        customFilePaths.put("outputFile", "/company.dump");
        customFilePaths.put("baseDir", "/bin");
        customFilePaths.put("exec", "/mycommand.sh");

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

        DumpMojo dumpMojo = (DumpMojo) rule.lookupConfiguredMojo(config, "dump");
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


//        dumpMojo.execute();
//
//        File outputDirectory = (File) rule.getVariableValueFromObject(dumpMojo, "outputDirectory");
//        assertNotNull(outputDirectory);
//        assertTrue(outputDirectory.exists());
//
//        File touch = new File(outputDirectory, "touch.txt");
//        assertTrue(touch.exists());

    }

//    @WithoutMojo
//    @Test
//    public void testSomethingWhichDoesNotNeedTheMojoAndProbablyShouldBeExtractedIntoANewClassOfItsOwn() {
//        assertTrue(true);
//    }
}

