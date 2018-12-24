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
 * Создает дапм базы данных MySql. Для работы необходимо установленная утилита
 * mysqldump. Для тестирования она заменена мок скриптом mysqldump.sh, который имитирует
 * работу mysqldump.
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


    /**
     * Команда для исполнения mysqldump. Через этот параметр
     * можно подменить выполнение настоящей mysqldump скриптом mysqldump.sh, который имитирует
     * работу mysqldump.
     */
    @Parameter(defaultValue = CMD_DUMP, property = "exec")
    private String exec;


    /**
     * Экранировать названия таблиц и столбцов, например, `tablename`.`colname`
     */
    @Parameter(defaultValue = "true", property = "quoteNames")
    private boolean quoteNames;


    /**
     * Использовать полные выражения вставки.
     */
    @Parameter(defaultValue = "true", property = "completeInsert")
    private boolean completeInsert;


    /**
     * Использовать синтексис INSERT, который позволяет включать
     * несколько списков значений.
     */
    @Parameter(defaultValue = "true", property = "extendedInsert")
    private boolean extendedInsert;


    /**
     * В одной транзакции, только для InnoDB.
     */
    @Parameter(defaultValue = "true", property = "singleTransaction")
    private boolean singleTransaction;


    /**
     * Имя пользователя для подключения к серверу СУБД
     */
    @Parameter(defaultValue = "root", required = true, property = "userName")
    private String userName;


    /**
     * Пароль для подключения к серверу СУБД
     */
    @Parameter(defaultValue = "mysql", required = true, property = "password")
    private String password;


    /**
     * Хост сервера СУБД
     */
    @Parameter(defaultValue = "localhost", required = true, property = "host")
    private String host;


    /**
     * Порт сервера СУБД
     */
    @Parameter(defaultValue = "3306", required = true, property = "port")
    private int port;


    /**
     * Название базы данных
     */
    @Parameter(defaultValue = "database", required = true, property = "dbName")
    private String dbName;


    /**
     * Файл в который запишется дамп
     */
    @Parameter(defaultValue = "dump.sql", property = "outputFile")
    private File outputFile;


    /**
     * Каталог, из которого будет выполняться exec команда
     */
    @Parameter(defaultValue = "${project.basedir}", readonly = true, property = "baseDir")
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
