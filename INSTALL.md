# 📱 Installation Guide

## 🔥 Quick Install

### 1️⃣ **Download APK**
```bash
# Release APK (Recommended)
app-release.apk (9.6MB) - Production ready, signed

# Debug APK (Development)  
app-debug.apk (12MB) - For testing purposes
```

### 2️⃣ **Prerequisites** 
- ✅ **Android 11+** (API Level 30+)
- ✅ **Rooted device** (Magisk/SuperSU)
- ✅ **Unknown sources enabled**

### 3️⃣ **Installation Steps**
1. **Transfer APK** to your Android device
2. **Enable Unknown Sources** in Settings → Security
3. **Install APK** by tapping the file
4. **Grant root permission** when SuperUser prompt appears
5. **Launch app** and start managing your whitelist!

## 🛡️ Root Permission Setup

The app requires root access to:
- Create `/system/etc/duraspeed/` directory
- Write `configuration.xml` file  
- Execute `su -c reboot` command
- Modify system-level whitelist entries

**First Launch:**
1. App will request root access
2. Grant permission in SuperUser/Magisk
3. App initializes system configuration
4. Start adding apps to whitelist

## ⚡ Usage

### **Adding Apps**
- Browse "All Apps" tab
- Tap "Add to Whitelist" on desired apps
- Changes apply immediately

### **Managing Whitelist**  
- View current whitelist in "Whitelisted Apps" tab
- Tap "Remove from Whitelist" to unlist apps
- Use "Reset" button to clear all entries

### **System Operations**
- **Reboot**: Applies changes with device restart
- **Reset**: Clears entire whitelist to empty state

## 🔧 Troubleshooting

**Root Access Denied?**
- Verify device is properly rooted
- Check Magisk/SuperSU is working
- Grant permissions in superuser app

**App Not Starting?**  
- Ensure Android 11+ compatibility
- Install on supported architecture
- Check available storage space

**Permission Errors?**
- Verify `/system` partition is writable
- Check SELinux policies if applicable
- Try rebooting after root grant

---
**Ready to optimize your Android system? Install now!** 🚀