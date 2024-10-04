package be.R0B0TB0SS.LauncherBootstrap.utils.logger;

import be.R0B0TB0SS.LauncherBootstrap.utils.io.OsCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class BootstrapLogger {
    public static void setup() throws FileNotFoundException {
        File LinstallDir = new File(OsCheck.getLocal(), "/programs/ROBOSS-Games");
    if (!LinstallDir.exists())
        LinstallDir.mkdirs();
    File logFile = new File(LinstallDir, "bootstrap.log");
    if (logFile.exists())
        logFile.delete();
    PrintStream out = new PrintStream(new FileOutputStream(logFile, true), true);
    System.setOut(new PrintStream(new FileLoggerOutputStream(out, System.out)));
    System.setErr(new PrintStream(new FileLoggerOutputStream(out, System.err)));
}
}
