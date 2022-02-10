package com.benjaminwan.ocr.ncnn

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benjaminwan.ocr.ncnn.app.App
import com.benjaminwan.ocr.ncnn.models.Records
import com.benjaminwan.ocr.ncnn.models.RecordsViewModel
import com.benjaminwan.ocr.ncnn.models.RecordsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton





class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val RecordActivityRequestCode = 123


    private lateinit var list_records: RecyclerView
    private lateinit var button_search_address: Button
    private lateinit var button_search_status: Button
    private lateinit var input_search: EditText
    private fun initViews() {
        list_records = findViewById<RecyclerView>(R.id.list_of_records)
        button_search_address = findViewById<Button>(R.id.button_search_address)
        button_search_status = findViewById<Button>(R.id.button_search_status)
        input_search = findViewById<EditText>(R.id.input_search)
    }

    private val recordsViewModel: RecordsViewModel by viewModels{
        RecordsViewModelFactory((application as App).repository)
    }




    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //此处是跳转的result回调方法
            if (it.data != null && it.resultCode == Activity.RESULT_OK) {
                it.data?.getStringExtra(RecordActivity.EXTRA_REPLY)?.let { reply ->
                    val word = reply
                    var delimiter = ","

                    val parts = word.split(delimiter)
                    System.out.println("main receive:" + reply)
                    if(parts.size == 8){
                        System.out.println(parts.toString())
                        //recordsViewModel.update(Records(parts[1].toInt(),parts[2],parts[3],parts[4],parts[5],parts[6],parts[7]))
                    }else{
                        //recordsViewModel.insert(Records(parts[0].toInt(),parts[1],parts[2],parts[3],parts[4],parts[5],parts[6]))
                    }

                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        val adapter = RecordsListAdapter()
        list_records.adapter = adapter
        list_records.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlL.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        recordsViewModel.allRecords.observe(this) { records ->
            // Update the cached copy of the words in the adapter.
            records.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, RecordActivity::class.java)
            startActivity.launch(intent)
        }



        button_search_address.setOnClickListener {
            val address: String  = input_search.text.toString()
            recordsViewModel.searchByAddress(address)
        }

        button_search_status.setOnClickListener {
            val status: String = input_search.text.toString()
            recordsViewModel.searchByStatus(status)
        }


    }



    override fun onClick(view: View?) {
//        view ?: return
//        when (view.id) {
//            R.id.fab -> {
//                startActivity(Intent(this, RecordActivity::class.java))
//            }
//
//            else -> {
//            }
//        }
    }
}