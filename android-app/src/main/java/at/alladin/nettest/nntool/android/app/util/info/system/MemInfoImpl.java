package at.alladin.nettest.nntool.android.app.util.info.system;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.alladin.nntool.util.tools.MemInfo;
import at.alladin.nntool.util.tools.ToolUtils;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class MemInfoImpl implements MemInfo {

    private final static Pattern MEMINFO_PATTERN = Pattern.compile("([a-zA-Z]*)\\:\\s*([0-9]*)\\s*([a-zA-Z]*)");

    private final static String MEMINFO_PATH = "/proc/meminfo";

    private Map<String, Long> memoryMap;

    public enum MemInfoTypeEnum implements MemInfoType {
        MEMTOTAL("MemTotal"),
        MEMFREE("MemFree"),
        MEMAVAILABLE("MemAvailable"),
        BUFFERS ("Buffers"),
        CACHED ("Cached"),
        SWAPCACHED ("SwapCached"),
        ACTIVE ("Active"),
        INACTIVE ("Inactive"),
        ACTIVE_ANON ("Active(anon)"),
        INACTIVE_ANON ("Inactive(anon)"),
        ACTIVE_FILE ("Active(file)"),
        INACTIVE_FILE ("Inactive(file)"),
        UNEVICTABLE ("Unevictable"),
        MLOCKED ("Mlocked"),
        SWAPTOTAL ("SwapTotal"),
        SWAPFREE ("SwapFree"),
        DIRTY ("Dirty"),
        WRITEBACK ("Writeback"),
        ANONPAGES ("AnonPages"),
        MAPPED ("Mapped"),
        SHMEM ("Shmem"),
        SLAB ("Slab"),
        SRECLAIMABLE ("SReclaimable"),
        SUNRECLAIM ("SUnreclaim"),
        KERNELSTACK ("KernelStack"),
        PAGETABLES ("PageTables"),
        NFS_UNSTABLE ("NFS_Unstable"),
        BOUNCE ("Bounce"),
        WRITEBACKTMP ("WritebackTmp"),
        COMMITLIMIT ("CommitLimit"),
        COMMITTED_AS ("Committed_AS"),
        VMALLOCTOTAL ("VmallocTotal"),
        VMALLOCUSED ("VmallocUsed"),
        VMALLOCCHUNK ("VmallocChunk"),
        HARDWARECORRUPTED ("HardwareCorrupted"),
        ANONHUGEPAGES ("AnonHugePages"),
        SHMEMHUGEPAGES ("ShmemHugePages"),
        SHMEMPMDMAPPED ("ShmemPmdMapped"),
        CMATOTAL ("CmaTotal"),
        CMAFREE ("CmaFree"),
        HUGEPAGES_TOTAL ("HugePages_Total"),
        HUGEPAGES_FREE ("HugePages_Free"),
        HUGEPAGES_RSVD ("HugePages_Rsvd"),
        HUGEPAGES_SURP ("HugePages_Surp"),
        HUGEPAGESIZE ("Hugepagesize"),
        DIRECTMAP4K ("DirectMap4k"),
        DIRECTMAP2M ("DirectMap2M"),
        DIRECTMAP1G ("DirectMap1G");

        private final String key;

        MemInfoTypeEnum(final String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Long getMemoryValue(final MemInfo memInfo) {
            if (memInfo.getMemoryMap() == null) {
                memInfo.update();
                return MemInfo.UNKNOWN;
            }

            return memInfo.getMemoryMap().get(key);
        }
    }

    public Map<String, Long> getMemoryMap() {
        if (memoryMap == null) {
            update();
        }

        return memoryMap;
    }

    public Long getTotalMem() {
        return getValue(MemInfoTypeEnum.MEMTOTAL);
    }

    public Long getFreeMem() {
        return getValue(MemInfoTypeEnum.MEMFREE);
    }

    public Long getValue(final MemInfoType memInfoType) {
        return memInfoType.getMemoryValue(this);
    }

    /**
     * memory usage: 0..1
     * @return
     */
    public float calculateMemoryUsage() {
        final Long totalMem = getTotalMem();
        final Long freeMem = getFreeMem();
        if (totalMem != null && freeMem != null) {
            return (float)freeMem / (float)totalMem;
        }

        return 0f;
    }

    public synchronized void update() {
        memoryMap = new HashMap<>();
        String memInfo = ToolUtils.readFromProc(MEMINFO_PATH);
        Matcher m = MEMINFO_PATTERN.matcher(memInfo);
        while (m.find()) {
            String type = m.group(1);
            Long size;
            try {
                size = Long.parseLong(m.group(2));
            }
            catch (Exception e) {
                size = UNKNOWN;
            }

            memoryMap.put(type, size);
        }
    }

}
