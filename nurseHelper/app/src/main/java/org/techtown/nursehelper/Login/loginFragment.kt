package org.techtown.nursehelper.Login

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.databinding.FragmentLoginBinding


class loginFragment : Fragment() {
    lateinit var binding : FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    //main에서 초기화
    lateinit var login : (String,String)->Int
    lateinit var resist : (userResist)->Int
    lateinit var idCheck : (String)->Int
    lateinit var loginSuccess :(String,String)->Unit
    lateinit var resistSucsses :()->Unit

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(LayoutInflater.from(mainActivity))

       //로그인 버튼
        binding.loginBtn.setOnClickListener{
            val id  = binding.textId.text.toString()
            val pw = binding.textPw.text.toString()

            //키보드 내리기
            val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow( mainActivity.currentFocus?.windowToken, 0)


            CoroutineScope(Dispatchers.Main).launch {
                var rt = -1
                CoroutineScope(Dispatchers.Default).async {
                    rt =login(id,pw)
                }.await()
                when(rt){
                    //성공시
                    1 -> {setUserInfoIdInSharedPref(id,pw)//id,pw 공유변수에 저장!

                        //splash
                        mainActivity.binding.imgLogo.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({
                            val transition: Transition = Fade()
                            transition.setDuration(400)
                            transition.addTarget(mainActivity.binding.imgLogo)
                            TransitionManager.beginDelayedTransition(binding.root, transition)
                            mainActivity.binding.imgLogo.visibility = View.GONE
                        }, 600)

                        loginSuccess(id,pw)
                    }
                    //실패시
                    0->{Toast.makeText(mainActivity,"아이디 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()}
                    else->{Toast.makeText(mainActivity,"오류 code:$rt", Toast.LENGTH_SHORT).show()}
                }
            }

        }

        //새 사용자 버튼
        binding.resisterBtn.setOnClickListener{

            val RF = ResistFragment().apply {
                resist= this@loginFragment.resist
                idCheck = this@loginFragment.idCheck
                resistSucsses = this@loginFragment.resistSucsses
            }

            mainActivity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.MainFrame,RF,"resist_tag")
                addToBackStack("resist_tag")
                commit()
            }
        }

        return binding.root
    }

    fun setUserInfoIdInSharedPref(id:String,pw:String){
        var pref = mainActivity.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var edit = pref.edit()
        if(pref.getString("id",null) == null){ //처음 로그인
            Log.d("tst","처음 로그인하여 아이디를 pref에 넣었습니다."+id)
        }else if(!pref.getString("id",null).equals(id)){ //새로운 id가 입력된경우
            Log.d("tst","기존의 아이디"+pref.getString("id",null)+"가 만료 되어서 "
                    +"아이디 "+id+" 로 교체 되었습니다.")
        }
        edit.putString("id",id)
        edit.putString("pw",pw)
        edit.apply() //비동기 처리
    }


}