package com.example.notes;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardSourceFirebaseImpl implements CardSource {

    public static final String CARD_COLLECTION = "card";
    public static final String TAG = "CardSourceFirebaseImpl";

    private final FirebaseFirestore store = FirebaseFirestore.getInstance();
    private final CollectionReference collection = store.collection(CARD_COLLECTION);
    private List<DataClass> dataClasses = new ArrayList<>();

    @Override
    public DataClass dataClass(int position) {
        return dataClasses.get(position);
    }

    @Override
    public int size() {
        return dataClasses.size();
    }

    @Override
    public CardSource init(CardSourceResponse cardSourceResponse) {
        collection
                .orderBy(DataClassMapping.Fields.DATE, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dataClasses = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> doc = document.getData();
                            DataClass dataClass = DataClassMapping.toDataClass(document.getId(), doc);
                            dataClasses.add(dataClass);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }

                    collection.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            dataClasses = new ArrayList<>();

                            for (QueryDocumentSnapshot document : value) {
                                if (document != null) {
                                    Map<String, Object> doc = document.getData();
                                    DataClass dataClass = DataClassMapping.toDataClass(document.getId(), doc);
                                    dataClasses.add(dataClass);
                                }
                            }
                            Log.d(TAG, "Current cites in CA: " + dataClasses);
                        }
                    });
                });
        return this;
    }

    @Override
    public void deleteDataClass(int position) {
        collection.document(dataClasses.get(position).getId()).delete();
        dataClasses.remove(position);
    }

    @Override
    public void updateDataClass(int position, DataClass dataClass) {
        collection.document(dataClasses.get(position).getId()).set(DataClassMapping.toDocument(dataClass));
    }

    @Override
    public void addDataClass(DataClass dataClass) {
        collection.add(DataClassMapping.toDocument(dataClass))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dataClass.setId(documentReference.getId());
                    }
                });
    }

    @Override
    public void clearDataClass() {
        for (DataClass dataClass : dataClasses) {
            collection.document(dataClass.getId()).delete();
        }
        dataClasses.clear();
    }
}
