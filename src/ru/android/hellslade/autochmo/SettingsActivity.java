package ru.android.hellslade.autochmo;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

        EditTextPreference login = (EditTextPreference)this.findPreference("loginKey");
        login.setSummary(login.getText());
        login.setOnPreferenceChangeListener(PreferenceChangeListener);
        
        /*EditTextPreference factCount = (EditTextPreference)this.findPreference("factCountKey");
        factCount.setSummary(factCount.getText());
        factCount.setOnPreferenceChangeListener(PreferenceChangeListener);*/

	}
	OnPreferenceChangeListener PreferenceChangeListener = new OnPreferenceChangeListener() 
	{
        public boolean onPreferenceChange(Preference preference, Object newValue) 
        {
	      	preference.setSummary((CharSequence)newValue);
        	return true;
        }
    };
}
