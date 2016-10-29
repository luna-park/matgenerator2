package org.lunapark.develop.matgenerator.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.lunapark.develop.matgenerator.R;

import java.util.ArrayList;

/**
 * Created by znak on 25.10.2016.
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<StringField> sFields = new ArrayList<>();
    private Context context;
    private SharedPreferences preferences;
    private String SIZE = "size", DELIMETER = "#";

    public CustomAdapter(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
        loadData();
    }

    @Override
    public int getCount() {
        return sFields.size();
    }

    @Override
    public Object getItem(int pos) {
        return sFields.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

//        view.setClickable(false);
//        view.setFocusable(false);
        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.text1);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position); //or some other task
                sFields.remove(position);
                notifyDataSetChanged();
                saveData();
            }
        });

        return view;
    }

    private String convert(StringField sField) {
        return sField.adj1 + sField.adj2 + " " + sField.noun1 + sField.noun2;
    }

    public void add(StringField stringField) {
        StringField sf = new StringField(stringField.adj1, stringField.adj2, stringField.noun1, stringField.noun2);
        sFields.add(sf);
        list.add(convert(sf));
        notifyDataSetChanged();
        saveData();
    }

    // TODO Save and load data

    private void saveData() {
        SharedPreferences.Editor ed = preferences.edit();
        ed.clear().apply();

        ed.putInt(SIZE, sFields.size());

        for (int i = 0; i < sFields.size(); i++) {
            StringField sField = sFields.get(i);
            String data = sField.adj1 + DELIMETER + sField.adj2 + DELIMETER + sField.noun1 + DELIMETER + sField.noun2;
            ed.putString(String.valueOf(i), data);
        }

        ed.commit();
    }

    private void loadData() {
        int dataSize = preferences.getInt(SIZE, 0);

        if (dataSize > 0) {
            for (int i = 0; i < dataSize; i++) {
                String idx = String.valueOf(i);
                String rawData = preferences.getString(idx, "");
                String[] data = rawData.split(DELIMETER);

                StringField sf = new StringField(data[0], data[1], data[2], data[3]);
//                sf.adj1 = data[0];
//                sf.adj2 = data[1];
//                sf.noun1 = data[2];
//                sf.noun2 = data[3];

                sFields.add(i, sf);
                list.add(i, convert(sf));
            }
        }
    }

}