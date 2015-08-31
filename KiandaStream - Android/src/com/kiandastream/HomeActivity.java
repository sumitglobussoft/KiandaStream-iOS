package com.kiandastream;


import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.Profile;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kiandastream.adapter.DrawerAdapter;
import com.kiandastream.fragment.AlbumsFragment;
import com.kiandastream.fragment.BrowseFragment;
import com.kiandastream.fragment.ChartsFragment;
import com.kiandastream.fragment.CreatePlaylistFragment;
import com.kiandastream.fragment.FavoritesFragment;
import com.kiandastream.fragment.FollowFragment;
import com.kiandastream.fragment.New_ReleaseFragment;
import com.kiandastream.fragment.PlayListFragment;
import com.kiandastream.fragment.ProfileFragment;
import com.kiandastream.fragment.SettingFragment;
import com.kiandastream.fragment.TrendingFragment;
import com.kiandastream.model.Items;
import com.kiandastream.model.MainSingleTon;
import com.kiandastream.model.PlayingSongListmodel;
import com.kiandastream.musicplayer.MusicService;
import com.kiandastream.musicplayer.PlayerUpdate;
import com.kiandastream.utils.ImageLoader;
import com.kiandastream.utils.Item;
import com.kiandastream.utils.MultiSwipeRefreshLayout;
import com.kiandastream.utils.SectionItem;

public class HomeActivity extends ActionBarActivity implements MultiSwipeRefreshLayout.CanChildScrollUpCallback,PlayerUpdate 
{
	/*Initialize all the variable */
	Handler handler ;
	public static ActionBar actiobar;
	public static Menu mMenu;
	
	// Spinner
	public static Spinner toolbarspinner;
	public static ImageView dashimage;
	
	private String[] mDrawerTitles;
	private TypedArray mDrawerIcons;
	private ArrayList<Item> drawerItems;
	private DrawerLayout mDrawerLayout;
	RelativeLayout addAccountRlt, settingRlt, feedbackRlt;
	public static LinearLayout bottonplayer;
	private ListView mDrawerList_Left;
	ImageView player_songimage,player_pause,userimage;
	TextView player_songname,player_artistname;
	private ActionBarDrawerToggle mDrawerToggle;
	public static FragmentManager fragmentManager;//
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	boolean doubleBackToExitPressedOnce;
	ImageLoader imageloader;
	Toolbar toolbar;
	
	// SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
	private MultiSwipeRefreshLayout mSwipeRefreshLayout;
	private ViewGroup headerRight;
	private ViewGroup footerRight;
	
	String currentUserEmailId = null;
	Profile fBprofile;

	ImageView pCoverPic;
	TextView userName,mainuname;
	TextView userEmailId ;
	ImageView profilePic;
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageloader=new ImageLoader(getApplicationContext());
		fragmentManager = HomeActivity.this.getSupportFragmentManager();
		handler = new Handler();
		
		// ToolBar initialization
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) 
			setSupportActionBar(toolbar);
		// Remove default title of toolbox	
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		// Custom title of toolbox
		textView=(TextView) toolbar.findViewById(R.id.toolbartitle);
		// spinner in toolbox
		toolbarspinner = (Spinner) findViewById(R.id.toolbarspinner);
		dashimage = (ImageView) findViewById(R.id.dashimage);
	    
		mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
		mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_icons);
		player_artistname=(TextView)findViewById(R.id.artistname);
		player_songname=(TextView)findViewById(R.id.songname);
		player_pause=(ImageView)findViewById(R.id.player_pause_btn);
		player_songimage=(ImageView)findViewById(R.id.songimage);
		drawerItems = new ArrayList<Item>();
		
		MainSingleTon.mp = new MediaPlayer();
		mDrawerList_Left = (ListView) findViewById(R.id.left_drawer);
		setItem();
		bottonplayer=(LinearLayout)findViewById(R.id.linearLayout2);
		bottonplayer.setVisibility(View.GONE);
		
		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /** host Activity */
				mDrawerLayout,         /** DrawerLayout object */
				toolbar,                 /** nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /** "open drawer" description */
				R.string.drawer_close  /** "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) 
			{
				super.onDrawerClosed(view);
				textView.setText(mTitle);
			
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				
				textView.setText(mDrawerTitle);

			}
		};
		// initialize actionbar
		actiobar=getSupportActionBar();
		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Adding header and footer view to navigation list
		LayoutInflater inflater = getLayoutInflater();
		headerRight = (ViewGroup) inflater.inflate(R.layout.header,mDrawerList_Left, false);
		userimage=(ImageView) headerRight.findViewById(R.id.userimage);
		mainuname=(TextView) headerRight.findViewById(R.id.mainusername);
		userName=(TextView) headerRight.findViewById(R.id.username);
		userName.setText(MainSingleTon.username);
		mainuname.setText(MainSingleTon.mainusername);
		
		// TODO
		//imageloader.DisplayImage("", userimage);
		
		footerRight = (ViewGroup) inflater.inflate(R.layout.footer, mDrawerList_Left, false);
		settingRlt=(RelativeLayout)footerRight.findViewById(R.id.setting_rl);
	    settingRlt.setOnClickListener(new OnClickListener() 
	    {
			@Override
			public void onClick(View v) 
			{
				toolbarspinner.setVisibility(View.GONE);
				dashimage.setVisibility(View.GONE);
				
				Fragment fragment = new SettingFragment();
				fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
		
		mDrawerList_Left.addHeaderView(headerRight, null, false); // true = clickable
		mDrawerList_Left.addFooterView(footerRight, null, false); // true = clickable

		userimage.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				toolbarspinner.setVisibility(View.GONE);
				dashimage.setVisibility(View.GONE);
				
				setTitle("Profile");
				Fragment fragment = new ProfileFragment();
				fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
		//Set width of drawer
		DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mDrawerList_Left.getLayoutParams();
		lp.width = calculateDrawerWidth();
		mDrawerList_Left.setLayoutParams(lp);

		//Set width of drawer
		// Set the adapter for the list view
		mDrawerList_Left.setAdapter(new DrawerAdapter(getApplicationContext(), drawerItems));
		// Set the list's click listener
		mDrawerList_Left.setOnItemClickListener(new LeftDrawerItemClickListener());
		selectItem(2);

	}
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
		//trySetupSwipeRefresh();
	}

	void setItem()
	{
		//drawerItems.add(new SectionItem("Browse"));
		
		drawerItems.add(new Items(mDrawerTitles[0], mDrawerIcons.getResourceId(0, -1)));
		drawerItems.add(new Items(mDrawerTitles[1], mDrawerIcons.getResourceId(1, -1)));
		drawerItems.add(new Items(mDrawerTitles[2], mDrawerIcons.getResourceId(2, -1)));
		drawerItems.add(new Items(mDrawerTitles[3], mDrawerIcons.getResourceId(3, -1)));
		drawerItems.add(new Items(mDrawerTitles[4], mDrawerIcons.getResourceId(4, -1)));
		drawerItems.add(new Items(mDrawerTitles[5], mDrawerIcons.getResourceId(5, -1)));
		drawerItems.add(new SectionItem("PlayList"));
		drawerItems.add(new Items(mDrawerTitles[6], mDrawerIcons.getResourceId(7, -1)));
		drawerItems.add(new Items(mDrawerTitles[7], mDrawerIcons.getResourceId(8, -1)));
		drawerItems.add(new Items(mDrawerTitles[8], mDrawerIcons.getResourceId(9, -1)));
		
		
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onBackPressed()	
	{					

		if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
		{
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
		}
		else
		{
			if(MainSingleTon.previous_fragment.isEmpty())
			{
				showAlertDialog();
				//finish();
			}else
			{
				if(MainSingleTon.previous_fragment.equals("home"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Home");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="home";
					Fragment fragment = new BrowseFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("trending"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Trending");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="trending";
					Fragment fragment = new TrendingFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("album"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Album");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="album";
					Fragment fragment = new AlbumsFragment(); 
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("charts"))
				{
					toolbarspinner.setVisibility(View.VISIBLE);
					dashimage.setVisibility(View.VISIBLE);
					
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
			                R.array.chartsoptions, R.layout.toolbar_simple_spinneritem);
			        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			        toolbarspinner.setAdapter(adapter);
			        
					setTitle("Charts");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="charts";
					Fragment fragment = new ChartsFragment(); 
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				
				//TODO 
				if(MainSingleTon.previous_fragment.equals("browse"))
				{
					toolbarspinner.setVisibility(View.VISIBLE);
					dashimage.setVisibility(View.VISIBLE);
					
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
			                R.array.browseoptions, R.layout.toolbar_simple_spinneritem);
			        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
			        toolbarspinner.setAdapter(adapter);
			        
					setTitle("Browse");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="browse";
					Fragment fragment = new BrowseFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("follow"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Follow");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="follow";
					Fragment fragment = new FollowFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("createplaylist"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Create Playlist");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="createplaylist";
					Fragment fragment = new CreatePlaylistFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("playlist"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Playlist");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="playlist";
					Fragment fragment = new PlayListFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				if(MainSingleTon.previous_fragment.equals("favorites"))
				{
					toolbarspinner.setVisibility(View.GONE);
					dashimage.setVisibility(View.GONE);
					
					setTitle("Favorites");
					MainSingleTon.previous_fragment="";
					MainSingleTon.current_fragment="favorites";
					Fragment fragment = new FavoritesFragment();
					fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
				}
				
			}
		}
	}

	/**
	 * Swaps fragments in the main content view
	 */
	private void selectItem(int position)
	{
		Fragment fragment = null;
		System.out.println("selected position "+position);
		switch (position)
		{
		/* <item>Treanding</item>
	        <item>New Release</item>
	        <item>Artist</item>
	        <item>Albums</item>
	        <item>Top Chart</item>
	        <item>Radio</item>
	        <item>Browse</item>
	        <item>Friend Find</item>
	        <item>Create New</item>
	        <item>Create Playlist</item>
	        <item>Playlist</item>*/
		case 0:
			/*fragment = new MusicPlayer_Fragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();*/
			break;
		case 1:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Home");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="home";
			fragment = new BrowseFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;

		case 2:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Trending");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="trending";
			fragment = new TrendingFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;

		case 3:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("New Release");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="newrelease";
			fragment = new New_ReleaseFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;
		case 4:
			// TODO
			
			toolbarspinner.setVisibility(View.VISIBLE);
			dashimage.setVisibility(View.VISIBLE);
			
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(toolbar.getContext(),
	                R.array.chartsoptions, R.layout.toolbar_simple_spinneritem);
	        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
	        toolbarspinner.setAdapter(adapter);
	        
			setTitle("Charts");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="charts";
			fragment = new ChartsFragment(); 
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;

		case 5:
			
			toolbarspinner.setVisibility(View.VISIBLE);
			dashimage.setVisibility(View.VISIBLE);
			
			ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(toolbar.getContext(),
	                R.array.browseoptions, R.layout.toolbar_simple_spinneritem);
	        adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
	        toolbarspinner.setAdapter(adapter1);
	        
			setTitle("Browse");
			fragment = new BrowseFragment();
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="browse";
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;
		

		case 6:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Follow");
			fragment = new FollowFragment();
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="follow";
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;
			
		case 7:
			/*setTitle("Follow");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="follow";
			fragment = new FollowFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();*/
			break;
		case 8:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Create Playlist");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="createplaylist";
			fragment = new CreatePlaylistFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;
		case 9:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Playlist");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="playlist";
			fragment = new PlayListFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;
		case 10:
			
			toolbarspinner.setVisibility(View.GONE);
			dashimage.setVisibility(View.GONE);
			
			setTitle("Favorities");
			MainSingleTon.previous_fragment="";
			MainSingleTon.current_fragment="favorites";
			fragment = new PlayListFragment();
			fragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit();
			break;

		}

		// Highlight the selected item, update the title, and close the drawer
		if(mDrawerList_Left.isEnabled())
		{
			mDrawerList_Left.setItemChecked(position, true);

			//setTitle(mDrawerTitles[position]);
			//			updateView(position, position, true,mDrawerList_Left);

			mDrawerLayout.closeDrawer(mDrawerList_Left);
			
		}
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		textView.setText(mTitle.toString());
	}

	public int calculateDrawerWidth()
	{
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
		}

		Display display = getWindowManager().getDefaultDisplay();
		int width;
		int height;
		if (android.os.Build.VERSION.SDK_INT >= 13) 
		{
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else
		{
			width = display.getWidth();  // deprecated
			height = display.getHeight();  // deprecated
		}
		return width - actionBarHeight;
	}

	

	@Override
	public boolean canSwipeRefreshChildScrollUp()
	{
		return false;
	}

	private class LeftDrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			selectItem(position);
		}
	}

	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleIn(View view) 
	{

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the final radius for the clipping circle
		int finalRadius = Math.max(view.getWidth(), view.getHeight());

		// create the animator for this view (the start radius is zero)
		Animator anim =  ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

		// make the view visible and start the animation
		view.setVisibility(View.VISIBLE);
		anim.start();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void circleOut(final View view)
	{

		// get the center for the clipping circle
		int cx = (view.getLeft() + view.getRight()) / 2;
		int cy = (view.getTop() + view.getBottom()) / 2;

		// get the initial radius for the clipping circle
		int initialRadius = view.getWidth();

		// create the animation (the final radius is zero)
		Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

		// make the view invisible when the animation is done
		anim.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation) 
			{
				super.onAnimationEnd(animation);
				view.setVisibility(View.INVISIBLE);
			}
		});

		// start the animation
		anim.start();
	}
	
	
	@Override
	public void updatedata() 
	{
		if(player_artistname!=null)
		{
			PlayingSongListmodel model=MusicService.getSongdetail();
			player_artistname.setText(model.getSong_artist());
			player_songname.setText(model.getSong_name());
			imageloader.DisplayImage(model.getSong_image(), player_songimage);
		}
		
	}
	@Override
	public void showplayer(boolean status) 
	{
		if(status)
		{
			bottonplayer.setVisibility(View.VISIBLE);
		}else
		{
			bottonplayer.setVisibility(View.GONE);
		}
		
	}
	
	public void showAlertDialog() {
		  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		  alertDialogBuilder.setMessage("Are you Sure Want To Quit");
		  alertDialogBuilder.setPositiveButton(R.string.yes,
		    new DialogInterface.OnClickListener() {

		     @Override
		     public void onClick(DialogInterface arg0, int arg1) {
		     HomeActivity.this.finish(); 
		     }
		    });
		  alertDialogBuilder.setNegativeButton(R.string.no,
		    new DialogInterface.OnClickListener() {

		     @Override
		     public void onClick(DialogInterface dialog, int which) {
		    	 
		     }
		    });

		  AlertDialog alertDialog = alertDialogBuilder.create();
		  alertDialog.show();

		 }
}
