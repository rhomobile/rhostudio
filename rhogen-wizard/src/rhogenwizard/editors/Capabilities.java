package rhogenwizard.editors;

import java.util.ArrayList;
import java.util.List;
import rhogenwizard.PlatformType;

public class Capabilities {

    private static ArrayList<Capabilities> capabilityList = new ArrayList<Capabilities>();

    public String publicId;
    public PlatformType platformId;

    private Capabilities(String publicName, PlatformType platform) {
        platformId = platform;
        publicId = publicName;
    }

    public static String[] getPublicIds() {
        return getPublicIdsList().toArray(new String[0]);
    }

    public static List<String> getPublicIdsList() {
        List<String> list = new ArrayList<String>();

        for (Capabilities capability : capabilityList) {
            if (capability.publicId != null) {
                list.add(capability.publicId);
            }
        }
        return list;
    }

    public static List<String> getPublicIdsList(List<Capabilities> capabList) {
        List<String> list = new ArrayList<String>();

        for (Capabilities pt : capabList) {
            if (pt.publicId != null) {
                list.add(pt.publicId);
            }
        }
        return list;
    }

    public static List<Capabilities> getCapabilitiesList(List<String> capabList) {
        List<Capabilities> list = new ArrayList<Capabilities>();
        defaultCapabilityList();
        if (capabList != null) {
            for (String pt : capabList) {
                list.add(Capabilities.fromId(pt));
            }
        }
        return list;
    }

    // Create default device capability list
    public static void defaultCapabilityList() {

        if (capabilityList.size() > 0)
            capabilityList.clear();

        capabilityList.add(new Capabilities("gps", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("pim", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("camera", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("vibrate", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("phone", PlatformType.eUnknown));
        capabilityList
                .add(new Capabilities("bluetooth", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("calendar", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("non_motorola_device",
                PlatformType.eUnknown));
        capabilityList.add(new Capabilities("native_browser",
                PlatformType.eUnknown));
        capabilityList.add(new Capabilities("motorola_browser",
                PlatformType.eUnknown));
        capabilityList.add(new Capabilities("hardware_acceleration",
                PlatformType.eAndroid));
        capabilityList.add(new Capabilities("push", PlatformType.eUnknown));
        capabilityList.add(new Capabilities("network_state",
                PlatformType.eUnknown));
        capabilityList.add(new Capabilities("sdcard", PlatformType.eUnknown));

    }

    public static Capabilities fromId(String id) {
        for (Capabilities capability : capabilityList) {
            if (id.equals(capability.publicId)) {
                return capability;
            }
        }

        // this is new capability, add it to the list and show it in capability
        // dialog
        Capabilities UnknownCapability = new Capabilities(id,
                PlatformType.eUnknown);
        capabilityList.add(UnknownCapability);
        return UnknownCapability;
    }

    @Override
    public String toString() {
        return publicId;
    }

}