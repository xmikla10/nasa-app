package com.meteoritelandings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Peter Miklánek
 *
 * Class represent custom adapter for listview
 */

public class CustomAdapterMeteors extends BaseAdapter{

    Context c;
    ArrayList<MeteorsAd> a;

    public CustomAdapterMeteors(Context c, ArrayList<MeteorsAd> a)
    {
        this.c = c;
        this.a = a;
    }
    @Override
    public int getCount() {
        return a.size();
    }
    @Override
    public Object getItem(int position) {
        return a.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        if(convertView==null)
        {
            convertView= LayoutInflater.from(c).inflate(R.layout.item_frame,parent,false);
        }

        TextView meteorName = (TextView) convertView.findViewById(R.id.meteorName);
        TextView meteorYear = (TextView) convertView.findViewById(R.id.meteorYear);
        TextView meteorMass = (TextView) convertView.findViewById(R.id.meteorMass);
        TextView meteorCount = (TextView) convertView.findViewById(R.id.meteorCount);

        final MeteorsAd s= (MeteorsAd) this.getItem(position);

        meteorName.setText(s.getName());
        meteorYear.setText(s.getYear());
        meteorMass.setText(s.getMass() + c.getString(R.string.grams));
        meteorCount.setText(String.valueOf(position+1)+ ".");

        return convertView;
    }

}