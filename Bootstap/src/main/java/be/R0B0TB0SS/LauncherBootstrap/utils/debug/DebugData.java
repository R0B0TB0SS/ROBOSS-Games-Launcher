package be.R0B0TB0SS.LauncherBootstrap.utils.debug;

import be.R0B0TB0SS.LauncherBootstrap.distribution.DistributionOS;
import be.R0B0TB0SS.LauncherBootstrap.utils.io.OsCheck;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

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

}
