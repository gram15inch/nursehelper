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
        mainActivity = context as MainActivity


    }


    override fun onResume() {
        super.onResume()
        //Log.d("tst","home : onResume")
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


        val homeUpdate =object: ()->Unit{
            override fun invoke() {
                  val  rt = mainActivity.searchData(today.time)
                    todayAdapter?.Users =mainActivity.sortData(rt)
            }

        }

        mainActivity.homeUpdate = homeUpdate
        homeUpdate.invoke()

        return binding.root
    }


}