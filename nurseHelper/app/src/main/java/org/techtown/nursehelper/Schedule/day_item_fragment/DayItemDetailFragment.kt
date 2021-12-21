package org.techtown.nursehelper.Schedule.day_item_fragment

import android.R.attr
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.setFragmentResultListener
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.Schedule.searchPatientFragment
import org.techtown.nursehelper.Schedule.userPatient
import org.techtown.nursehelper.databinding.FragmentDayItemDetailBinding
import org.techtown.nursehelper.userSchedule
import java.util.*
import android.R.attr.button
import android.text.format.DateFormat.is24HourFormat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.techtown.nursehelper.DBC
import java.text.SimpleDateFormat


class DayItemDetailFragment(var sUser : userSchedule? =null) : Fragment() {
    val binding by lazy{FragmentDayItemDetailBinding.inflate(layoutInflater)}
    lateinit var mainActivity : MainActivity
    var pUser :userPatient? = null
    var parseCal : Calendar
    val timeF = SimpleDateFormat("hh:mm a")
    val dateF = SimpleDateFormat("yyyy/MM/dd")
    val st =Calendar.getInstance()
    val et =Calendar.getInstance()
    var isDate =0
    var isName =0
    init{
        parseCal = Calendar.getInstance()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        //환자 이름검색
        binding.searchPaitentBtn.setOnClickListener {

            var searchPatientFragment = searchPatientFragment()

            //검색결과를 다시가져오는함수 주입
            searchPatientFragment.patientUpdate = this.patientUpdate
            mainActivity.supportFragmentManager.beginTransaction().run{
                replace(R.id.popUpContainer,searchPatientFragment)
                addToBackStack("search_patient")
                commit()
            }


        }

        //일정 초기화
        binding.run {
            //넘겨받은 일정이 있을경우
            if(sUser!=null){
                //화면 텍스트 초기화
                textNameDetail.text = sUser?.name
                textAddDetail.setText(sUser?.addr?:"21")
                textStartDate.setText(dateF.format(sUser?.startTime))
                textStartTime.setText(timeF.format(sUser?.startTime))
                textEndDate.setText(dateF.format(sUser?.endTime))
                textEndTime.setText(timeF.format(sUser?.endTime))

                //프래그먼트 변수 초기화
                st.time = sUser?.startTime
                et.time = sUser?.endTime
                isName =1
                isDate =1

            }//넘겨받은 환자가 있을경우
            else if(pUser!= null){
                isName =1

                
            }else{
                textNameDetail.text = ""
                textAddDetail.setText("")
                textStartDate.setText("")
                textEndDate.setText("")
                isName=0
                isDate=0
            }


        }


        //데이트 피커 초기화
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.addOnPositiveButtonClickListener {
            //피커에서 가져온 날짜
            val calP =Calendar.getInstance()
            calP.time = Date(it)

            //프래그먼트에서 가져온 날짜
            val calSt =Calendar.getInstance()
            calSt.time = st.time
            calSt.set(Calendar.YEAR,calP.get(Calendar.YEAR))
            calSt.set(Calendar.MONTH,calP.get(Calendar.MONTH))
            calSt.set(Calendar.DAY_OF_MONTH,calP.get(Calendar.DAY_OF_MONTH))


            //시작, 끝 날짜 구분
            when(datePicker.tag){
                "startDate"-> binding.textStartDate.text =dateF.format(calSt.time)
                "endDate"-> binding.textEndDate.text =dateF.format(calSt.time)
            }
        }

        //타임피커 초기화
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Select Appointment time")
                .build()

        timePicker.addOnPositiveButtonClickListener {


            val cal =Calendar.getInstance()
            cal.time = st.time
            cal.set(Calendar.HOUR,timePicker.hour)
            cal.set(Calendar.MINUTE,timePicker.minute)

            //시작, 끝 시간 구분
            when(timePicker.tag){
                "startTime"->  {
                    binding.textStartTime.text =timeF.format(cal.time)

                    //끝날짜,시간 자동생성 :: 0 날짜, +1 시간
                    cal.add(Calendar.HOUR,1)
                    if(binding.textEndDate.text =="")
                        binding.textEndDate.text = dateF.format(cal.time)
                    if(binding.textEndTime.text ==""){
                        cal.add(Calendar.HOUR,1)
                        binding.textEndTime.text = timeF.format(cal.time)
                    }
                }

                "endTime"-> {
                    binding.textEndTime.text = timeF.format(cal.time)
                    //여기부터 12/21:: 날짜 설정후 타당성 검증 필요
                    //오전오후 바뀜 확인 !!
                    //checkDateValid()

                }
            }
        }


        //날짜,시간 클릭시 다이얼에서 가져오기
        binding.textStartDate.setOnClickListener {
            datePicker.show(mainActivity.supportFragmentManager,"startDate")
        }
        binding.textStartTime.setOnClickListener {
            timePicker.show(mainActivity.supportFragmentManager,"startTime")
        }
        binding.textEndDate.setOnClickListener {
            datePicker.show(mainActivity.supportFragmentManager,"endDate")
        }
        binding.textEndTime.setOnClickListener {
            timePicker.show(mainActivity.supportFragmentManager,"endTime")
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenerInit()


      /*
        setMainColor(Color.RED)
        colorAdapterInit(binding.colorRecycle)*/
    }

    override fun onStart() {
        super.onStart()

        if(this.arguments?.getString("date")!=null)
            binding.textStartDate.text = this.arguments?.getString("date").toString()

        //넘겨받은 값이 있으면 이름,주소 생성
        if(pUser?.name?:null !=null){
            binding.textNameDetail.text = pUser?.name
            binding.textAddDetail.text = pUser?.addr
        }


    }

    fun showSaveBtn(cmd :Int){
        when(cmd){
            1-> binding.saveBtn.visibility = View.VISIBLE
            0-> binding.saveBtn.visibility = View.GONE
        }
    }

    fun checkDateValid(s:Date,e:Date):Int{
        val sc= Calendar.getInstance()
        sc.time =s
        val ec = Calendar.getInstance()
        ec.time =e
        if(sc.compareTo(ec)>=0)
            Log.d("tst","sc")
        else
            Log.d("tst","ec")
        return 1
    }
    // SearchPatientFragment에서 사용
    val patientUpdate  = object : (userPatient)->Unit{
        override fun invoke(up:userPatient) {
            this@DayItemDetailFragment.pUser = up
        }
    }


    fun listenerInit(){

        binding.run{
            prevBtn.setOnClickListener {
                mainActivity.supportFragmentManager.popBackStack("day_item_detail", 1)
            }

            /*

            mainColorView.setOnClickListener {
                if (colorRecycle.visibility == View.VISIBLE)
                    colorRecycle.visibility = View.INVISIBLE
                else if(colorRecycle.visibility == View.INVISIBLE )
                    colorRecycle.visibility = View.VISIBLE
                //Log.d("tst1","${binding.mainColorView.visibility} == ${View.VISIBLE}")
            }

*/
        }


        binding.colorRecycle.setOnTouchListener { v, event ->
            true
        }

        this.view?.setOnClickListener {
            binding.colorRecycle.visibility = View.INVISIBLE
        }

    }

    /*

    fun loadColorData():MutableList<Int>{
        var colorList = mutableListOf<Int>()

        colorList.add(Color.RED)
        colorList.add(Color.BLUE)
        colorList.add(Color.CYAN)
        colorList.add(Color.GREEN)
        colorList.add(Color.YELLOW)
        colorList.add(Color.MAGENTA)

        return colorList
    }


    fun setMainColor(color: Int) {
        var shape = ContextCompat.getDrawable(mainActivity, R.drawable.color_bg)
        shape?.setTint(color)
        binding.mainColorView.setBackground(shape)
    }
*/
}