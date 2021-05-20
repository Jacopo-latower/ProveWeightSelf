package com.example.provehomefragments
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId;
open class Trainings(
        @PrimaryKey var _id: ObjectId = ObjectId(),
        var _partitionkey: String = "",
        var description: String? = null,
        var difficulty: String? = null,
        var duration: Long? = null,
        var imgUrl: String? = null,
        var trainingName: String? = null,
        var videoUrl: String? = null
): RealmObject() {}