package com.benjaminwan.ocr.ncnn

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.afollestad.assent.rationale.createDialogRationale
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.benjaminwan.ocr.ncnn.app.App
import com.benjaminwan.ocr.ncnn.models.Records
import com.benjaminwan.ocr.ncnn.models.RecordsViewModel
import com.benjaminwan.ocr.ncnn.models.RecordsViewModelFactory
import com.benjaminwan.ocr.ncnn.utils.RecordsRoomDatabase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class RecordActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,AMapLocationListener{

    private val MY_PERMISSIONS_REQUEST_CALL_LOCATION = 1
    var mlocationClient: AMapLocationClient? = null
    var mLocationOption: AMapLocationClientOption? = null



    private lateinit var button_update: Button
    private lateinit var button_delete: Button
    private lateinit var button_add: Button
    private lateinit var button_get_loc: Button
    private lateinit var button_take_pic: Button
    private lateinit var textView_detail_address: TextView
    private lateinit var textView_status: TextView
    private lateinit var textView_date: TextView
    private lateinit var textView_area: TextView
    private lateinit var textView_length: TextView
    private lateinit var textView_about_address: TextView
    private lateinit var input_date: EditText
    private lateinit var input_detail_address: EditText
    private lateinit var input_length: EditText
    private lateinit var input_area: EditText
    private lateinit var input_about_address: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton

    private fun findViews() {
        button_take_pic = findViewById<Button>(R.id.button_take_pic)
        button_get_loc = findViewById<Button>(R.id.button_get_loc)
        button_add = findViewById<Button>(R.id.button_add_record)
        button_update = findViewById<Button>(R.id.button_update)
        button_delete = findViewById<Button>(R.id.button_delete)

        textView_area = findViewById<TextView>(R.id.textView_area)
        textView_date = findViewById<TextView>(R.id.textView_date)
        textView_detail_address = findViewById<TextView>(R.id.textView_detail_address)
        textView_length = findViewById<TextView>(R.id.textView_length)
        textView_status = findViewById<TextView>(R.id.textView_status)
        textView_about_address = findViewById<TextView>(R.id.textView_about_address)

        input_date = findViewById<EditText>(R.id.input_date)
        input_detail_address = findViewById<EditText>(R.id.input_detail_address)
        input_length = findViewById<EditText>(R.id.input_length)
        input_area = findViewById<EditText>(R.id.input_area)
        input_about_address = findViewById<EditText>(R.id.input_about_address)

        radioGroup = findViewById(R.id.radioGroup_status)
    }

    private fun initViews() {
        button_get_loc.setOnClickListener(this)
        button_take_pic.setOnClickListener(this)
    }

    val recordsViewModel: RecordsViewModel by viewModels{
        RecordsViewModelFactory((application as App).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        findViews()
        initViews()
        AMapLocationClient.updatePrivacyShow(this,true,true);
        AMapLocationClient.updatePrivacyAgree(this,true);
        val intent = intent
        val data = getIntent().getStringExtra("key").toString()
        var delimiter = ","

        val parts = data.split(delimiter)
        System.out.println("records receive:" + parts.toString())
        if(data.isNotEmpty()){
            if(parts.size!=7 ){

            }else{
                input_date.setText(parts[1])
                input_about_address.setText(parts[2])
                input_detail_address.setText(parts[3])
                input_area.setText(parts[4])
                input_length.setText(parts[5])
                if(parts[6] == "?????????")
                    radioGroup.check(R.id.radioButton)
                if(parts[6] == "?????????")
                    radioGroup.check(R.id.radioButton2)
                if(parts[6] == "??????")
                    radioGroup.check(R.id.radioButton3)

            }

        }

        button_add.setOnClickListener {
            val replyIntent = Intent()
            if (input_about_address.text.toString().isEmpty()) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                val id = 0
                val date = input_date.text.toString()
                val detail_address = input_detail_address.text.toString()
                val about_address = input_about_address.text.toString()
                val area = input_area.text.toString()
                val length = input_length.text.toString()

                val radioButton: RadioButton = findViewById(radioGroup.getCheckedRadioButtonId());

                val status = radioButton.text.toString()

                val record = Records(
                    id = id,
                    date = date,
                    about_address = about_address,
                    detail_address = detail_address,
                    area = area,
                    length = length,
                    status = status)

                val str = "0," + date +","+ about_address + ","+detail_address +","+area+","+length+","+status

                System.out.println(record.toString())
                replyIntent.putExtra(EXTRA_REPLY, str)
                setResult(RESULT_OK, replyIntent)
                recordsViewModel.insert(record)

            }
            finish()
        }




        button_update.setOnClickListener {
            val replyIntent = Intent()
            val id = parts[0].toInt()
            val date = input_date.text.toString()
            val detail_address = input_detail_address.text.toString()
            val about_address = input_about_address.text.toString()
            val area = input_area.text.toString()
            val length = input_area.text.toString()
            val radioButton: RadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            val status = radioButton.text.toString()

            if (input_about_address.text.toString().isEmpty()) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                val record = Records(
                    id = id,
                    date = date,
                    about_address = about_address,
                    detail_address = detail_address,
                    area = area,
                    length = length,
                    status = status)

                val str = "update," + id.toString() + "," + date +","+ about_address + ","+detail_address +","+area+","+length+","+status

                System.out.println(str)
                replyIntent.putExtra(EXTRA_REPLY, str)
                setResult(RESULT_OK, replyIntent)
                recordsViewModel.update(record)
            }
            finish()
        }

        button_delete.setOnClickListener {
            val replyIntent = Intent()
            val id = parts[0].toInt()
            val date = input_date.text.toString()
            val detail_address = input_detail_address.text.toString()
            val about_address = input_about_address.text.toString()
            val area = input_area.text.toString()
            val length = input_area.text.toString()
            val radioButton: RadioButton = findViewById(radioGroup.getCheckedRadioButtonId());
            val status = radioButton.text.toString()

            if (input_about_address.text.toString().isEmpty()) {
                setResult(RESULT_CANCELED, replyIntent)
            } else {
                val record = Records(
                    id = id,
                    date = date,
                    about_address = about_address,
                    detail_address = detail_address,
                    area = area,
                    length = length,
                    status = status)

                val str = "delete," + id.toString() + "," + date +","+ about_address + ","+detail_address +","+area+","+length+","+status

                System.out.println(str)
                replyIntent.putExtra(EXTRA_REPLY, str)
                setResult(RESULT_OK, replyIntent)
                recordsViewModel.delete(record)
            }
            finish()
        }


//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                    MY_PERMISSIONS_REQUEST_CALL_LOCATION
//                )
//            } else {
//                //"???????????????";
//                if(data.isEmpty())
//                    showLocation()
//            }
//        }
//
//
//        //????????????????????????M
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                    MY_PERMISSIONS_REQUEST_CALL_LOCATION
//                )
//            } else {
//                //"???????????????";
//                if(data.isEmpty())
//                    showLocation()
//            }
//        }

        // Android SDK<=28 ????????????????????????
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                ActivityCompat.requestPermissions(this, strings, 1)
            }
        } else {
            // Android SDK > 28 ???????????????????????????????????????android.permission.ACCESS_BACKGROUND_LOCATION????????????
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val strings = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    "android.permission.ACCESS_BACKGROUND_LOCATION"
                )
                ActivityCompat.requestPermissions(this, strings, 2)
            }
        }

    }





    override fun onClick(view: View?) {
        view ?: return
        when (view.id) {
            R.id.button_get_loc -> {
                showLocation()
            }
            R.id.button_take_pic -> {
                startActivity(Intent(this, CameraActivity::class.java))
            }
        }
    }

    private fun getPermissions() {
        val rationaleHandler = createDialogRationale(R.string.storage_permission) {
            onPermission(
                Permission.READ_EXTERNAL_STORAGE, "???????????????",
            )
        }

        if (!isAllGranted(Permission.READ_EXTERNAL_STORAGE)) {
            askForPermissions(
                Permission.READ_EXTERNAL_STORAGE,
                rationaleHandler = rationaleHandler
            ) { result ->
                val permissionGranted: Boolean =
                    result.isAllGranted(
                        Permission.READ_EXTERNAL_STORAGE
                    )
                if (!permissionGranted) {
                    showToast("?????????????????????????????????????????????")
                }
            }
        }
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        TODO("Not yet implemented")
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        TODO("Not yet implemented")
    }


    override fun onLocationChanged(amapLocation: AMapLocation?) {
        try {
            if (amapLocation != null) {
                if (amapLocation.errorCode == 0) {
                    //?????????????????????????????????????????????

                    //??????????????????????????????????????????????????????????????????????????????
                    Log.i("????????????", amapLocation.locationType.toString() + "")
                    Log.i("????????????", amapLocation.latitude.toString() + "")
                    Log.i("????????????", amapLocation.longitude.toString() + "")
                    Log.i("??????????????????", amapLocation.accuracy.toString() + "")

                    //??????option?????????isNeedAddress???false??????????????????????????????????????????????????????????????????GPS??????????????????????????????
                    Log.i("??????", amapLocation.address)
                    Log.i("????????????", amapLocation.country)
                    Log.i("?????????", amapLocation.province)
                    Log.i("????????????", amapLocation.city)
                    Log.i("????????????", amapLocation.district)
                    Log.i("????????????", amapLocation.street)
                    Log.i("?????????????????????", amapLocation.streetNum)
                    Log.i("????????????", amapLocation.cityCode)
                    Log.i("????????????", amapLocation.adCode)
                    Log.i("????????????????????????AOI??????", amapLocation.aoiName)
                    Log.i("????????????????????????????????????Id", amapLocation.buildingId)
                    Log.i("?????????????????????????????????", amapLocation.floor)
                    Log.i("??????GPS???????????????", amapLocation.gpsAccuracyStatus.toString() + "")

                    //??????????????????
                    val df = SimpleDateFormat("yyyy-MM-dd")
                    val date = Date(amapLocation.time)
                    Log.i("??????????????????", df.format(date))

                    input_about_address.setText(amapLocation.address.toString())
                    input_date.setText(df.format(date))
                    // ????????????
                    mlocationClient!!.stopLocation()
                } else {
                    //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
                    Log.e(
                        "AmapError", "location Error, ErrCode:"
                                + amapLocation.errorCode + ", errInfo:"
                                + amapLocation.errorInfo
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun showLocation() {
        try {
            mlocationClient = AMapLocationClient(this)
            mLocationOption = AMapLocationClientOption()

            mlocationClient!!.setLocationListener(this)
            //???????????????????????????????????????Battery_Saving?????????????????????Device_Sensors??????????????????
            mLocationOption!!.locationMode =
                AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption!!.interval = 5000
            mLocationOption!!.isOnceLocation = true
            //??????????????????
            mlocationClient!!.setLocationOption(mLocationOption)
            //????????????
            mlocationClient!!.startLocation()
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
        // ????????????
        if (null != mlocationClient) {
            mlocationClient!!.stopLocation()
        }
    }

    /**
     * ????????????
     */
    private fun destroyLocation() {
        if (null != mlocationClient) {
            /**
             * ??????AMapLocationClient????????????Activity???????????????
             * ???Activity???onDestroy??????????????????AMapLocationClient???onDestroy
             */
            mlocationClient!!.onDestroy()
            mlocationClient = null
        }
    }

    override fun onDestroy() {
        destroyLocation()
        super.onDestroy()
    }

    private fun showToast(string: String) {
        Toast.makeText(this@RecordActivity, string, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_REPLY = "123"
    }
}