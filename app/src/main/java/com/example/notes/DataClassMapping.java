package com.example.notes;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataClassMapping {


    public static class Fields {
        public final static String NAME_NOTE = "nameNote";
        public final static String NOTE_DESCRIPTION = "noteDescription";
        public final static String DATE = "date";
    }

    public static DataClass toDataClass (String id, Map<String, Object> doc) {
        Timestamp timestamp = (Timestamp) doc.get(Fields.DATE);
        DataClass answer = new DataClass(
                (String) doc.get(Fields.NAME_NOTE),
                (String) doc.get(Fields.NOTE_DESCRIPTION),
                timestamp.toDate());
        answer.setId(id);
        return answer;
    }

    public static Object toDocument(DataClass dataClass) {
        Map<String, Object> result = new HashMap<>();
        result.put(Fields.NAME_NOTE, dataClass.getNameNote());
        result.put(Fields.NOTE_DESCRIPTION, dataClass.getNoteDescription());
        result.put(Fields.DATE, new Timestamp(dataClass.getDate()));
        return result;
    }
}
