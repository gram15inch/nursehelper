package org.techtown.nursehelper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.apache.commons.lang3.time.DateUtils
import org.techtown.nursehelper.Home.HomeFragment
import org.techtown.nursehelper.Login.loginFragment
import org.techtown.nursehelper.Login.userResist
import org.techtown.nursehelper.databinding.ActivityMainBinding
import java.util.*
import kotlin.random.Random




class MainActivity : AppCompatActivity() {
    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    val dbc :DBC
    val navController by lazy{findNavController(R.id.nav_fragment)}
    //var userItems : MutableList<userSchedule>
    var dbUserItems : MutableList<userSchedule>
    var now : Calendar
    init {
        now = Calendar.getInstance()
        now = DateUtils.truncate(now, Calendar.DAY_OF_MONTH)
        dbc = DBC(this)
        //userItems = createData()
        dbUserItems = mutableListOf<userSchedule>()
      /*  userItems = mutableListOf()
        userItems.addAll(dbc.getSchedule("user1","2021"))*/
    }
    lateinit var usList :List<userSchedule>
    lateinit var LF : loginFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("tstLife","onCreate")



        //nav init
        binding.bottomNavigatinView.setupWithNavController(navController)



        //login check
        val userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if(userInfo.getString("id",null)==null){//first login
            LFInit()
           binding.MainFrame.visibility = View.VISIBLE
        }else {
            val id = userInfo.getString("id", null)
            val pw = userInfo.getString("pw", null)
            if(id != null && pw != null){

                //splash
                binding.imgLogo.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val transition: Transition = Fade()
                    transition.setDuration(400)
                    transition.addTarget(binding.imgLogo)
                    TransitionManager.beginDelayedTransition(binding.root, transition)
                    binding.imgLogo.visibility = View.GONE
                }, 800)

                CoroutineScope(Dispatchers.Main).launch {
                    var rt = -1
                    CoroutineScope(Dispatchers.Default).async {
                        rt =login(id,pw)
                    }.await()
                    when(rt){
                        //성공시
                        1 -> {Log.d("tst","loginSuccess")
                            loginSuccess(id,pw)

                        }
                        //실패시
                        0->{
                            Toast.makeText(this@MainActivity,"아이디 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()}
                        else->{
                            Toast.makeText(this@MainActivity,"오류 code:$rt", Toast.LENGTH_SHORT).show()}
                    }
                }
            }
            else
                Log.d("tst","main loginInit error")
        }

        //logout
        binding.textLogout.setOnClickListener {
            var pref = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
            var edit = pref.edit()
            edit.remove("id")
            edit.remove("pw")
            edit.commit()
            LFInit()
            if(binding.MainFrame.visibility != View.VISIBLE){
                binding.MainFrame.visibility = View.VISIBLE
                Log.d("tst","visible")
            }
        }

        binding.SubFrame.setOnTouchListener { v, event ->
            true
        }
    }


    fun LFInit(){
        LF = loginFragment().apply {
            login = this@MainActivity.login
            resist = this@MainActivity.resist
            idCheck = this@MainActivity.idCheck
            loginSuccess= this@MainActivity.loginSuccess
            resistSucsses = this@MainActivity.resistSucsses
        }

        supportFragmentManager.beginTransaction().apply {
            add(R.id.MainFrame ,LF,"login_tag")
            addToBackStack("login_tag")
            commit()
        }
        Log.d("tst","lfinit")
    }

    fun clearBackStack() {
        supportFragmentManager.apply {
            Log.d("tstback","$backStackEntryCount")
          while(backStackEntryCount >0){
              popBackStack()
          }
        }
    }

    //--

    fun createData():MutableList<userSchedule>{
        var userItems = mutableListOf<userSchedule>()
        var colors = createColor()
        var start = Calendar.getInstance()
        var end = Calendar.getInstance()
        start.time = now.time
        end.time = now.time

        var rand = Random(Calendar.getInstance().timeInMillis)
        var randI = 0
        var randC = 0
        var count = 0
        for(month in 7..9) {
            start.set(android.icu.util.Calendar.MONTH,month)
            for (day in 1..31) {
                randI = rand.nextInt(0, 7)

                for (no in 1..randI) {
                    randC = rand.nextInt(0, 4)
                    start.set(android.icu.util.Calendar.DAY_OF_MONTH, day)
                    start.set(android.icu.util.Calendar.HOUR_OF_DAY, 10 + no)
                    var sex = when(randI%2){
                        1-> "M"
                        else-> "F"
                    }
                    end.time = start.time
                    end.add(Calendar.HOUR,1)
                    var user = userSchedule(++count,
                        "name$day($no)",
                        "addr$no",
                        start.time,end.time, sex, end.time,
                        colors.get(randC))
                    //Log.d("tst","${user.startTime}/${user.color}")
                    userItems.add(user)
                }
            }
        }
        return userItems
    }
    fun createColor():MutableList<Int>{
        var colorList = mutableListOf<Int>()
        colorList.add(Color.parseColor("#FFFB9DA7"))
        colorList.add(Color.parseColor("#FFFCCCD4"))
        colorList.add(Color.parseColor("#FFFBDEA2"))
        colorList.add(Color.parseColor("#FFF2E2C6"))
        return colorList
    }

    fun searchData(date: Date):List<userSchedule> {
        var searchedList: MutableList<userSchedule> = mutableListOf()
        var calData = Calendar.getInstance()
        var calfocus = Calendar.getInstance()
        calfocus.time = date
        for (data in dbUserItems) {

            calData.clear()
            calData.time = data.startTime
            if (calfocus.get(Calendar.YEAR) == calData.get(Calendar.YEAR))
                if (calfocus.get(Calendar.MONTH) == calData.get(Calendar.MONTH))
                    if (calfocus.get(Calendar.DAY_OF_MONTH) == calData.get(Calendar.DAY_OF_MONTH)) {
                        searchedList.add(data)

                }
        }
        return searchedList
    }

    fun sortScheData(list: List<userSchedule>):List<userSchedule>{
        val sort =mutableListOf<userSchedule>()
        sort.addAll(list)
        sort.sortWith(compareBy<userSchedule> { it.startTime }.thenBy { it.endTime }.thenBy { it.name })
        return sort
    }
    fun sortDocData(list: List<userDocument>):List<userDocument>{
        val sort =mutableListOf<userDocument>()
        sort.addAll(list)
        sort.sortByDescending { it.date }
        return sort
    }
    fun insertSchedule(us: userSchedule):Int{
        dbUserItems.add(us)
        return 1
    }

    fun updateSchedule(us:userSchedule):Int{
        dbUserItems.remove(us)
        dbUserItems.add(us)
        return 1
    }
    fun deleteUser(sid:Int){
        for(user in dbUserItems){
            if (user.idCode == sid) {
                Log.d("tst","del : ${user.idCode}==${sid}")

                dbUserItems.remove(user)
                break
            }
        }
    }
    //--
    fun getYearSchedule(id:String,day:String):List<userSchedule>{
        return dbc.getSchedule(id,day)// 11/28 임시수정
    }

    fun getDaySchedule(id: String,day:String):List<userSchedule>{
        Log.d("tstdb",day)
        return dbc.getSchedule(id,day)



    }

    fun getDocument(id:String,pno:String,name:String,addr:String,date:String):List<userDocument>{

        return dbc.getDocument(id,pno,name,addr,date)
    }

   /* fun getUserInfo():Int{
        val sp1: SharedPreferences =  this.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var id = sp1.getString("id", null)
        if(id==null)
            return -1
        else{
            updateDBdata(id,Calendar.getInstance().get(Calendar.YEAR).toString())
            return 1
        }

    }*/

    fun updateDBdata(id:String,date:String){

        CoroutineScope(Dispatchers.Main).launch {
            lateinit var rt : List<userSchedule>
            CoroutineScope(Dispatchers.Default).async {
                rt = getYearSchedule(id,date)
            }.await()
            when(rt){
                //실패시
                null -> Log.d("tst","dbError")
                //성공시
                else-> {dbUserItems.clear()
                        dbUserItems.addAll(rt)
                    Log.d("tstdb","allDB update")
                    homeUpdate?.invoke()
                }
            }
        }
    }

    fun getUserInfo():String{
        val sp1: SharedPreferences =  getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        var id = sp1.getString("id", null)
        if(id==null)
            return ""
        else
            return id
    }
    var homeUpdate  : (()->Unit)? =null


    //Login 객체함수
    val login  = object : (String,String)->Int{
        override fun invoke(id:String,pw:String):Int {

            return  dbc.login(id,pw)
        }
    }
    val resist = object : (userResist)->Int{
        override fun invoke(urd : userResist):Int {

            return  dbc.regist(urd.id,urd.pw,urd.name,urd.sex,urd.pn)
        }
    }
    val idCheck  = object : (String)->Int {
        override fun invoke(id:String):Int {

            return dbc.idCheck(id)
        }
    }
    val loginSuccess  = object : (String,String)->Unit {
        override fun invoke(id:String,pw:String) {
            //navController.popBackStack()


            // id 텍스트뷰에 저장
            binding.textLogout.apply {
                val sp1: SharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE)

                val id = sp1.getString("id", null)
                val pw = sp1.getString("pw", null)
                text = "로그아웃"
            }

            //dbLoad
            updateDBdata(id,Calendar.getInstance().get(Calendar.YEAR).toString())

            if(binding.MainFrame.visibility != View.GONE)
                binding.MainFrame.visibility = View.GONE


        }
    }

    val write  = object : (String,String)->Int{
        override fun invoke(id:String,pw:String):Int {

            return  dbc.login(id,pw)
        }
    }

    val writingSuccess  = object : (String,String)->Unit {
        override fun invoke(id:String,pw:String) {
            navController.popBackStack()



            if(binding.SubFrame.visibility != View.GONE)
                binding.SubFrame.visibility = View.GONE


        }
    }
    val resistSucsses = object : ()->Unit {
        override fun invoke() {


            supportFragmentManager.popBackStack("resist_tag", 1)
        }
    }





    //life cycle
    override fun onStart() {
        super.onStart()
        Log.d("tstlife","onStart")

    }
    override fun onResume() {
        super.onResume()
        Log.d("tstlife","onStart")
    }


}