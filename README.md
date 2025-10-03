# DuraSpeed Whitelist Manager - Built for Qin F21 Pro

A simple app whitelist manager for rooted Android devices that works with DuraSpeed system configurations.

## What it does

This app lets you manage which apps are included in your DuraSpeed whitelist by editing the `/system/etc/duraspeed/configuration.xml` file directly. If you already have a DuraSpeed configuration, it'll load your existing settings without overwriting anything.

## Features

- **View and manage apps**: See all your installed apps in a clean, searchable list
- **Quick whitelist toggle**: Add or remove apps with a single button press  
- **Safe configuration handling**: Preserves your existing DuraSpeed setup
- **Clean interface**: Dark theme with tabs for "Whitelisted Apps" and "All Apps"
- **Search functionality**: Find apps quickly in the "All Apps" tab
- **System controls**: Built-in reboot and reset options with confirmation dialogs
- **Real-time updates**: Changes are applied immediately to the system configuration

## Requirements

- Rooted device

## Installation

1. Sign the apk
2. Install on your rooted device
3. Grant root permissions when prompted
4. The app will initialize the DuraSpeed configuration if needed

## How it works

The app directly modifies `/system/etc/duraspeed/configuration.xml` to add or remove apps from the whitelist. Each whitelisted app gets an entry like `<App1>com.example.package</App1>`.

When you first open the app, it:
- Checks for existing DuraSpeed configuration
- Loads any current whitelist entries if they exist
- Creates the configuration structure only if it doesn't exist

## Usage

1. **Whitelisted Apps tab**: Shows only apps currently in your whitelist
2. **All Apps tab**: Shows all installed apps with search functionality
3. **Add/Remove buttons**: Toggle apps in and out of the whitelist
4. **Menu options**: 
   - Reboot device to apply changes
   - Reset whitelist to clear all entries


Built for users who need direct control over their DuraSpeed whitelist configuration.
