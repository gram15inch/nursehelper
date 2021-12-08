package org.techtown.nursehelper.Home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.databinding.FragmentHomeBinding
import org.techtown.nursehelper.databinding.HomeSectionBinding
import org.techtown.nursehelper.userSchedule
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    lateinit var mainActivity : MainActivity
    val binding by lazy{FragmentHomeBinding.inflate(layoutInflater)}
    val todayBinding by lazy{HomeSectionBinding.inflate(layoutInflater)}
    val recentBinding by lazy{HomeSectionBinding.inflate(layoutInflater)}
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val today = Calendar.getInstance()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var todayAdapter = homeScheduleAdpater(mainActivity)
        todayBinding.run{
            textSectionTitle.text = "오늘 일정"
            sectionRecyclerView.adapter = todayAdapter
            sectionRecyclerView.layoutManager = LinearLayoutManager(activity)
        }

        //프레임안에 레이아웃 넣기
        binding.todayFrame.addView(todayBinding.root)
        CoroutineScope(Dispatchers.Main).launch {
           var rt : List<userSchedule> = listOf()
            CoroutineScope(Dispatchers.Default).async {
                val sp1: SharedPreferences =  mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                var id = sp1.getString("id", null)
                if(id==null){
                    Log.d("tsthome","id = null")
                }else {
                    //rt = mainActivity.getDaySchedule(id, dateFormat.format(today.time))
                    rt = mainActivity.searchData(today.time)
                    Log.d("tsthome", rt.size.toString())
                }
            }.await()
            Log.d("tsthome","await")

            when(rt.size){
                //데이터 없음
                 0-> {
                    Log.d("tst","home : 값 없음")
                }
                //데이터 있음
                else->{
                    todayAdapter.Users =rt

                }
            }


        }



        return binding.root
    }

}