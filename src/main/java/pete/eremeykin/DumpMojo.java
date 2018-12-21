package pete.eremeykin;


import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Goal which dumps mysql data base
 */
@Mojo(name = "dump", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DumpMojo extends AbstractMojo {

    private static final String UTF8 = "UTF-8";
    private static final String MSG_EXIT_CODE = "mysqldump command exited with code: ";

    private static final String CMD_DUMP = "mysqldump";
    private static final String ARG_QUOTE_NAMES = "-Q";
    private static final String ARG_COMPLETE_INSERT = "-c";
    private static final String ARG_EXTENDED_INSERT = "-e";
    private static final String ARG_USER = "--user";
    private static final String ARG_PASSWORD = "--password";
    private static final String ARG_SINGLE_TRANSACTION = "--single-transaction";

    @Parameter(defaultValue = "true", property = "quoteNames")
    private boolean quoteNames;

    @Parameter(defaultValue = "true", property = "completeInsert")
    private boolean completeInsert;

    @Parameter(defaultValue = "true", property = "extendedInsert")
    private boolean extendedInsert;


    @Parameter(property = "userName", required = true)
    private String userName;

    @Parameter(property = "password", required = true)
    private String password;

    @Parameter(property = "dbName", required = true)
    private String dbName;

    @Parameter(defaultValue = "true", property = "singleTransaction")
    private boolean singleTransaction;

    @Parameter(defaultValue = "mysqldump", property = "mySqlDump")
    private File outputFile;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true ,required = true)
    private File baseDir;


    public void execute() throws MojoExecutionException {
        Command.Builder commandBuilder = new Command.Builder(CMD_DUMP);

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
                .addArgument(dbName);

        Command dumpCommand = commandBuilder.build();

        ProcessBuilder pb = new ProcessBuilder(dumpCommand.asStringArray());
        pb.directory(baseDir);

        pb.redirectOutput(ProcessBuilder.Redirect.to(outputFile));


        try {
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                StringWriter errorWriter = new StringWriter();
                IOUtils.copy(p.getInputStream(), errorWriter, UTF8);
                String errorMessage = MSG_EXIT_CODE + exitCode;
                getLog().error(errorWriter.toString());
                getLog().error(errorMessage);
                throw new MojoExecutionException(errorMessage);
            }
            getLog().info("Dump created successfully");
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("An exception occurred during running the mysqldump command", e);
        }
    }
}
