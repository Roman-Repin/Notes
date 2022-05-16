package com.example.notes;

public interface CardSource {
    DataClass dataClass(int position);
    int size();

    CardSource init (CardSourceResponse cardSourceResponse);
    void deleteDataClass(int position);
    void updateDataClass(int position, DataClass dataClass);
    void addDataClass(DataClass dataClass);
    void clearDataClass();
}
