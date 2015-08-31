package com.kiandastream.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.kiandastream.HomeActivity;
import com.kiandastream.R;

public class BrowseFragment extends Fragment {

	Fragment fragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    
		View rootView = inflater.inflate(R.layout.browsefragment, container, false);
		
		fragment = new ArtistsFragment();  
		HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
		
		HomeActivity.toolbarspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, final int position, long id) {
						
						try {
							
							if(position==0){
								
								fragment = new ArtistsFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
								
							}else if(position==1){
								
								fragment = new FeaturedAlbumFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
								
							}else{
								
								fragment = new ExploreFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						Toast.makeText(getActivity(), ""+parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
					}
					
		});
		return rootView;
	}

}
