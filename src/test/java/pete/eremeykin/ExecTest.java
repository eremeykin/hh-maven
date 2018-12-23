package pete.eremeykin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;

import org.junit.Before;
import org.junit.Rule;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Map;

import static pete.eremeykin.Dictionary.*;

@RunWith(Parameterized.class)
public class ExecTest {

    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";
    @Rule
    public MojoRule rule = new MojoRule();

    public File config = new File("target/test-classes/custom-config-project/");


    @Parameterized.Parameters
    public static Iterable<Object[]> mojoParamters() {
        return asList(new Object[][]{
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, true, null, null, null, null, null, null, null, null, null, null},
                {null, null, true, null, null, null, null, null, null, null, null, null},
                {null, null, null, true, null, null, null, null, null, null, null, null},
                {null, null, null, null, true, null, null, null, null, null, null, null},
                {null, null, null, null, null, "test User", null, null, null, null, null, null},
                {null, null, null, null, null, null, "aTJs3d57kID", null, null, null, null, null},
                {null, null, null, null, null, null, null, "212.45.46.5", null, null, null, null},
//              {null, null, null, null, null, null, null, null, 3451, null, null, null}
                {null, null, null, null, null, null, null, null, null, "MySuperDB", null, null}
        });

    }


    @Parameterized.Parameter(0)
    public String exec;

    @Parameterized.Parameter(1)
    public Boolean quoteNames;

    @Parameterized.Parameter(2)
    public Boolean completeInsert;

    @Parameterized.Parameter(3)
    public Boolean extendedInsert;

    @Parameterized.Parameter(4)
    public Boolean singleTransaction;

    @Parameterized.Parameter(5)
    public String userName;

    @Parameterized.Parameter(6)
    public String password;

    @Parameterized.Parameter(7)
    public String host;

    @Parameterized.Parameter(8)
    public Integer port;

    @Parameterized.Parameter(9)
    public String dbName;

    @Parameterized.Parameter(10)
    public File outputFile;

    @Parameterized.Parameter(11)
    public File baseDir;


    @Before
    // I personally want to check the result file
    //@After
    public void deleteOldDump() throws Exception {
        DumpMojo dumpMojo = (DumpMojo) rule.lookupConfiguredMojo(config, GOAL_DUMP);
        File oldDump = (File) rule.getVariableValueFromObject(dumpMojo, PARAM_OUTPUT_FILE);
        assertTrue(!oldDump.exists() || oldDump.delete());
    }


    @Test
    public void testDumpExists() throws Exception {
        DumpMojo dumpMojo = (DumpMojo) rule.lookupConfiguredMojo(config, GOAL_DUMP);
        assertNotNull(dumpMojo);
        Map<String, Object> mojoFields = rule.getVariablesAndValuesFromObject(DumpMojo.class, dumpMojo);

        for (Map.Entry<String, Object> mp : rule.getVariablesAndValuesFromObject(this.getClass(), this).entrySet()) {
            String variable = mp.getKey();
            Object value = mp.getValue();
            if (value != null && mojoFields.containsKey(variable)) {
                rule.setVariableValueToObject(dumpMojo, variable, value);
            }
        }
        // update fields
        mojoFields = rule.getVariablesAndValuesFromObject(DumpMojo.class, dumpMojo);

        dumpMojo.execute();


        File outputFile = (File) rule.getVariableValueFromObject(dumpMojo, PARAM_OUTPUT_FILE);
        assertNotNull(outputFile);
        assertTrue(outputFile.exists());

        String fileContent = FileUtils.readFileToString(outputFile, "UTF-8");


        String exec = (String) mojoFields.get(PARAM_EXEC);
        Boolean quoteNames = (Boolean) mojoFields.get(PARAM_QUOTE_NAMES);
        Boolean completeInsert = (Boolean) mojoFields.get(PARAM_COMPLETE_INSERT);
        Boolean extendedInsert = (Boolean) mojoFields.get(PARAM_EXTEND_INSERT);
        Boolean singleTransaction = (Boolean) mojoFields.get(PARAM_SINGLE_TRANSACTION);
        String userName = (String) mojoFields.get(PARAM_USER_NAME);
        String password = (String) mojoFields.get(PARAM_PASSWORD);
        String host = (String) mojoFields.get(PARAM_HOST);
        Integer port = (Integer) mojoFields.get(PARAM_PORT);
        String dbName = (String) mojoFields.get(PARAM_DB_NAME);
//        File outputFile = mojoFields.get(PARAM_OUTPUT_FILE);
//        File baseDir = mojoFields.get(PARAM_BASE_DIR);


        assertThat(fileContent, containsString(PARAM_QUOTE_NAMES + ": " + isEnabled(quoteNames)));
        assertThat(fileContent, containsString(PARAM_COMPLETE_INSERT + ": " + isEnabled(completeInsert)));
        assertThat(fileContent, containsString(PARAM_EXTEND_INSERT + ": " + isEnabled(extendedInsert)));
        assertThat(fileContent, containsString(PARAM_SINGLE_TRANSACTION + ": " + isEnabled(singleTransaction)));

        assertThat(fileContent, containsString(String.format("User: %s    Password: %s", userName, password)));
        assertThat(fileContent, containsString(String.format("Host: %s    Database: %s", host, dbName)));

    }

    private static String isEnabled(Boolean value) {
        if (value == null) {
            return DISABLED; // as it is set in custom config
        }
        return value ? ENABLED : DISABLED;
    }
}