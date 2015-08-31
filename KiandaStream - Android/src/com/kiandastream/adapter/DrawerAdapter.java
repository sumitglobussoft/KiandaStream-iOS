package com.kiandastream.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kiandastream.R;
import com.kiandastream.model.Items;
import com.kiandastream.utils.Item;
import com.kiandastream.utils.SectionItem;


/**
 * Created by d4ddy-lild4rk on 11/8/14.
 */
public class DrawerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Item> navDrawerItems;
    private LayoutInflater inflater;
    public DrawerAdapter(Context context, ArrayList<Item> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       /* if (convertView == null) 
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView text = (TextView) convertView.findViewById(R.id.item_text);

        image.setImageResource(navDrawerItems.get(position).getIcon());
        text.setText(navDrawerItems.get(position).getTitle());*/
    	final Item i = navDrawerItems.get(position);
		if (i != null) 
		{
			if (i.isSection()) 
			{
				SectionItem si = (SectionItem) i;
				convertView = inflater.inflate(R.layout.list_item_section, null);

				convertView.setOnClickListener(null);
				convertView.setOnLongClickListener(null);
				convertView.setLongClickable(false);

				final TextView sectionView = (TextView) convertView.findViewById(R.id.list_item_section_text);
				sectionView.setText(si.getTitle());
				sectionView.setTextSize(12);
				

			} else 
			{
				
				Items drawerItem = (Items) i;
				 convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
				 ImageView image = (ImageView) convertView.findViewById(R.id.item_icon);
			     TextView text = (TextView) convertView.findViewById(R.id.item_text);
			     image.setImageResource(drawerItem.getIcon());
			        text.setText(drawerItem.getTitle());
			}
		}

		Animation animation = null;
		animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
		convertView.startAnimation(animation);
        return convertView;
    }
}
