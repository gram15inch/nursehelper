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
import org.techtown.nursehelper.userSchedule
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    lateinit var mainActivity : MainActivity
    val binding by lazy{FragmentHomeBinding.inflate(layoutInflater)}
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
        binding.apply{
            texthomeTitle.text= "오늘일정"
            homeRecyclerView.adapter = todayAdapter
            homeRecyclerView.layoutManager = LinearLayoutManager(activity)
        }

        CoroutineScope(Dispatchers.Main).launch {
           var rt : List<userSchedule> = listOf()
            CoroutineScope(Dispatchers.Default).async {
                val sp1: SharedPreferences =  mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
                var id = sp1.getString("id", null)
                if(id==null){
                    Log.d("tsthome","id = null")
                }else {
                    rt = mainActivity.searchData(today.time)
                }
            }.await()

            when(rt.size){
                //데이터 없음
                 0-> {
                    Log.d("tst","home : 값 없음")
                }
                //데이터 있음
                else->{
                    todayAdapter.Users =mainActivity.sortData(rt)
                }
            }


        }

    fun test1(){
        Log.d("tst","test1 on")
    }

        return binding.root
    }

}