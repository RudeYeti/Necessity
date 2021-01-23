package io.github.rudeyeti.necessity.modules;

import github.scarsz.discordsrv.DiscordSRV;
import io.github.rudeyeti.necessity.Config;
import io.github.rudeyeti.necessity.Necessity;
import io.github.rudeyeti.necessity.Plugins;
import io.github.rudeyeti.necessity.modules.integration.Integration;
import io.github.rudeyeti.necessity.modules.status.Status;

import java.util.*;

public class ModuleManager {

    public static Map<String, List<Object>> modules = new LinkedHashMap<String, List<Object>>() {{
        put("Integration", Arrays.asList("Vault", Plugins.getVault()));
        put("Whitelist", null);
        put("Activity", Arrays.asList("CoreProtect", Plugins.getCoreProtect()));
        put("Schematics", Arrays.asList("WorldEdit", Plugins.getWorldEdit()));
        put("Status", null);
    }};

    public static void initialize(boolean logToConsole) {
       modules.forEach((module, dependency) -> {
            Map<Boolean, String> isEnabled = isEnabled(module, dependency);

            if (isEnabled.containsKey(false)) {
                String message = isEnabled.get(false);

                if (message != null && logToConsole) {
                    Necessity.logger.severe(message);
                }
            } else {
                if (logToConsole) {
                    Necessity.logger.info(isEnabled.get(true));
                }

                if (DiscordSRV.isReady) {
                    Integration.initialize();
                    Status.initialize();
                }
            }
        });
    }

    public static Map<Boolean, String> isEnabled(String module) {
        return isEnabled(module, modules.get(module));
    }

    public static Map<Boolean, String> isEnabled(String module, List<Object> dependency) {
        dependency = dependency == null ? Arrays.asList("", "") : dependency;
        Object value = Config.config.get(module.toLowerCase());
        boolean isEnabled = value instanceof Boolean && (Boolean) value;

        String message = isEnabled ? dependency.get(1) != null ? "The module " + module + " has been successfully enabled." :
                "Usage: The module " + module + " cannot be loaded because it depends on " + dependency.get(0) + "." : null;

        return Collections.singletonMap(isEnabled, message);
    }
}
