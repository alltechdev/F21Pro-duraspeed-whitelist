package com.duraspeed.whitelist;

import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duraspeed.whitelist.adapters.AppListAdapter;
import com.duraspeed.whitelist.models.AppInfo;
import com.duraspeed.whitelist.utils.ConfigManager;
import com.duraspeed.whitelist.utils.RootUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AppListAdapter.OnAppActionListener {

    private RecyclerView recyclerView;
    private AppListAdapter adapter;
    private TabLayout tabLayout;
    private CircularProgressIndicator progressIndicator;
    private TextView statusText;
    private FloatingActionButton fabRefresh;
    private TextInputLayout searchLayout;
    private TextInputEditText searchEditText;

    private List<AppInfo> allApps;
    private List<String> whitelistedPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupRecyclerView();
        setupTabLayout();
        setupFloatingActionButton();

        // Check root access and initialize system
        new InitializeSystemTask().execute();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        progressIndicator = findViewById(R.id.progressIndicator);
        statusText = findViewById(R.id.statusText);
        fabRefresh = findViewById(R.id.fabRefresh);
        searchLayout = findViewById(R.id.searchLayout);
        searchEditText = findViewById(R.id.searchEditText);
        
        // Set up toolbar with menu
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        allApps = new ArrayList<>();
        adapter = new AppListAdapter(allApps, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean showWhitelistedOnly = tab.getPosition() == 0;
                adapter.setShowWhitelistedOnly(showWhitelistedOnly);
                
                // Show search only in "All Apps" tab (position 1)
                if (tab.getPosition() == 1) {
                    searchLayout.setVisibility(View.VISIBLE);
                } else {
                    searchLayout.setVisibility(View.GONE);
                    searchEditText.setText(""); // Clear search when switching tabs
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Not used
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Not used
            }
        });

        // Setup search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void setupFloatingActionButton() {
        fabRefresh.setOnClickListener(v -> {
            new LoadAppsTask().execute();
        });
    }

    private void showLoading(boolean show, String message) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        statusText.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_reboot) {
            showRebootConfirmation();
            return true;
        } else if (id == R.id.action_reset) {
            showResetConfirmation();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showRebootConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_reboot_title))
                .setMessage(getString(R.string.confirm_reboot_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    new RebootTask().execute();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_reset_title))
                .setMessage(getString(R.string.confirm_reset_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    new ResetWhitelistTask().execute();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    @Override
    public void onAddToWhitelist(AppInfo appInfo) {
        new AddToWhitelistTask().execute(appInfo);
    }

    @Override
    public void onRemoveFromWhitelist(AppInfo appInfo) {
        new RemoveFromWhitelistTask().execute(appInfo);
    }

    private class InitializeSystemTask extends AsyncTask<Void, Void, Boolean> {
        private String errorMessage = "";

        @Override
        protected void onPreExecute() {
            showLoading(true, getString(R.string.initializing_system));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Check root access
                if (!RootUtils.isRootAvailable()) {
                    errorMessage = getString(R.string.root_required);
                    return false;
                }

                if (!RootUtils.isRootGranted()) {
                    errorMessage = getString(R.string.root_denied);
                    return false;
                }

                // Initialize system configuration
                if (!ConfigManager.initializeSystemIfNeeded()) {
                    errorMessage = "Failed to initialize system configuration";
                    return false;
                }

                return true;
            } catch (Exception e) {
                errorMessage = "Error: " + e.getMessage();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                showLoading(false, getString(R.string.root_granted));
                new LoadAppsTask().execute();
            } else {
                showLoading(false, errorMessage);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LoadAppsTask extends AsyncTask<Void, Void, List<AppInfo>> {

        @Override
        protected void onPreExecute() {
            showLoading(true, getString(R.string.searching_apps));
        }

        @Override
        protected List<AppInfo> doInBackground(Void... voids) {
            List<AppInfo> apps = new ArrayList<>();
            PackageManager pm = getPackageManager();

            try {
                // Get whitelisted packages
                whitelistedPackages = ConfigManager.getWhitelistedApps();

                // Get all installed applications
                List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

                for (ApplicationInfo appInfo : installedApps) {
                    // Skip system apps (optional - you can remove this filter if needed)
                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }

                    try {
                        String appName = pm.getApplicationLabel(appInfo).toString();
                        String packageName = appInfo.packageName;
                        
                        AppInfo app = new AppInfo(appName, packageName, pm.getApplicationIcon(appInfo));
                        app.setWhitelisted(whitelistedPackages.contains(packageName));
                        apps.add(app);
                    } catch (Exception e) {
                        // Skip apps that can't be processed
                        continue;
                    }
                }

                // Sort apps by name
                Collections.sort(apps, new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo a1, AppInfo a2) {
                        return a1.getAppName().compareToIgnoreCase(a2.getAppName());
                    }
                });

            } catch (Exception e) {
                // Return empty list on error
                apps.clear();
            }

            return apps;
        }

        @Override
        protected void onPostExecute(List<AppInfo> apps) {
            if (apps.isEmpty()) {
                showLoading(false, getString(R.string.no_apps_found));
            } else {
                showLoading(false, "Found " + apps.size() + " apps");
                allApps = apps;
                adapter.updateAppList(allApps);
                
                // Set initial filter based on current tab (first tab is whitelisted apps)
                int selectedTabPosition = tabLayout.getSelectedTabPosition();
                boolean showWhitelistedOnly = selectedTabPosition == 0;
                adapter.setShowWhitelistedOnly(showWhitelistedOnly);
            }
        }
    }

    private class AddToWhitelistTask extends AsyncTask<AppInfo, Void, Boolean> {
        private AppInfo targetApp;

        @Override
        protected Boolean doInBackground(AppInfo... apps) {
            targetApp = apps[0];
            return ConfigManager.addAppToWhitelist(targetApp.getPackageName());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                targetApp.setWhitelisted(true);
                adapter.updateAppWhitelistStatus(targetApp.getPackageName(), true);
                Toast.makeText(MainActivity.this, getString(R.string.app_added_to_whitelist), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_adding_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RemoveFromWhitelistTask extends AsyncTask<AppInfo, Void, Boolean> {
        private AppInfo targetApp;

        @Override
        protected Boolean doInBackground(AppInfo... apps) {
            targetApp = apps[0];
            return ConfigManager.removeAppFromWhitelist(targetApp.getPackageName());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                targetApp.setWhitelisted(false);
                adapter.updateAppWhitelistStatus(targetApp.getPackageName(), false);
                Toast.makeText(MainActivity.this, getString(R.string.app_removed_from_whitelist), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.error_removing_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RebootTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoading(true, getString(R.string.rebooting_device));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return RootUtils.executeRootCommand("reboot");
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                showLoading(false, getString(R.string.reboot_failed));
                Toast.makeText(MainActivity.this, getString(R.string.reboot_failed), Toast.LENGTH_SHORT).show();
            }
            // If reboot succeeds, the device will restart so we won't reach here
        }
    }

    private class ResetWhitelistTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            showLoading(true, getString(R.string.resetting_whitelist));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return ConfigManager.resetWhitelistToDefaults();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                showLoading(false, getString(R.string.reset_complete));
                Toast.makeText(MainActivity.this, getString(R.string.reset_complete), Toast.LENGTH_SHORT).show();
                // Reload the app list to reflect changes
                new LoadAppsTask().execute();
            } else {
                showLoading(false, getString(R.string.reset_failed));
                Toast.makeText(MainActivity.this, getString(R.string.reset_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }
}