package com.bugaga.printfultesttask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class myAdapter(val context: Context,val dataList: List<CountriesData>):BaseAdapter() {

    private val mInflator: LayoutInflater
    init {
        this.mInflator = LayoutInflater.from(context)
    }
    private class ViewHolder(row: View?){
        var title: TextView? = null
        var conf: TextView? = null
        var flag: ImageView? = null
        var dead: TextView?= null
        init {
            title = row?.findViewById(R.id.CountryTitle)
            conf = row?.findViewById(R.id.CountryTotalConf)
            flag = row?.findViewById(R.id.CountryFlag)
            dead = row?.findViewById(R.id.CountryTotalDeaths)
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null){
            view = mInflator.inflate(R.layout.adapter_layout,null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.title?.text = dataList[position].Country
        viewHolder.conf?.text = dataList[position].TotalConfirmed.toString()
        viewHolder.dead?.text = dataList[position].TotalDeaths.toString()
        val url = "https://www.countryflags.io/${dataList[position].CountryCode}/flat/64.png"
        Picasso.get().load(url).into(viewHolder.flag)
        return view as View
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}