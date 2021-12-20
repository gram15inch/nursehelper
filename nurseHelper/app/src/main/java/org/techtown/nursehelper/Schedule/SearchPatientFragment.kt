package org.techtown.nursehelper.Schedule

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
        //서치뷰 초기화
        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            //텍스트 변경시
            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == "")
                    binding.searchRecycler.visibility = View.INVISIBLE
                else {
                    updateAdapter(newText) //어답터 업데이트
                    // Log.d("tst", "submit:$newText")
                    if (binding.searchRecycler.visibility == View.INVISIBLE)
                        binding.searchRecycler.visibility = View.VISIBLE
                }
                return false

            }
            //쿼리 제출시 어답터 업데이트
            override fun onQueryTextSubmit(query: String): Boolean {

                if(query=="") {
                    binding.searchRecycler.visibility = View.INVISIBLE
                    return false}


                if(binding.searchRecycler.visibility==View.INVISIBLE)
                    binding.searchRecycler.visibility = View.VISIBLE
                return false}

        })
        }

        //리클라이어 초기화
        binding.searchRecycler.run {
            patientAdapter = patientAdapter(mainActivity)
            patientAdapter.patientUpdate = this@searchPatientFragment.patientUpdate
            patientAdapter.keyFocusClear = this@searchPatientFragment.keyFocusClear
            adapter = patientAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        return binding.root
    }
    fun updateAdapter(qry:String){

        lateinit var rt : List<userPatient>

        val id = mainActivity.getUserInfo()
        if(id != "") {

            CoroutineScope(Dispatchers.Main).launch {
                CoroutineScope(Dispatchers.Default).async {
                     rt = mainActivity.dbc.getPatient(id,qry) //여기부터
                }.await()
                when(rt.size){

                    //실패시
                    0 -> Log.d("tst","sche_get : 값 없음")
                    //성공시
                    else -> {
                        patientAdapter.Users = rt
                    }


                }
            }

        }else
            Log.d("tst","sche_del : id error")

    }
                val keyFocusClear = object : ()->Unit {
                override fun invoke() {
                    val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow( mainActivity.currentFocus?.windowToken, 0)
        }
    }
}