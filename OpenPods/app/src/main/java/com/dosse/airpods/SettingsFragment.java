package com.dosse.airpods;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import static com.dosse.airpods.AboutActivity.donateURL;
import static com.dosse.airpods.AboutActivity.fdroidURL;
import static com.dosse.airpods.AboutActivity.githubURL;
import static com.dosse.airpods.AboutActivity.websiteURL;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Context context;

    @SuppressWarnings("FieldCanBeLocal")
    private Preference mTextonlyPreference, mHideAppPreference, mRestartServicePreference, mAboutPreference, mFDroidPreference, mWebsitePreference, mGithubPreference, mDonationPreference;

    @Override
    public void onCreatePreferences (Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_screen, rootKey);
        context = getContext();

        mHideAppPreference = getPreferenceManager().findPreference("hideApp");
        assert mHideAppPreference != null;
        mHideAppPreference.setOnPreferenceClickListener(preference -> {
            PackageManager p = requireContext().getPackageManager();
            p.setComponentEnabledSetting(new ComponentName(context, MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Toast.makeText(context, getString(R.string.hideClicked), Toast.LENGTH_LONG).show();

            try {
                context.openFileOutput("hidden", Context.MODE_PRIVATE).close();
            } catch (Throwable ignored) {
            }

            enableDisableOptions();
            requireActivity().finish();
            return true;
        });

        mTextonlyPreference = getPreferenceManager().findPreference("textonly");
        assert mTextonlyPreference != null;
        mTextonlyPreference.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O); // Only show the option on Oreo and higher
        //noinspection DanglingJavadoc
        mTextonlyPreference.setOnPreferenceClickListener(preference -> {
            /**
             * This code is for displaying the popup window to explain to the user how to turn on text only mode.
             */
            @SuppressLint("InflateParams") View pwView = LayoutInflater.from(getActivity()).inflate(R.layout.textonly_popup, null);
            PopupWindow pw = new PopupWindow(pwView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            pw.setElevation(5.0f);
            pw.setAnimationStyle(R.style.PopupWindowAnimation);
            pw.showAtLocation(requireActivity().findViewById(R.id.settings_container), Gravity.CENTER, 0, 0);

            pwView.findViewById(R.id.positive).setOnClickListener(v -> {
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName())
                            .putExtra(Settings.EXTRA_CHANNEL_ID, "AirPods");
                }
                startActivityForResult(intent, 0);
            });
            pwView.findViewById(R.id.negative).setOnClickListener(v -> pw.dismiss());
            return true;
        });

        mRestartServicePreference = getPreferenceManager().findPreference("restart");
        assert mRestartServicePreference != null;
        mRestartServicePreference.setOnPreferenceClickListener(preference -> {
            Starter.restartPodsService(requireContext());
            return true;
        });

        mAboutPreference = getPreferenceManager().findPreference("about");
        assert mAboutPreference != null;
        mAboutPreference.setSummary(String.format("%s v%s", getString(R.string.app_name), BuildConfig.VERSION_NAME));
        mAboutPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(context, AboutActivity.class));
            return true;
        });

        mFDroidPreference = getPreferenceManager().findPreference("fdroid");
        assert mFDroidPreference != null;
        mFDroidPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fdroidURL)));
            return true;
        });

        mGithubPreference = getPreferenceManager().findPreference("github");
        assert mGithubPreference != null;
        mGithubPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(githubURL)));
            return true;
        });

        mWebsitePreference = getPreferenceManager().findPreference("website");
        assert mWebsitePreference != null;
        mWebsitePreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL)));
            return true;
        });

        mDonationPreference = getPreferenceManager().findPreference("donate");
        assert mDonationPreference != null;
        mDonationPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(donateURL)));
            Toast.makeText(getContext(), "❤️", Toast.LENGTH_SHORT).show();
            return true;
        });

        enableDisableOptions();
    }

    private void enableDisableOptions () {
        try {
            context.openFileInput("hidden").close();
            mHideAppPreference.setEnabled(false);
        } catch (Throwable ignored) {
        }
    }


    //region Google rant

    /**
     * Google get your shit together,
     * Why can't I just restart the notification when I restart the service
     * And why I can't have a normal way to kill the app
     * WHY I NEED TO FUCKING CALL A KILLPROCESS ON THE APP!!!
     *      I'M MAD!!! >:(
     *
     * - Electric1447
     */
    @Override
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        android.os.Process.killProcess(android.os.Process.myPid()); // This is a fucking retarded hacky workaround
    }

    //endregion

}