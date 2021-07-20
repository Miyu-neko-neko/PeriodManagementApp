package com.example.periodmanagementapp2

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import io.realm.*
import io.realm.kotlin.deleteFromRealm

// リスト項目のデータ
class ListItem(val title: String) {

    var description: String = "No description"

    constructor(title: String, description: String) : this(title) {
        this.description = description
    }
}

// リスト項目を再利用するためのホルダー
data class ViewHolder(val listViewText: TextView, val deleteIcon: ImageButton)

// 自作のリスト項目データを扱えるようにした ArrayAdapter
class CustomAdapter(context: Context, resource: Int, items: List<ListItem>) :
    ArrayAdapter<ListItem?>(context, resource, items), RealmModel {

    private lateinit var realm: Realm
    lateinit var result: RealmResults<PeriodData>

    //private val mItems: List<ListItem> = items
    @RequiresApi(Build.VERSION_CODES.N)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var viewHolder: ViewHolder? = null
        var view = convertView

        val inflater: LayoutInflater? =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        //listViewが空欄だったら、listviewのレイアウト、item_titleと削除ボタンを置く
        if (view == null) {

            view = inflater!!.inflate(R.layout.listview, parent, false)

            viewHolder = ViewHolder(
                view.findViewById(R.id.item_title),
                view.findViewById(R.id.delete_button)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        // リストビューに表示する要素を取得
        //val item: ViewHolder = mItems[position]

        // 項目の情報を設定
        val listItem = getItem(position)
        viewHolder.listViewText.text = listItem!!.title
        viewHolder.deleteIcon.setOnClickListener { _ ->
            // 削除ボタンをタップしたときの処理
            realm = Realm.getDefaultInstance()
            result = realm.where(PeriodData::class.java).findAll().sort("Date_Day", Sort.DESCENDING)

            val selectDB = result[position]!!
            realm.beginTransaction()
            selectDB.deleteFromRealm()
            realm.commitTransaction()

            this.remove(listItem)
            this.notifyDataSetChanged()
            //realm.close()
        }
        return view!!
    }
}






