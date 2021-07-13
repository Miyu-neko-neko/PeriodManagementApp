package com.example.periodmanagementapp2

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.example.periodmanagementapp2.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm
    lateinit var result: RealmResults<PeriodData>  //データの塊(コレクション)
    lateinit var customAdapter: CustomAdapter
    var itemsList = arrayListOf<ListItem>()
    var Date_Day_str: String = ""  //年月日（曜日）
    var dateDayItem:String = ""
    var yearItem:String = ""
    var monthItem:String = ""

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
        if(today.get(Calendar.YEAR) == 2021) {
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
        //年月日はStringとして、生理周期は前回から何日後であるかを計算してtoStringしてListViewに表示する
        val strYear = yearItem
        val strMonth = monthItem
        val strDate = dateDayItem

        Date_Day_str = strYear + strMonth + strDate

        val PeriodData = realm.createObject(PeriodData::class.java)
        PeriodData.Date_Day = Date_Day_str

        val listView = binding.listView
        //ListViewが空だったら、(Date_Day_str + "周期: 0 日")を表示
        if (listView.isEmpty()){
            itemsList = arrayListOf<ListItem>(
            ListItem(Date_Day_str + "    " + "周期: 0 日")
            )
            customAdapter = CustomAdapter(
                this,
                R.id.item_title,
                itemsList)

                //ListViewに繋げる
            binding.listView.adapter = customAdapter

        } else {
            //以外は、常に0番目に追加していく
            itemsList.add(0,ListItem(Date_Day_str))

            customAdapter = CustomAdapter(
                this,
                R.id.item_title,
                itemsList)

            binding.listView.adapter = customAdapter
        }

        //periodCycle = 計算した日数差異をtoStringにする
        //最初だけ（周期：０日）
        /*val intYear = yearItem.toInt()
        val intMonth = monthItem.toInt()
        val intDate = dateDayItem.toInt()
        val selectedDate = LocalDate.of(intYear, intMonth, intDate)
        //listViewの0番目の日付をもってくる
        val day1_fromListView = LocalDate.of(2021, Month.MAY, 5)
        val diff = ChronoUnit.DAYS.between(selectedDate, day1_fromListView).toString()
        periodCycle = "周期:" + diff + ("日")*/

        realm.commitTransaction()//データベースの使用を終了する

    }

    //ListViewの表示方法
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()

        realm = Realm.getDefaultInstance()  //データベースの使用開始

        //年月日順で表示
        result = realm.where(PeriodData::class.java).findAll().sort("Date_Day", Sort.DESCENDING)

        //取得したデータの行数
        //1行ずつ取り出すために宣言する
        val length = result.size

        for (i in 0..length - 1) {
            //年月日、(周期)、ゴミ箱のイラストボタン
            itemsList.add(ListItem(result[i]!!.Date_Day + "  " + result[i]!!.periodCycle))
        }

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

