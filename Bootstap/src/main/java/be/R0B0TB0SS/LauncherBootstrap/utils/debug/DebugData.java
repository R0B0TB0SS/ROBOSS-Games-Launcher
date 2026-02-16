package be.R0B0TB0SS.LauncherBootstrap.utils.debug;


import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionOS;
import be.R0B0TB0SS.LauncherBootstrap.utils.io.OsCheck;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DebugData {
    private final DistributionOS distributionOS;

    private final String os;

    private final String javaVersion;

    private final String appData;

    private final String cpu;

    private final String arch;

    private final String gpu;

    private final String ram;

    private final long date;
    private String date2;

    public DebugData() {
        OperatingSystemMXBean bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.distributionOS = OsCheck.getOperatingSystemType();
        this.os = System.getProperty("os.name", "generic");
        this.javaVersion = System.getProperty("java.version");
        this.appData = OsCheck.getAppData().getAbsolutePath();
        this.cpu = System.getenv("PROCESSOR_IDENTIFIER");
        this.arch = System.getProperty("sun.arch.data.model");
        this.gpu = System.getenv("DISPLAY");
        this.ram = bean.getTotalMemorySize() / 1024L / 1024L / 1024L + "G";

        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SS");
            String strDate = sdf.format(cal.getTime());
            SimpleDateFormat sdf1 = new SimpleDateFormat();
            sdf1.applyPattern("dd/MM/yyyy HH:mm:ss:SS");
            Date date = sdf1.parse(strDate);
            this.date2 = sdf1.format(date);
        }catch (Exception ignored){}

        this.date = System.currentTimeMillis();

    }

    public DistributionOS getDistributionOS() {
        return this.distributionOS;
    }

    public String getOs() {
        return this.os;
    }

    public String getJavaVersion() {
        return this.javaVersion;
    }


    public String getAppData() {
        return this.appData;
    }

    public String getCpu() {
        return this.cpu;
    }

    public String getArch() {
        return this.arch;
    }

    public String getGpu() {
        return this.gpu;
    }

    public String getRam() {
        return this.ram;
    }

    public long getDate() {
        return this.date;
    }

    public String getDate2() {
        return this.date2;
    }

}
