package org.techtown.nursehelper.Document

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.databinding.FragmentDocumentBinding
import org.techtown.nursehelper.userDocument
import java.text.SimpleDateFormat
import java.util.*


class DocumentFragment : Fragment() {
    val binding by lazy{FragmentDocumentBinding.inflate(layoutInflater)}
    lateinit var mainActivity : MainActivity
    lateinit var Adapter: userDocumentAdapter

    val dateFormat = SimpleDateFormat("yyyyMMdd")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val searchView = binding.searchView

        //어답터 초기화
        Adapter = userDocumentAdapter(mainActivity)
        Adapter.dateAdapter = this.dateAdapter
        //리클라이어 초기화
        binding.searchRecycler.run {
            adapter = Adapter
            layoutManager = LinearLayoutManager(activity)

        }

        //토글 변경시 어답터 업데이트
        binding.searchTg.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(searchView.query.toString()=="")
                binding.searchRecycler.visibility = View.INVISIBLE
            else if(isChecked)
                updateAdapter(searchView.query.toString())
        }
        binding.dateTg.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if(searchView.query.toString()=="")
                binding.searchRecycler.visibility = View.INVISIBLE
            else if(isChecked)
                updateAdapter(searchView.query.toString())

        }

        //검색창 텍스트 이벤트
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

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
                updateAdapter(query)
                if(binding.searchRecycler.visibility==View.INVISIBLE)
                    binding.searchRecycler.visibility = View.VISIBLE
                return false}

            })

        //포켜스 여부 (현재는 사용안함)
        searchView.setOnQueryTextFocusChangeListener(object : View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if(hasFocus)
                    Log.d("tst","ON")
                else
                    Log.d("tst","OFF")
            }
        })


        return binding.root
    }

    fun getDocument(pcode:String,name:String,addr:String,date:String):List<userDocument>{
        var documents = listOf<userDocument>()
        val sp1: SharedPreferences =  mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var id = sp1.getString("id", null)
        if(id==null)
            return documents
        else
            return mainActivity.dbc.getDocument(id,pcode,name,addr,date)
    }

    fun updateAdapter(newText:String){

        var name =""
        var addr=""
        var pcode =""
        var today= Calendar.getInstance()
        var date =""
        when( binding.searchTg.checkedButtonId){
            R.id.nameTgBtn ->{name = newText}
            R.id.addrTgBtn ->{addr = newText}
            R.id.pnoTgBtn ->{pcode= newText}
        }
        when(binding.dateTg.checkedButtonId){
            R.id.weekTgBtn ->{today.add(Calendar.DAY_OF_WEEK,-7)}
            R.id.monthTgBtn ->{today.add(Calendar.MONTH,-1)}
            R.id.yearTgBtn ->{today.add(Calendar.YEAR,-1)}
        }
        date = dateFormat.format(today.time)

        CoroutineScope(Dispatchers.Main).launch {
            lateinit var rt : List<userDocument>
            CoroutineScope(Dispatchers.Default).async {
                rt = getDocument(pcode,name,addr,date)
            }.await()
            when(rt){
                //실패시
                null -> Log.d("tst","dbError")
                //성공시
                else-> Adapter.Documents = rt

            }
        }
    }
    val dateAdapter = object : ()->Unit{
        override fun invoke() {
            updateAdapter(binding.searchView.query.toString())
        }

    }
}