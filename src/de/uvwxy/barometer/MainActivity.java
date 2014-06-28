package de.uvwxy.barometer;

import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.uvwxy.cardpager.ActivityCardPager;
import de.uvwxy.cardpager.FragmentAbout;

public class MainActivity extends ActivityCardPager {
	public static MainActivity dhis;
	
	FragmentBarometer fBarometer;
	FragmentAbout fAbout;
	FragmentSettings fSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dhis = this;
	}
	
	@Override
	public Fragment getFragment(int position) {

		switch (position) {
		case 1:
			if (fSettings == null) {
				fSettings = new FragmentSettings();
			}
			return fSettings;

		case 2:
			if (fAbout == null) {
				fAbout = new FragmentAbout();
				fAbout.setTitle(getApplication().getText(R.string.app_name).toString());
                fAbout.setPackageName("de.uvwxy.barometer");
                fAbout.setMarketUrl("market://search?q=de.uvwxy.barometer&c=apps");
                fAbout.setAboutApp(getString(R.string.app_description));
                fAbout.setLicenses(new String[] { "lombok" });
			}
			return fAbout;

		default:
			if (fBarometer == null) {
				fBarometer = new FragmentBarometer();
			}
			return fBarometer;
		}
	}

	@Override
	public CharSequence getFragmentTitle(int position) {
		Locale l = Locale.getDefault();

		switch (position) {
		case 1:
			return "Settings".toUpperCase(l);
		case 2:
			return "About".toUpperCase(l);
		default:
			return "Barometer".toUpperCase(l);
		}

	}

	@Override
	public int getFragmentCount() {
		return 3;
	}

}
