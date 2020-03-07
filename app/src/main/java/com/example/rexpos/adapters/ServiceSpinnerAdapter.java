package com.example.rexpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rexpos.R;
import com.example.rexpos.models.Service;

import java.util.ArrayList;
import java.util.List;


public class ServiceSpinnerAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflter;
    private List<Service> mListService = new ArrayList<>();


    public ServiceSpinnerAdapter(Context applicationContext) {
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));

    }



    @Override
    public int getCount() {
        return mListService.size();
    }

    @Override
    public Service getItem(int position) {
        return mListService.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflter.inflate(R.layout.spinner_table_item, null);

        Service mTable = mListService.get(position);
        TextView mTextTableItemInSpinner = convertView.findViewById(R.id.txtTableName);
        mTextTableItemInSpinner.setText(mTable.service_key);


        return convertView;
    }

    public void addAllServices(List<Service> mListTable){
        this.mListService = mListTable;
        notifyDataSetChanged();
    }
}
