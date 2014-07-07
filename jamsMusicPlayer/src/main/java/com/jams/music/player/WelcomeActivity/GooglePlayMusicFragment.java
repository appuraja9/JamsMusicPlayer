package com.jams.music.player.WelcomeActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.jams.music.player.R;
import com.jams.music.player.AsyncTasks.AsyncGoogleMusicAuthenticationTask;
import com.jams.music.player.Helpers.TypefaceHelper;
import com.jams.music.player.Helpers.UIElementsHelper;
import com.jams.music.player.Utils.Common;

public class GooglePlayMusicFragment extends Fragment {

	private Context mContext;
	private Common mApp;

	private Account account;
	private RadioGroup radioGroup;
	private TextView welcomeHeader;
	private TextView welcomeText1;
	private TextView googlePlayMusicDisclaimer;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext =  getActivity().getApplicationContext();
		mApp = (Common) mContext;
		View rootView = (View) inflater.inflate(R.layout.fragment_welcome_screen_5, null);
		
		welcomeHeader = (TextView) rootView.findViewById(R.id.welcome_header);
		welcomeHeader.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Thin"));
        welcomeHeader.setPaintFlags(welcomeHeader.getPaintFlags() 
        						   | Paint.ANTI_ALIAS_FLAG
        						   | Paint.SUBPIXEL_TEXT_FLAG);
		
		welcomeText1 = (TextView) rootView.findViewById(R.id.welcome_text_1);
		welcomeText1.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Light"));
        welcomeText1.setPaintFlags(welcomeText1.getPaintFlags() 
        						   | Paint.ANTI_ALIAS_FLAG
        						   | Paint.SUBPIXEL_TEXT_FLAG);
        
		googlePlayMusicDisclaimer = (TextView) rootView.findViewById(R.id.google_play_music_disclaimer);
		googlePlayMusicDisclaimer.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Light"));
        googlePlayMusicDisclaimer.setPaintFlags(googlePlayMusicDisclaimer.getPaintFlags() 
        						   | Paint.ANTI_ALIAS_FLAG
        						   | Paint.SUBPIXEL_TEXT_FLAG);
        
        radioGroup = (RadioGroup) rootView.findViewById(R.id.google_play_music_radio_group);
        
        final AccountManager accountManager = AccountManager.get(getActivity().getApplicationContext());
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final int size = accounts.length+1; //We're adding 1 here to account (no pun intended) for the extra "Don't use Google Play Music" option.
        
        final RadioButton[] radioButton = new RadioButton[size];
        
        //Add a new radio button the group for each username.
        for (int i=0; i < size; i++) {
        	radioButton[i] = new RadioButton(getActivity());
        	radioGroup.addView(radioButton[i]);
        	
        	//The first radio button will always be "Don't use Google Play Music".
        	if (i==0) {
        		radioButton[i].setChecked(true);
        		radioButton[i].setText(R.string.dont_use_google_play_music);
        	} else {
        		radioButton[i].setText(accounts[i-1].name);
        	}
        	
        	radioButton[i].setTag(i);
        	radioButton[i].setTextColor(UIElementsHelper.getTextColor(mContext));
        	radioButton[i].setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));
        	radioButton[i].setPaintFlags(radioButton[i].getPaintFlags()
        								 | Paint.ANTI_ALIAS_FLAG
        								 | Paint.SUBPIXEL_TEXT_FLAG);
        	
        }
        
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				int radioButtonID = group.getCheckedRadioButtonId();
				View radioButton = group.findViewById(radioButtonID);
				int index = group.indexOfChild(radioButton);
				
				if (index!=0) {
					
					account = accounts[index-1];
					mApp.getSharedPreferences()
						.edit()
						.putString("GOOGLE_PLAY_MUSIC_ACCOUNT", account.name)
						.commit();
					
					AsyncGoogleMusicAuthenticationTask task = new AsyncGoogleMusicAuthenticationTask(mContext, 
																									 getActivity(),
																									 true,
																									 account.name);
					
					task.execute();
				} else {
					mApp.getSharedPreferences().edit().putString("GOOGLE_PLAY_MUSIC_ACCOUNT", "").commit();
					mApp.getSharedPreferences().edit().putBoolean("GOOGLE_PLAY_MUSIC_ENABLED", false).commit();
				}
				
			}
        	
        });
		
		return rootView;
	}
	
}