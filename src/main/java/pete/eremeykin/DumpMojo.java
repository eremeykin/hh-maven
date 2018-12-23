package pete.eremeykin;


import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Goal which makes a dump of mysql database
 */
@Mojo(name = "dump", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DumpMojo extends AbstractMojo {

    private static final String ENC_UTF8 = "UTF-8";
    private static final String MSG_EXIT_CODE = "command exited with code: ";

    private static final String CMD_DUMP = "mysqldump";
    private static final String ARG_QUOTE_NAMES = "-Q";
    private static final String ARG_COMPLETE_INSERT = "-c";
    private static final String ARG_EXTENDED_INSERT = "-e";
    private static final String ARG_SINGLE_TRANSACTION = "--single-transaction";
    private static final String ARG_USER = "--user";
    private static final String ARG_PASSWORD = "--password";
    private static final String ARG_HOST = "--host";
    private static final String ARG_PORT = "--port";

    @Parameter(defaultValue = CMD_DUMP)
    private String exec;

    @Parameter(defaultValue = "true")
    private boolean quoteNames;

    @Parameter(defaultValue = "true")
    private boolean completeInsert;

    @Parameter(defaultValue = "true")
    private boolean extendedInsert;

    @Parameter(defaultValue = "true")
    private boolean singleTransaction;

    @Parameter(defaultValue = "root", required = true)
    private String userName;

    @Parameter(defaultValue = "mysql", required = true)
    private String password;

    @Parameter(defaultValue = "localhost", required = true)
    private String host;

    @Parameter(defaultValue = "3306", required = true)
    private int port;

    @Parameter(defaultValue = "database", required = true)
    private String dbName;

    @Parameter(defaultValue = "dump.sql", property = "mySqlDump")
    private File outputFile;

    @Parameter(defaultValue = "${project.basedir}", readonly = true, required = false)
    private File baseDir;


    public void execute() throws MojoExecutionException {
        Command.Builder commandBuilder = new Command.Builder(exec);

        if (quoteNames) {
            commandBuilder.addArgument(ARG_QUOTE_NAMES);
        }
        if (completeInsert) {
            commandBuilder.addArgument(ARG_COMPLETE_INSERT);
        }
        if (extendedInsert) {
            commandBuilder.addArgument(ARG_EXTENDED_INSERT);
        }
        if (singleTransaction) {
            commandBuilder.addArgument(ARG_SINGLE_TRANSACTION);
        }

        commandBuilder.addArgument(ARG_USER, userName)
                .addArgument(ARG_PASSWORD, password)
                .addArgument(ARG_HOST, host)
                .addArgument(ARG_PORT, String.valueOf(port))
                .addArgument(dbName);

        Command dumpCommand = commandBuilder.build();

        getLog().info("Run command: " + dumpCommand.toString());
        ProcessBuilder pb = new ProcessBuilder(dumpCommand.asStringArray());
        pb.directory(baseDir);

        pb.redirectOutput(ProcessBuilder.Redirect.to(outputFile));

        try {
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                String error = IOUtils.toString(p.getErrorStream(), ENC_UTF8);
                String errorMessage = exec + " " + MSG_EXIT_CODE + exitCode;
                getLog().error(errorMessage);
                throw new MojoExecutionException(errorMessage + "\n" + error);
            }
            getLog().info("Dump created successfully: " + outputFile.getAbsolutePath());
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("An exception occurred during running the mysqldump.sh command", e);
        }
    }
}
