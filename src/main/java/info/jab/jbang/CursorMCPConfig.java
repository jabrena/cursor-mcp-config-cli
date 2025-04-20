///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.7.6
//DEPS com.github.lalyos:jfiglet:0.0.9
//DEPS com.diogonunes:JColor:5.5.1

package info.jab.jbang;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.concurrent.Callable;

import com.diogonunes.jcolor.Attribute;
import static com.diogonunes.jcolor.Ansi.colorize;

import com.github.lalyos.jfiglet.FigletFont;

enum OperatingSystem {
    LINUX, MACOS, OTHER;

    public static OperatingSystem current() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            return MACOS;
        } else if (osName.contains("linux")) {
            return LINUX;
        } else {
            return OTHER;
        }
    }
}

@Command(
    name = "CursorMCPConfig", 
    version = "CursorMCPConfig 1.0",
    description = "Manages Cursor MCP configuration file (mcp.json).",
    mixinStandardHelpOptions = true,
    usageHelpAutoWidth = true
)
public class CursorMCPConfig implements Callable<Integer> {

    private static final String CURSOR_CONFIG_DIR = ".cursor";
    private static final String MCP_FILENAME = "mcp.json";
    private static final String BACKUP_FILENAME = "mcp.bk.json";

    @Option(
        names = "--backup", 
        description = "Backup the current mcp.json to mcp.bk.json in the current directory.")
    private boolean backupRequested;

    @Option(
        names = "--replace", 
        paramLabel = "FILE", 
        description = "Replace the current mcp.json with the specified file.")
    private File replacementFile;

    @Option(
        names = "--show",
        description = "Show the content of the current '~/.cursor/mcp.json'.")
    private boolean showRequested;

    @Override
    public Integer call() throws Exception {

        printBanner();

        verifyOS();

        Path userHome = Paths.get(System.getProperty("user.home"));
        Path cursorConfigPath = userHome.resolve(CURSOR_CONFIG_DIR);
        Path mcpSourcePath = cursorConfigPath.resolve(MCP_FILENAME);

        // Validate mutually exclusive options
        int optionsCount = (backupRequested ? 1 : 0) + (replacementFile != null ? 1 : 0) + (showRequested ? 1 : 0);
        if (optionsCount > 1) {
            System.err.println("Error: --backup, --replace, and --show options are mutually exclusive.");
            return 1;
        }

        if (showRequested) {
            return show(mcpSourcePath);
        } else if (backupRequested) {
            return backup(mcpSourcePath);
        } else if (!Objects.isNull(replacementFile)) {
            return replace(replacementFile.toPath(), mcpSourcePath);
        } else {
            CommandLine.usage(this, System.out);
            return 1;
        }
    }

    private void printBanner() throws IOException {
        System.out.println();
        String asciiArt = FigletFont.convertOneLine("Cursor MCP Config");
        System.out.println(colorize(asciiArt, Attribute.GREEN_TEXT()));
    }

    private void verifyOS() {
        OperatingSystem currentOS = OperatingSystem.current();
        if (currentOS == OperatingSystem.OTHER) {
            System.err.println("Error: This tool only supports Linux and macOS. Detected: " + System.getProperty("os.name"));
            System.exit(1);
        }
        System.out.println("Detected OS: " + currentOS);
    }

    private int backup(Path sourcePath) {
        Path destinationPath = Paths.get(BACKUP_FILENAME);
        System.out.println("Backing up " + sourcePath + " to " + destinationPath.toAbsolutePath());
        try {
            if (Files.exists(sourcePath)) {
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Backup successful.");
                return 0;
            } else {
                System.err.println("Error: Source file for backup does not exist: " + sourcePath);
                return 1;
            }
        } catch (IOException e) {
            System.err.println("Error during backup: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private int replace(Path sourcePath, Path destinationPath) {
        if (!Files.exists(sourcePath)) {
            System.err.println("Error: Replacement file not found: " + sourcePath.toAbsolutePath());
            return 1;
        }
        System.out.println("Replacing " + destinationPath + " with " + sourcePath.toAbsolutePath());
        try {
            // Ensure parent directory exists
            Files.createDirectories(destinationPath.getParent());
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Replacement successful.");
            return 0;
        } catch (IOException e) {
            System.err.println("Error during replacement: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private int show(Path sourcePath) {
        if (!Files.exists(sourcePath)) {
            System.err.println("Error: MCP configuration file not found at " + sourcePath);
            return 1;
        }

        System.out.println("Content of " + sourcePath + ":");
        try {
            Files.lines(sourcePath).forEach(System.out::println);
            return 0;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new CursorMCPConfig()).execute(args);
        System.exit(exitCode);
    }
}