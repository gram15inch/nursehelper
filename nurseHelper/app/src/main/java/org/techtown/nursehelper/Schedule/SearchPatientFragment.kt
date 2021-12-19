package org.techtown.nursehelper.Schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import org.techtown.nursehelper.Document.userDocumentAdapter
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.databinding.FragmentSearchPatientBinding
import java.util.*

class searchPatientFragment : Fragment() {
    val binding by lazy {  FragmentSearchPatientBinding.inflate(layoutInflater) }
    lateinit var mainActivity : MainActivity
    lateinit var patientAdapter: patientAdapter
    //디테일에서 초기화
    lateinit var patientUpdate : (userPatient)->Unit
    var parseCal : Calendar
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
        //뒤로가기
        binding.prevBtn3.setOnClickListener {
            mainActivity.supportFragmentManager.popBackStack("search_patient", 1)
        }

        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //텍스트 변경시 (지금 사용안함)
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == "")
                    binding.searchRecycler.visibility = View.INVISIBLE
                Log.d("tst", "submit:$newText")
                return false

            }
            //쿼리 제출시 어답터 업데이트
            override fun onQueryTextSubmit(query: String): Boolean {

                if(query=="") {
                    binding.searchRecycler.visibility = View.INVISIBLE
                    return false}
                updateAdapter(query) //어답터 업데이트

                if(binding.searchRecycler.visibility==View.INVISIBLE)
                    binding.searchRecycler.visibility = View.VISIBLE
                return false}

        })
        }
        //어답터 초기화

        //리클라이어 초기화
        binding.searchRecycler.run {
            patientAdapter = patientAdapter(mainActivity)
            patientAdapter.patientUpdate = this@searchPatientFragment.patientUpdate
            adapter = patientAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
    }
    fun updateAdapter(qry:String){

        val tmpUser = mutableListOf<userPatient>()
        tmpUser.add(userPatient(1,
            "$qry",
            "m",
            Calendar.getInstance().time,
            "addr1"
        ))
        patientAdapter.Users = tmpUser

    }
}