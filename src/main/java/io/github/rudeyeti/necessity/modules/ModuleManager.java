package io.github.rudeyeti.necessity.modules;

import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;

import java.util.*;

public class ModuleManager {
    public static void initialize() {
        modules().forEach((module, dependency) -> {
            Map<Boolean, String> isEnabled = isEnabled(module, dependency);

            if (isEnabled.containsKey(false)) {
                String message = isEnabled.get(false);

                if (message != null) {
                    Necessity.logger.severe(message);
                }
            } else {
                Necessity.logger.info(isEnabled.get(true));
            }
        });
    }

    public static Map<String, List<Object>> modules() {
        Map<String, List<Object>> hashMap = new HashMap<>();

        hashMap.put("Integration", Arrays.asList("Vault", Plugins.getVault()));
        hashMap.put("Whitelist", Arrays.asList("", ""));
        hashMap.put("Activity", Arrays.asList("CoreProtect", Plugins.getCoreProtect()));
        hashMap.put("Schematics", Arrays.asList("WorldEdit", Plugins.getWorldEdit()));
        hashMap.put("Status", Arrays.asList("", ""));
        return hashMap;
    }

    public static Map<Boolean, String> isEnabled(String module) {
        return isEnabled(module, modules().get(module));
    }

    public static Map<Boolean, String> isEnabled(String module, List<Object> dependency) {
        Map<Boolean, String> hashMap = new HashMap<>();
        boolean isEnabled = Config.config.getBoolean(module.toLowerCase());
        String message = isEnabled ? dependency.get(1) != null ? "The module " + module + " has been successfully enabled." :
                "Usage: The module " + module + " cannot be loaded because it depends on " + dependency.get(0) + "." : null;

        hashMap.put(isEnabled, message);
        return hashMap;
    }
}
