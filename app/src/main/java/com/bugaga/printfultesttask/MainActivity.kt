package com.bugaga.printfultesttask

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var infoBt : FloatingActionButton
    lateinit var swiper: SwipeRefreshLayout
    lateinit var mainListView: ListView
    lateinit var updateTime: TextView
    var mainDataList: MutableList<CountriesData> = mutableListOf()
    var filtеredDataList: MutableList<CountriesData> = mutableListOf()
    var listAdapter: myAdapter? = null
    var myHandler: Handler = Handler()

    private var requestFailCount = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Coronavirus statistics"
        initUI()
        if (!isOnline(applicationContext)) showErrorDialog("Internet in not available!")
        myHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    0->{
                        if (requestFailCount++ < 3){
                            apiClient(applicationContext,myHandler,mainDataList).getSummery()
                        }
                        else{
                            showErrorDialog("Something wrong with api!")
                            swiper.isRefreshing = false
                        }
                    }
                    1->{
                        filtеredDataList.clear()
                        filtеredDataList.addAll(mainDataList)
                        listAdapter?.notifyDataSetChanged()
                        swiper.isRefreshing = false
                        requestFailCount = 0
                        updateSyncTime()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        apiClient(applicationContext, myHandler, mainDataList).getSummery()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        val search = menu?.findItem(R.id.nav_search)
        val searchView = search?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null){
                    filtеredDataList.clear()
                    filtеredDataList.addAll(mainDataList.filter {
                        it.Country.toLowerCase(Locale.ROOT)
                        .contains(newText.toLowerCase(Locale.ROOT))
                    })
                    listAdapter?.notifyDataSetChanged()
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun showErrorDialog(title:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setPositiveButton("Ok"){d,_->
            d.dismiss()
        }
        builder.show()
    }

    private fun initUI() {
        updateTime = findViewById(R.id.LastSyncTime)
        infoBt = findViewById(R.id.infoFab)
        infoBt.setOnClickListener {
            showInfoDialog()
        }
        swiper = findViewById(R.id.swipeContainer)
        swiper.setOnRefreshListener {
            apiClient(applicationContext,myHandler,mainDataList).getSummery()
        }
        mainListView = findViewById(R.id.ListOfItems)
        mainListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this,CountryInfoActivity::class.java)
                .apply { putExtra("country",filtеredDataList[position]) }
            startActivity(intent)
        }
        listAdapter = myAdapter(applicationContext,filtеredDataList)
        mainListView.adapter = listAdapter
    }

    fun showInfoDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.info_dialog,null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    fun updateSyncTime(){
        val currentDateTime = LocalDateTime.now()
        updateTime.text = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }
}