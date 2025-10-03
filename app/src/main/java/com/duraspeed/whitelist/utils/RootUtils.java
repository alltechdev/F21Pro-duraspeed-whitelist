package com.duraspeed.whitelist.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

public class RootUtils {
    private static final String TAG = "RootUtils";

    public static boolean isRootAvailable() {
        return executeCommand("which su") != null;
    }

    public static boolean isRootGranted() {
        return executeCommand("su -c 'id'") != null;
    }

    public static String executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean executeRootCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String executeRootCommandWithOutput(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            process.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean initializeDuraSpeedSystem() {
        // Check if configuration already exists
        String existingConfig = executeRootCommandWithOutput("cat /system/etc/duraspeed/configuration.xml 2>/dev/null");
        if (existingConfig != null && !existingConfig.trim().isEmpty() && existingConfig.contains("<configuration>")) {
            // Configuration already exists and is valid, don't overwrite
            return true;
        }
        
        // Only create/initialize if it doesn't exist or is invalid
        String initCommand = "mount -o remount,rw / && " +
                "mkdir -p /system/etc/duraspeed && " +
                "chown root:root /system/etc/duraspeed && " +
                "chmod 755 /system/etc/duraspeed && " +
                "if [ ! -f /system/etc/duraspeed/configuration.xml ]; then " +
                "echo '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<configuration>\n" +
                "    <PlatformWhitelist>\n" +
                "    </PlatformWhitelist>\n" +
                "</configuration>' > /system/etc/duraspeed/configuration.xml; " +
                "fi && " +
                "chown root:root /system/etc/duraspeed/configuration.xml && " +
                "chmod 644 /system/etc/duraspeed/configuration.xml";
        
        return executeRootCommand(initCommand);
    }

    public static boolean addAppToWhitelist(String packageName) {
        String command = "mount -o remount,rw / && " +
                "sed -i \"/<\\/PlatformWhitelist>/i\\        <App1>" + packageName + "</App1>\" /system/etc/duraspeed/configuration.xml";
        return executeRootCommand(command);
    }

    public static boolean removeAppFromWhitelist(String packageName) {
        String command = "mount -o remount,rw / && " +
                "sed -i \"/<App1>" + packageName + "<\\/App1>/d\" /system/etc/duraspeed/configuration.xml";
        return executeRootCommand(command);
    }

    public static String getWhitelistConfiguration() {
        return executeRootCommandWithOutput("cat /system/etc/duraspeed/configuration.xml 2>/dev/null");
    }

    public static boolean resetWhitelistToEmpty() {
        // This forcefully resets the whitelist to empty, but preserves the structure
        String resetCommand = "mount -o remount,rw / && " +
                "echo '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<configuration>\n" +
                "    <PlatformWhitelist>\n" +
                "    </PlatformWhitelist>\n" +
                "</configuration>' > /system/etc/duraspeed/configuration.xml && " +
                "chown root:root /system/etc/duraspeed/configuration.xml && " +
                "chmod 644 /system/etc/duraspeed/configuration.xml";
        
        return executeRootCommand(resetCommand);
    }
}