package org.wikipedia.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.wikipedia.R;
import org.wikipedia.WikipediaApp;
import org.wikipedia.history.DeleteAllHistoryTask;
import org.wikipedia.search.DeleteAllRecentSearchesTask;


public class SettingsFragment extends PreferenceLoaderFragment {

    private WikipediaApp app;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void loadPreferences() {
        SettingsPreferenceLoader preferenceLoader = new SettingsPreferenceLoader(this);
        preferenceLoader.loadPreferences();
        setupRememberHistoryButton(findPreference(getString(R.string.preference_key_remember_history)));
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        prepareDeveloperSettingsMenuItem(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.developer_settings:
                launchDeveloperSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchDeveloperSettingsActivity() {
        startActivity(DeveloperSettingsActivity.newIntent(getActivity()));
    }

    private void prepareDeveloperSettingsMenuItem(Menu menu) {
        menu.findItem(R.id.developer_settings).setVisible(Prefs.isShowDeveloperSettingsEnabled());
    }

    private void invalidateOptionsMenu() {
        getFragmentManager().invalidateOptionsMenu();
    }

    private void setupRememberHistoryButton(Preference button) {
        button.setOnPreferenceChangeListener(rememberHistoryChangeListener());
    }

    private Preference.OnPreferenceChangeListener rememberHistoryChangeListener() {
        return new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if  (!(Boolean) newValue)  {
                    app = (WikipediaApp) getActivity().getApplicationContext();
                    new DeleteAllHistoryTask(app).execute();
                    new DeleteAllRecentSearchesTask(app).execute();
                }
                return true;
            }
        };
    }

}
