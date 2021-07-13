package com.example.periodmanagementapp2

import android.app.Application
import android.widget.ArrayAdapter
import io.realm.Realm

// app/java/一番上のフォルダ上で右クリック
//New→Kotlin File/Class を選択
//classを選択してenter
class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        //Realmの初期化をする
        Realm.init(this)
    }

}