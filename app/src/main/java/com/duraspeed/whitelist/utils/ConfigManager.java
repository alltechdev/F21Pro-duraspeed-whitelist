package com.duraspeed.whitelist.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigManager {
    private static final String TAG = "ConfigManager";

    public static List<String> getWhitelistedApps() {
        List<String> whitelistedApps = new ArrayList<>();
        String config = RootUtils.getWhitelistConfiguration();
        
        if (config != null && !config.isEmpty()) {
            Pattern pattern = Pattern.compile("<App1>([^<]+)</App1>");
            Matcher matcher = pattern.matcher(config);
            
            while (matcher.find()) {
                String packageName = matcher.group(1);
                if (packageName != null && !packageName.trim().isEmpty()) {
                    whitelistedApps.add(packageName.trim());
                }
            }
        }
        
        return whitelistedApps;
    }

    public static boolean isAppWhitelisted(String packageName) {
        List<String> whitelistedApps = getWhitelistedApps();
        return whitelistedApps.contains(packageName);
    }

    public static boolean addAppToWhitelist(String packageName) {
        if (isAppWhitelisted(packageName)) {
            return true; // Already whitelisted
        }
        return RootUtils.addAppToWhitelist(packageName);
    }

    public static boolean removeAppFromWhitelist(String packageName) {
        if (!isAppWhitelisted(packageName)) {
            return true; // Not in whitelist
        }
        return RootUtils.removeAppFromWhitelist(packageName);
    }

    public static boolean initializeSystemIfNeeded() {
        String config = RootUtils.getWhitelistConfiguration();
        
        // If we can read a valid configuration, we're good to go
        if (config != null && !config.trim().isEmpty() && 
            config.contains("<configuration>") && config.contains("<PlatformWhitelist>")) {
            return true; // Already initialized and valid
        }
        
        // Only initialize if no valid config exists
        return RootUtils.initializeDuraSpeedSystem();
    }

    public static boolean resetWhitelistToDefaults() {
        return RootUtils.resetWhitelistToEmpty();
    }
}