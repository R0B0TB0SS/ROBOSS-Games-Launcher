package be.R0B0TB0SS.LauncherBootstrap.utils.debug;

public class Debugger {
    public static DebugData debugData() {
        DebugData debugData = new DebugData();
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
        return debugData;
    }
}
