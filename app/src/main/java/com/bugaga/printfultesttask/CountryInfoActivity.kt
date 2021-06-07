package com.bugaga.printfultesttask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NavUtils
import com.squareup.picasso.Picasso

class CountryInfoActivity : AppCompatActivity() {
    private lateinit var CountryName: TextView
    private lateinit var TotalCases: TextView
    private lateinit var TotalDeaths: TextView
    private lateinit var TotalRecovered: TextView
    private lateinit var Active: TextView
    private lateinit var Closed: TextView
    private lateinit var Flag: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_info)
        supportActionBar?.title = "Country Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val country = intent.getSerializableExtra("country") as? CountriesData
        if (country != null) initUI(country)
        else{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
    private fun initUI(country : CountriesData) {
        CountryName = findViewById(R.id.CountryInfoTitle)
        CountryName.text = country.Country
        TotalCases = findViewById(R.id.CountryInfoCasesLabel)
        TotalCases.text = country.TotalConfirmed.toString()
        TotalDeaths = findViewById(R.id.CountryInfoDeathsLabel)
        TotalDeaths.text = country.TotalDeaths.toString()
        TotalRecovered = findViewById(R.id.CountryInfoRecoveredLabel)
        TotalRecovered.text = country.TotalRecovered.toString()
        Active = findViewById(R.id.CountryInfoActiveLabel)
        var tmp = country.TotalConfirmed - country.TotalRecovered - country.TotalDeaths
        Active.text = tmp.toString()
        Closed = findViewById(R.id.CountryInfoClosedLabel)
        tmp = country.TotalRecovered + country.TotalDeaths
        Closed.text = tmp.toString()
        Flag = findViewById(R.id.CountryInfoFlag)
        val url = "https://www.countryflags.io/${country.CountryCode}/flat/64.png"
        Picasso.get().load(url).into(Flag)

    }
}