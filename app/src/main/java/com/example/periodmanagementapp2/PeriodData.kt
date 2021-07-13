package com.example.periodmanagementapp2

import io.realm.RealmObject

open class PeriodData: RealmObject() {

    open var Date_Day: String = "" //年月日（曜日）
    open var periodCycle: String = "" //周期●日

}