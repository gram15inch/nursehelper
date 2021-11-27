package org.techtown.nursehelper.Login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.databinding.FragmentResistBinding


class ResistFragment : Fragment() {
    lateinit var mainActivity : MainActivity
    lateinit var binding : FragmentResistBinding

    //id,pw 인증상태
    var idState :Int =0
    var pwState :Int =0

    // LoginFragment 에서 초기화
    lateinit var idCheck : (String)->Int
    lateinit var resist : (userResist)->Int
    lateinit var resistSucsses : ()->Unit

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentResistBinding.inflate(LayoutInflater.from(mainActivity))

        //아이디 중복확인
        binding.textRId.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                binding.textRId.background.setTint(Color.CYAN)
            }else{
                if(binding.textRId.text.toString() != "")
                CoroutineScope(Dispatchers.Main).launch {
                    var rt = -2
                    CoroutineScope(Dispatchers.Default).async {

                        rt=  idCheck(binding.textRId.text.toString())
                    }.await()
                    when(rt){
                        1->{ Toast.makeText(mainActivity,"아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show()
                            v.requestFocus()
                            idState = 0
                        }
                        0 ->{ binding.textRId.background.setTint(Color.GRAY)
                            idState = 1
                        }
                        else-> {Toast.makeText(mainActivity,"오류 code:$rt", Toast.LENGTH_SHORT).show()
                            idState = 0
                        }
                    }
                }
            }
        }

        //비밀번호(1,2) 일치확인
        binding.textRPw2.addTextChangedListener (
            object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                    if(binding.textRPw1.text.toString()==s.toString()){
                        pwState = 1
                        binding.textRPw2.background.setTint(Color.GRAY)
                    }
                    else{
                        pwState = 0
                        binding.textRPw2.background.setTint(Color.RED)
                    }
                }
            }
                )

        //사용자 계정 생성 제출
        binding.resisterBtn2.setOnClickListener {
            //키보드 내리기
            val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow( mainActivity.currentFocus?.windowToken, 0)

            if(idState == 1 && pwState == 1){
                CoroutineScope(Dispatchers.Main).launch {
                    var rt = -2
                    CoroutineScope(Dispatchers.Default).async {

                        binding.apply {
                           val sex = when(radioGRSex.checkedRadioButtonId) {
                               radioMRBtn.id->"M"
                               radioFRBtn.id->"F"
                               else -> ""
                           }

                            val user = userResist(
                                textRId.text.toString(),
                                textRPw2.text.toString(),
                                textRName.text.toString(),
                                sex,
                                textIRPN.text.toString()
                            )
                            rt=  resist(user)
                        }
                        Log.d("tst","계정생성")
                    }.await()
                    when(rt){
                        //성공시
                        1 -> {resistSucsses()}
                        //실패시
                        0->{
                            Toast.makeText(mainActivity,"계정생성 실패.", Toast.LENGTH_SHORT).show()}
                        else->{
                            Toast.makeText(mainActivity,"오류 code:$rt", Toast.LENGTH_SHORT).show()}
                    }
                }
            }
            else {
                if(idState !=1)
                    Log.d("tst","id확인")
                if(pwState !=1)
                    Toast.makeText(mainActivity,"비밀번호 불일치", Toast.LENGTH_SHORT).show()
            }




        }

        return binding.root
    }
    
    

}