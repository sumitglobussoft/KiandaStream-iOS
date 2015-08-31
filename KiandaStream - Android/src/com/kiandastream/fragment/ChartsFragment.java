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

public class ChartsFragment extends Fragment {
	
	Fragment fragment;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    
		View rootView = inflater.inflate(R.layout.browsefragment, container, false);
		
		fragment = new AlbumsFragment();  
		HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
		
		HomeActivity.toolbarspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, final int position, long id) {
						
						try {
							
							if(position==0){
								
								fragment = new AlbumsFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
								
								System.out.println("position 0 vlaue is +++++ "+parent.getItemAtPosition(position));
							}else if(position==1){
								
								fragment = new ArtistsFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
								
								System.out.println("position 1 vlaue is +++++ "+parent.getItemAtPosition(position));
							}else{
								
								fragment = new TopTrackFragment();  
								HomeActivity.fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
								System.out.println("position 2 vlaue is +++++ "+parent.getItemAtPosition(position));
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
