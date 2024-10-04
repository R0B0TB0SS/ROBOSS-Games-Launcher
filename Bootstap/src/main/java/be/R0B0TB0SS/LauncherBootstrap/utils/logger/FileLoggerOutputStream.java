package be.R0B0TB0SS.LauncherBootstrap.utils.logger;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class FileLoggerOutputStream extends OutputStream {
    private final PrintStream out;

    private final OutputStream logger;

    public FileLoggerOutputStream(PrintStream out, OutputStream logger) {
        this.out = out;
        this.logger = logger;
    }

    public void write(int b) throws IOException {
        this.out.write(b);
        this.logger.write(b);
    }

    public void flush() throws IOException {
        super.flush();
        this.out.flush();
        this.logger.flush();
    }

    public void close() throws IOException {
        super.close();
        this.out.close();
    }
}
