package be.R0B0TB0SS.LauncherBootstrap.utils.games;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class ProcessLogManager extends Thread {
    private final BufferedReader reader;

    private Consumer<String> callback;

    public ProcessLogManager(Process process) {
        this.reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public void run() {
        try {
            String line;
            while ((line = this.reader.readLine()) != null) {
                if (this.callback == null) {
                    System.out.println(line);
                    continue;
                }
                this.callback.accept(line);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            interrupt();
            System.exit(0);
        }
    }
}
