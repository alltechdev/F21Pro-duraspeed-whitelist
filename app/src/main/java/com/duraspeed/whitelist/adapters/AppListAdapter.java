package com.duraspeed.whitelist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duraspeed.whitelist.R;
import com.duraspeed.whitelist.models.AppInfo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private List<AppInfo> appList;
    private List<AppInfo> filteredAppList;
    private OnAppActionListener listener;
    private boolean showWhitelistedOnly = false;

    public interface OnAppActionListener {
        void onAddToWhitelist(AppInfo appInfo);
        void onRemoveFromWhitelist(AppInfo appInfo);
    }

    public AppListAdapter(List<AppInfo> appList, OnAppActionListener listener) {
        this.appList = appList != null ? appList : new ArrayList<>();
        this.filteredAppList = new ArrayList<>(this.appList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = filteredAppList.get(position);
        holder.bind(appInfo);
    }

    @Override
    public int getItemCount() {
        return filteredAppList.size();
    }

    public void updateAppList(List<AppInfo> newAppList) {
        this.appList = newAppList != null ? newAppList : new ArrayList<>();
        filterList();
    }

    public void setShowWhitelistedOnly(boolean showWhitelistedOnly) {
        this.showWhitelistedOnly = showWhitelistedOnly;
        filterList();
    }

    public void updateAppWhitelistStatus(String packageName, boolean isWhitelisted) {
        for (AppInfo app : appList) {
            if (app.getPackageName().equals(packageName)) {
                app.setWhitelisted(isWhitelisted);
                break;
            }
        }
        filterList();
    }

    public void filter(String searchText) {
        filterList(searchText);
    }

    private void filterList() {
        filterList("");
    }

    private void filterList(String searchText) {
        filteredAppList.clear();
        String searchLower = searchText.toLowerCase().trim();
        
        for (AppInfo app : appList) {
            boolean matchesSearch = searchLower.isEmpty() || 
                    app.getAppName().toLowerCase().contains(searchLower) || 
                    app.getPackageName().toLowerCase().contains(searchLower);
            
            boolean matchesTab = !showWhitelistedOnly || app.isWhitelisted();
            
            if (matchesSearch && matchesTab) {
                filteredAppList.add(app);
            }
        }
        notifyDataSetChanged();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView appIcon;
        private TextView appName;
        private TextView packageName;
        private MaterialButton actionButton;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            packageName = itemView.findViewById(R.id.packageName);
            actionButton = itemView.findViewById(R.id.actionButton);
        }

        public void bind(AppInfo appInfo) {
            appName.setText(appInfo.getAppName());
            packageName.setText(appInfo.getPackageName());
            appIcon.setImageDrawable(appInfo.getAppIcon());

            if (appInfo.isWhitelisted()) {
                actionButton.setText(R.string.remove_from_whitelist);
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRemoveFromWhitelist(appInfo);
                    }
                });
            } else {
                actionButton.setText(R.string.add_to_whitelist);
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAddToWhitelist(appInfo);
                    }
                });
            }
        }
    }
}