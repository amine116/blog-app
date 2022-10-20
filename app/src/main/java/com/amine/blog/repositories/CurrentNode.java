package com.amine.blog.repositories;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class CurrentNode {

    private DataSnapshot snapshot;
    private DatabaseReference reference;

    public CurrentNode(){}

    public CurrentNode(DataSnapshot snapshot, DatabaseReference reference) {
        this.snapshot = snapshot;
        this.reference = reference;
    }

    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public void setReference(DatabaseReference reference) {
        this.reference = reference;
    }
}
