package com.example.periodmanagementapp2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.example.periodmanagementapp2.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm
    lateinit var result: RealmResults<PeriodData>  //データの塊(コレクション)
    lateinit var customAdapter: CustomAdapter
    var itemsList = arrayListOf<ListItem>()
    var Date_Day_str: String = ""  //年月日
    var periodCycle_str: String = ""
    var dateDayItem: String = ""
    var yearItem: String = ""
    var monthItem: String = ""
    var int_spn_year: Int = 0
    var int_spn_year_list: Int = 0
    var int_spn_month: Int = 0
    var int_spn_month_list: Int = 0
    var int_spn_date: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //spinner宣言
        val spinnerYear: Spinner = binding.spinnerYear
        val spinnerMonth: Spinner = binding.spinnerMonth
        val spinnerDateDay: Spinner = binding.spinnerDateDay

        //xmlファイルからアイテムの配列を取得
        val items = resources.getStringArray(R.array.month_array)
        val items2 = resources.getStringArray(R.array.year_array)
        val items3 = resources.getStringArray(R.array.date_array)

        //アダプターにアイテム配列を設定
        val Adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        val Adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items2)
        val Adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items3)

        //スピナーにアダプターを設定
        val today = Calendar.getInstance()
        var yearPosition: Int = 0

        spinnerMonth.adapter = Adapter
        spinnerYear.adapter = Adapter2
        spinnerDateDay.adapter = Adapter3

        spinnerYear.setSelection(today.get(Calendar.YEAR))
        if (today.get(Calendar.YEAR) == 2021) {
            yearPosition = 1
            spinnerYear.setSelection(yearPosition)
        }

        spinnerMonth.setSelection(today.get(Calendar.MONTH))
        spinnerDateDay.setSelection(today.get(Calendar.DAY_OF_MONTH))

        //スピナーのセレクトイベント設定
        //https://kotlin.akira-watson.com/android/spinner.html
        spinnerDateDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //parentのspinnerを指定
                //選択されたitemを取得
                dateDayItem = parent.selectedItem as String
                int_spn_date = spinnerDateDay.getSelectedItemPosition()
                Log.v("MainActivity", "int_spn_date= " + int_spn_date + "")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //parentのspinnerを指定
                //選択されたitemを取得
                yearItem = parent.selectedItem as String
                // 選択されているアイテムのIndexを取得
                int_spn_year = spinnerYear.getSelectedItemPosition()
                Log.v("MainActivity", "int_spn_year= " + int_spn_year + "")
                when (int_spn_year) {
                    1 -> {
                        int_spn_year_list = 2021
                    }
                    2 -> {
                        int_spn_year_list = 2022
                    }
                    3 -> {
                        int_spn_year_list = 2023
                    }
                    4 -> {
                        int_spn_year_list = 2024
                    }
                    5 -> {
                        int_spn_year_list = 2025
                    }
                }
                Log.v("MainActivity", "int_spn_year_list= " + int_spn_year_list + "")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //parentのspinnerを指定
                //選択されたitemを取得
                monthItem = parent.selectedItem as String

                int_spn_month = spinnerMonth.getSelectedItemPosition()
                Log.v("MainActivity", "int_spn_month= " + int_spn_month + "")

                when (int_spn_month) {
                    0 -> {//0は1月
                        int_spn_month_list = 1
                    }
                    1 -> {
                        int_spn_month_list = 2
                    }
                    2 -> {
                        int_spn_month_list = 3
                    }
                    3 -> {
                        int_spn_month_list = 4
                    }
                    4 -> {
                        int_spn_month_list = 5
                    }
                    5 -> {
                        int_spn_month_list = 6
                    }
                    6 -> {
                        int_spn_month_list = 7
                    }
                    7 -> {
                        int_spn_month_list = 8
                    }
                    8 -> {
                        int_spn_month_list = 9
                    }
                    9 -> {
                        int_spn_month_list = 10
                    }
                    10 -> {
                        int_spn_month_list = 11
                    }
                    11 -> {
                        int_spn_month_list = 12
                    }
                }
                Log.v("MainActivity", "int_spn_month_list= " + int_spn_month_list + "")

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.buttonRegister.setOnClickListener(this)
        binding.listView.setOnItemLongClickListener(this)

    }


    //登録ボタンを押すとスピナーで選択した日付がカスタムしたレイアウトで表示される
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {

        realm.beginTransaction()//データベースの使用開始
        //年月日はString
        val strYear = yearItem
        val strMonth = monthItem
        val strDate = dateDayItem

        Date_Day_str = strYear + strMonth + strDate
        Log.v("MainActivity", "Date_Day_str= " + Date_Day_str + "")

        val intYear = int_spn_year_list
        val intMonth = int_spn_month_list
        val intDate = int_spn_date
        var SelectedDateFromList: String = ""

        //スピナーで選択した値
        val selectedDateFromSpn = LocalDate.of(intYear, intMonth, intDate)  //2021-9-15
        Log.v("MainActivity", "SelectedDateFromSpn= " + selectedDateFromSpn + "")

        //初回はspinnerで選択した日付をもってくる
        //次回からは常にlistViewの0番目の日付をもってくる
        val listView = binding.listView
        if (listView.isEmpty()) {
            SelectedDateFromList = Date_Day_str //例：2021年08月15日
        } else {
            SelectedDateFromList = result[0]!!.Date_Day
        }
        Log.v("MainActivity", "SelectedDateFromList= " + SelectedDateFromList + "")



        val array = SelectedDateFromList.split("年", "月", "日")//2021, 8, 15,
        Log.v("MainActivity", "array= " + array + "")

        //substringで何番目から何番目の文字を取り出すか入力
        //endIndexで指定されたインデックスの文字は含まれない
        val intYearFromList = array[0].toInt()

        val intMonthFromList = array[1].toInt()

        val intDateFromList = array[2].toInt()
        Log.v("MainActivity", "intYearFromList= " + intYearFromList + "")
        Log.v("MainActivity", "intMonthFromList= " + intMonthFromList + "")

        val day1_fromListView = LocalDate.of(intYearFromList, intMonthFromList, intDateFromList)
        Log.v("MainActivity", "day1_fromListView= " + day1_fromListView + "")

        if (selectedDateFromSpn < day1_fromListView) { //spinnerの年月日がlistViewの年月日よりも小さかったらエラー
            Log.v("MainActivity", "ERROR！すでに入力済みです")
            AlertDialog.Builder(this)
                .setTitle("ERROR！")
                .setMessage("すでに入力済みです")
                .setPositiveButton("OK") { dialog, which -> }
                .show()
            realm.commitTransaction()
            return
        }

        /*try {
            if (selectedDateFromSpn < day1_fromListView) { //Realm is already in a write transaction
                throw IllegalArgumentException("エラー: すでに入力済みです")
            }
        } catch (e: Exception) {
            System.err.println("エラー: すでに入力済みです")
            return
        }*/

        var PeriodData = realm.createObject(PeriodData::class.java)
        PeriodData.Date_Day = Date_Day_str

        val diff = ChronoUnit.DAYS.between(day1_fromListView, selectedDateFromSpn).toString()
        periodCycle_str = ("周期:" + diff + "日")
        PeriodData.periodCycle = periodCycle_str
        Log.v("MainActivity", "periodCycle_str= " + periodCycle_str + "")

        //常に0番目に追加していく
        itemsList.add(0, ListItem(Date_Day_str + "    " + periodCycle_str))

        customAdapter = CustomAdapter(
            this,
            R.id.item_title,
            itemsList
        )

        binding.listView.adapter = customAdapter

        realm.commitTransaction()//データベースの使用を終了する

    }


    //ListViewの表示方法
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()

        realm = Realm.getDefaultInstance()  //データベースの使用開始

        //年月日順で表示
        result = realm.where(PeriodData::class.java).findAll().sort("Date_Day", Sort.DESCENDING)
        Log.v("MainActivity", "result " + result + "")

        //取得したデータの行数
        //1行ずつ取り出すために宣言する
        val length = result.size

        for (i in 0..length -1){
            itemsList.add(ListItem(result[i]!!.Date_Day + "    " + result[i]!!.periodCycle))
        }

            /*result.forEach {
            //年月日、周期、ゴミ箱のイラストボタン
         itemsList.add(ListItem(it.Date_Day + "    " + it.periodCycle))
        }*/

        customAdapter = CustomAdapter(this, R.id.item_title, itemsList)

        binding.listView.adapter = customAdapter

    }


    override fun onPause() {
        super.onPause()
        realm.close()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onItemLongClick(
        parent: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ): Boolean {
        //選択した行番号が格納されている
        val selectDB = result[position]!!
        //AlertDialog.Builderのインスタンス化
        val dialog = AlertDialog.Builder(this@MainActivity).apply {
            setTitle(("『"+ selectDB.Date_Day +"』") + "の削除")
            setMessage("削除しても良いですか？")
            setPositiveButton("yes") {
                //Yesボタン押した時の処理
                    dialog, which ->

                //長押しした行のデータの削除
                realm.beginTransaction()
                selectDB.deleteFromRealm()
                realm.commitTransaction()

                //長押しした行のリストの削除
                itemsList.removeAt(position)
                //画面更新、アダプターの再接続
                binding.listView.adapter = customAdapter
            }
            setNegativeButton("no") {
                    dialog, which ->

            }
            show()
        }

        return true
    }

}

