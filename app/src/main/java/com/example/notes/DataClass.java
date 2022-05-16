package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class DataClass implements Parcelable {

    private final String nameNote;
    private final String noteDescription;
    private String id;
    private Date date;


    public DataClass(String nameNote, String noteDescription, Date date/*, String id*/) {
        this.nameNote = nameNote;
        this.noteDescription = noteDescription;
        this.date = date;
        /*this.id = id;*/
    }

    protected DataClass(Parcel in) {
        nameNote = in.readString();
        noteDescription = in.readString();
        date = new Date(in.readLong());
    }

    public static final Creator<DataClass> CREATOR = new Creator<DataClass>() {
        @Override
        public DataClass createFromParcel(Parcel in) {
            return new DataClass(in);
        }

        @Override
        public DataClass[] newArray(int size) {
            return new DataClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameNote);
        dest.writeString(noteDescription);
        dest.writeLong(date.getTime());
    }

    public String getNameNote() {
        return nameNote;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public Date getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

