package com.example.rexpos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.rexpos.R;
import com.example.rexpos.models.Category;
import com.example.rexpos.models.Tax;

import java.util.ArrayList;
import java.util.List;


public class TaxSpinnerAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater inflter;
    private List<Tax> mListTax = new ArrayList<>();


    public TaxSpinnerAdapter(Context applicationContext) {
        this.context = applicationContext;
        inflter = (LayoutInflater.from(applicationContext));

    }



    @Override
    public int getCount() {
        return mListTax.size();
    }

    @Override
    public Tax getItem(int position) {
        return mListTax.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflter.inflate(R.layout.spinner_table_item, null);

        Tax mTable = mListTax.get(position);
        TextView mTextTableItemInSpinner = convertView.findViewById(R.id.txtTableName);
        mTextTableItemInSpinner.setText(mTable.tax_code);


        return convertView;
    }

    public void addAllTaxes(List<Tax> mListTable){
        this.mListTax = mListTable;
        notifyDataSetChanged();
    }
}
