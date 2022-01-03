package org.techtown.nursehelper

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.techtown.nursehelper.databinding.FragmentHomeBinding
import org.techtown.nursehelper.databinding.FragmentWritingBinding
import java.text.SimpleDateFormat
import java.util.*






class WritingFragment(val cmd:Int) : Fragment() {
    lateinit var mainActivity : MainActivity
    lateinit var binding :FragmentWritingBinding
    lateinit var Document: userDocument
    lateinit var userSchedule: userSchedule
    //UserDocumentAdapter에서 전달
    lateinit var dateAdapter: ()->Unit
    val dateFormat = SimpleDateFormat("yyyyMMdd")
    val today = Calendar.getInstance()
    var wfHeight = -1

    //호출위치에 따라 초기화
    var pCode =-1
    var Date = ""
    var Memo :String = "#null"
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWritingBinding.inflate(layoutInflater)

        //문서 관리 탭에서 호출
        if(cmd == 1){
            Log.d("tst","doc call")
            binding.apply {
                writeName.text = Document.name
                writeAddr.text = Document.addr
                writeDom.text = dateFormat.format(Document.birth)
                writeSex.text = Document.sex
                writeMemo.setText(Document.memo)
            }

            //업데이트시 사용할 코드
            pCode = Document.pCode
            Date = dateFormat.format(Document.date)

        //홈에서 호출
        }else if(cmd==2) {
            Log.d("tst","home call")
            //업데이트시 사용할 코드
            Date = dateFormat.format(today.time)

            //---- 수정금지 공백유무로 가져오는값이 다름
            var name = userSchedule.name
            var addr = ""
            var pcode = ""
            var date = dateFormat.format(today.time)
            // -----

            CoroutineScope(Dispatchers.Main).launch {
                lateinit var rt: List<userDocument>
                CoroutineScope(Dispatchers.Default).async {
                    rt = getDocument(pcode, name, addr, date)

                }.await()
                when (rt.size) {
                    //오류
                    null -> Log.d("tst", "dbError")

                    //값이 없을시
                    0 -> binding.apply {
                        Log.d("tst","rt : size(0)")
                        writeName.text = userSchedule.name
                        writeAddr.text = userSchedule.addr
                        writeDom.text = dateFormat.format(userSchedule.birth)
                        writeSex.text = userSchedule.sex
                        writeMemo.setText("")
                    }

                    //성공시
                    else -> {
                        Log.d("tst", " rt : true")
                        binding.apply {
                            writeName.text = rt[0].name
                            writeAddr.text = rt[0].addr
                            writeDom.text = dateFormat.format(rt[0].birth)
                            writeSex.text = rt[0].sex
                            writeMemo.setText(rt[0].memo)
                        }
                        pCode = rt[0].pCode
                        Memo = rt[0].memo
                        Log.d("tst", " home pcode : $pCode / $Memo")


                    }
                }
            }


        }

        //메모 수정시 완료버튼 보이기
        binding.writeMemo.addTextChangedListener (
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(isEditMeno(cmd) && !Memo.equals("#null"))
                            binding.writeRtBtn.visibility = View.VISIBLE
                    else
                        binding.writeRtBtn.visibility = View.GONE

                    Log.d("tst","bool : ${isEditMeno(cmd) } && ${ !Memo.equals("")||(!binding.writeMemo.text.toString().equals(""))}")
                }

                override fun afterTextChanged(s: Editable?) { }


            }
        )

        //포커스 여부
        binding.writeMemo.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                if(wfHeight == -1) {
                    wfHeight = this.view?.height?:0
                   // Log.d("tst", "wfHeight : $wfHeight")
                }
            }
            else
                Log.d("tst", "OFF")

        }

        //클릭시 wirting프레그먼트로 이동
        binding.prevBtn2.setOnClickListener{
            mainActivity.supportFragmentManager.popBackStack("write", 1)
            mainActivity.binding.SubFrame.visibility = View.GONE
        }

        //완료 버튼
        binding.writeRtBtn.setOnClickListener {
            if(pCode == -1)
                return@setOnClickListener
            //키보드 내리고 포커스 해제
            val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow( mainActivity.currentFocus?.windowToken, 0)
            binding.writeMemo.clearFocus()

            //완료버튼 가리기
            if(binding.writeRtBtn.visibility != View.GONE)
                binding.writeRtBtn.visibility = View.GONE

            //데이터 업데이트
            CoroutineScope(Dispatchers.Main).launch {
                var rt: Int = -1
                CoroutineScope(Dispatchers.Default).async {
                    Log.d("tstUp", "update pCode : $pCode")
                    rt = updateDocument("1", //추후 type별 검색시 수정
                        pCode.toString(),
                        Date,
                        binding.writeMemo.text.toString())

                }.await()
                when (rt) {
                    //업데이트 정상
                    1 -> {Log.d("tst", "업데이트 정상")
                        if(cmd==1)
                            dateAdapter.invoke()
                    }
                    else ->  Log.d("tst", "doc update Error")

                }
            }

        }


        //meno 클릭유도
        binding.apply {
            writeFormEmpty.setOnClickListener {
                writeMemo.setSelection(writeMemo.length())
                writeMemo.requestFocus()
                val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(writeMemo,0)
                Log.d("tstclk","click formBack")
            }
        }

        //클릭미스 방지
        binding.apply {

            //writingBackScr.setOnTouchListener { v, event -> true }
            //writeFormEmpty.setOnTouchListener { v, event -> true }
        }
        return binding.root
    }

    fun getDocument(pcode:String,name:String,addr:String,date:String):List<userDocument>{
        lateinit var documents :List<userDocument>
        val sp1: SharedPreferences =  mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var id = sp1.getString("id", null)
        if(id==null)
            return documents
        else{
            return mainActivity.dbc.getDocument(id,pcode,name,addr,date)}
    }

    fun updateDocument(type:String, pcode: String, date: String, memo:String):Int{
        val sp1: SharedPreferences =  mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var id = sp1.getString("id", null)
        if(id==null)
            return 0
        else{
            return mainActivity.dbc.inUpdateDocument(id,type,pcode,date,memo)}
    }

    fun isEditMeno(mode:Int):Boolean{
        when(mode){
            1-> return !Document.memo.equals(binding.writeMemo.text.toString())

            2-> return !Memo.equals(binding.writeMemo.text.toString())
            else -> {
                Log.d("tst","mode $mode")
                return false}
        }
    }

}