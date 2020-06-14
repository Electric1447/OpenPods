package com.dosse.airpods;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import static com.dosse.airpods.AboutActivity.donateURL;
import static com.dosse.airpods.AboutActivity.githubURL;
import static com.dosse.airpods.AboutActivity.websiteURL;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate (Bundle icicle) {
        super.onCreate(icicle);
        getPreferenceManager().setSharedPreferencesName("openpods");
        addPreferencesFromResource(R.xml.preference_screen);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Hide app listener
        findPreference("hideApp").setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.hide_dialog);
            builder.setMessage(R.string.hide_dialog_desc);
            builder.setPositiveButton(R.string.hide_dialog_button, (dialog, which) -> {
                getPackageManager().setComponentEnabledSetting(new ComponentName(SettingsActivity.this, MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                Toast.makeText(this, getString(R.string.hideClicked), Toast.LENGTH_LONG).show();

                try {
                    openFileOutput("hidden", Context.MODE_PRIVATE).close();
                } catch (Throwable ignored) {
                }

                enableDisableOptions();
                finish();
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

        // About listener. Removing or hiding this is a violation of the GPL license
        findPreference("about").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
            return true;
        });

        findPreference("github").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(githubURL)));
            return true;
        });

        findPreference("website").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL)));
            return true;
        });

        findPreference("donate").setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(donateURL)));
            Toast.makeText(this, "❤️", Toast.LENGTH_SHORT).show();
            return true;
        });

        enableDisableOptions();
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("batterySaver"))
            Starter.restartPodsService(getApplicationContext());
    }

    private void enableDisableOptions () {
        try {
            getApplicationContext().openFileInput("hidden").close();
            findPreference("hideApp").setEnabled(false);
        } catch (Throwable ignored) {
        }
    }

}
