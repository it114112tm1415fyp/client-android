package it114112tm1415fyp.com.client_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

public class ShipmentLocationList_Adapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<String> locationList = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    private LayoutInflater mInflater;

    public ShipmentLocationList_Adapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final String item) {
        locationList.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final String item) {
        locationList.add(item);
        sectionHeader.add(locationList.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return locationList.size();
    }

    @Override
    public String getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        int rowType = getItemViewType(position);
        switch (rowType) {
            case TYPE_ITEM:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.choose_location_list_item, null);
                }
//                    TextView tv_num = (TextView) convertView
//                            .findViewById(R.id.user_address_item_num);
                //tv_num.setText("Address " + position + 1);
                TextView tv_address = (TextView) convertView.findViewById(R.id.user_address_item_address);
                tv_address.setText(locationList.get(position));
                break;
            case TYPE_SEPARATOR:
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.listview_section_header, null);
                }
                TextView tv_header = (TextView) convertView.findViewById(R.id.tv_listview_section_header);
                tv_header.setText(locationList.get(position));
                break;
        }
        return convertView;
    }
}