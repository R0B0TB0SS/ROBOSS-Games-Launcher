package be.R0B0TB0SS.launcher.utils.debug;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Debugger {
    public static void debugData() {

        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        DebugData debugData = new DebugData();
        System.out.println(date+"[ROBOSS Games Launcher] [DEBUG]:");
        System.out.println("This is a debug data, please send it to the developer if you have a problem.");
        System.out.println("--------------------");
        System.out.println("DOS: " + debugData.getDistributionOS());
        System.out.println("OS: " + debugData.getOs());
        System.out.println("JAVA: " + debugData.getJavaVersion());
        System.out.println("----");
        System.out.println("AppData: " + debugData.getAppData());
        System.out.println("----");
        System.out.println("CPU: " + debugData.getCpu());
        System.out.println("Arch: " + debugData.getArch());
        System.out.println("RAM: " + debugData.getRam());
        System.out.println("----");
        System.out.println("Date: " + debugData.getDate2());
        System.out.println("      " + debugData.getDate());
        System.out.println("--------------------");
    }
}