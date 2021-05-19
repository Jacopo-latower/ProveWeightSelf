package com.example.provehomefragments

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey
import java.util.Date;
import org.bson.types.ObjectId;

open class Weights(
    @PrimaryKey var _id: ObjectId = ObjectId(),
    var _partitionkey: String = "",
    var date: Date? = null,
    var user_id: String? = null,
    var weight: Double? = null
): RealmObject() {}