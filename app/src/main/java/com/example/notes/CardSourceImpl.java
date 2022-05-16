package com.example.notes;

import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CardSourceImpl implements CardSource {

    private final Resources resources;
    private final List<DataClass> dataSource;

    public CardSourceImpl(Resources resources) {
        dataSource = new ArrayList<>();
        this.resources = resources;
    }

    public CardSource init(CardSourceResponse cardSourceResponse) {
        String[] titles = resources.getStringArray(R.array.notes);
        String[] descriptions = resources.getStringArray(R.array.some_description_for_notes);


        if (cardSourceResponse != null) {
            cardSourceResponse.initialized(this);
        }
        return this;
    }

    /*@Override*/
    public void EventChangeListener(CardSourceResponse cardSourceResponse) {

    }

    public List<DataClass> getDataSource() {
        return dataSource;
    }

    @Override
    public DataClass dataClass(int position) {
        return dataSource.get(position);
    }

    @Override
    public int size() {
        return dataSource.size();
    }

    @Override
    public void deleteDataClass(int position) {
        dataSource.remove(position);
    }

    @Override
    public void updateDataClass(int position, DataClass dataClass) {
        dataSource.set(position, dataClass);
    }

    @Override
    public void addDataClass(DataClass dataClass) {
        dataSource.add(dataClass);
    }

    @Override
    public void clearDataClass() {
        dataSource.clear();
    }
}
