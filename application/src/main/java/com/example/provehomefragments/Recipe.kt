package com.example.provehomefragments

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import org.bson.types.ObjectId;
open class Recipe(
    @PrimaryKey var _id: ObjectId = ObjectId(),
    var _partitionkey: String = "",
    var description: String? = null,
    var imgUrl: String? = null,
    @Required
    var ingredients: RealmList<String> = RealmList(),
    var kcal: Long? = null,
    var recipeName: String? = null
): RealmObject() {}
