package org.techtown.nursehelper.Schedule.day_item_fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.databinding.FragmentDayItemDetailBinding
import org.techtown.nursehelper.userSchedule
import java.util.*


class DayItemDetailFragment(var user : userSchedule) : Fragment() {
    val binding by lazy{FragmentDayItemDetailBinding.inflate(layoutInflater)}
    lateinit var mainActivity : MainActivity
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
        binding.run {
            textNameDetail.setText(user.name)
            textAddDetail.setText(user.addr)
            parseCal.time = user.startTime
            textStart.setText("${parseCal.get(Calendar.MONTH)+1}-${parseCal.get(Calendar.DAY_OF_MONTH)} ${parseCal.get(Calendar.HOUR)}:${parseCal.get(Calendar.MINUTE)}")
            parseCal.time = user.endTime
            textEnd.setText("${parseCal.get(Calendar.MONTH)+1}-${parseCal.get(Calendar.DAY_OF_MONTH)} ${parseCal.get(Calendar.HOUR)}:${parseCal.get(Calendar.MINUTE)}")

            /*val layout = mainActivity.binding
            val params = layout.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(20, 10, 20, 10)
            layout.layoutParams = params
            mainActivity.binding.detailContainer.visibility = View.VISIBLE*/
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenerInit()
      /*
        setMainColor(Color.RED)
        colorAdapterInit(binding.colorRecycle)*/
    }

    override fun onStart() {
        super.onStart()

        if(this.arguments?.getString("date")!=null)
            binding.textStart.text = this.arguments?.getString("date").toString()
    }
/*

    fun  colorAdapterInit(recyclerView: RecyclerView){

        var colorA = colorAdapter()
        if(colorA is colorAdapter) {
            colorA.colorList = loadColorData()
            colorA.mainActivity = mainActivity
            colorA.colorFragment = this

            recyclerView.run {
                adapter = colorA
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
        }

    }
    fun loadColorData():MutableList<Int>{
        var colorList = mutableListOf<Int>()

        colorList.add(Color.RED)
        colorList.add(Color.BLUE)
        colorList.add(Color.CYAN)
        colorList.add(Color.GREEN)
        colorList.add(Color.YELLOW)
        colorList.add(Color.MAGENTA)

        return colorList
    }
*/

    fun setMainColor(color: Int) {
        var shape = ContextCompat.getDrawable(mainActivity, R.drawable.color_bg)
        shape?.setTint(color)
        binding.mainColorView.setBackground(shape)
    }

    fun listenerInit(){

        binding.run{
            prevBtn.setOnClickListener {
                mainActivity.supportFragmentManager.popBackStack("day_item_detail", 1)
            }

            /*textNameDetail.run{
                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Toast.makeText(mainActivity, v.text, Toast.LENGTH_SHORT).show()
                        v.clearFocus()
                    }
                    false
                }
            }

            textDateDetail.run {
                setOnClickListener {
                    //두개동시에
                    val frag = dataTimePicker()
                    mainActivity.supportFragmentManager.beginTransaction().run {
                        replace(R.id.detailContainer, frag)
                        addToBackStack("picker")
                        commit()
                    }
                }
            }

            textAddDetail.run {

                addrSaveBtn.setOnClickListener {
                    mainActivity.hideSoftKeyboard()
                    Handler().postDelayed({
                        textAddDetail.clearFocus()
                    }, 80)
                }

                setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                            textNameDetail.visibility = View.GONE
                            textDateDetail.visibility = View.GONE
                            colorRecycle.visibility = View.GONE
                            nameImg.visibility = View.GONE
                            dateImg.visibility = View.GONE
                            mainColorView.visibility = View.GONE

                            addrSaveBtn.visibility = View.VISIBLE
                        Log.d("tst1", "focus")
                    } else if (!hasFocus) {

                            textNameDetail.visibility = View.VISIBLE
                            textDateDetail.visibility = View.VISIBLE
                            colorRecycle.visibility = View.GONE
                            nameImg.visibility = View.VISIBLE
                            dateImg.visibility = View.VISIBLE
                            mainColorView.visibility = View.VISIBLE

                            addrSaveBtn.visibility = View.GONE
                        Log.d("tst1", "hide")
                    }
                }
                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        Toast.makeText(mainActivity, v.text, Toast.LENGTH_SHORT).show()
                        v.clearFocus()
                    }
                    false
                }
            }

            mainColorView.setOnClickListener {
                if (colorRecycle.visibility == View.VISIBLE)
                    colorRecycle.visibility = View.INVISIBLE
                else if(colorRecycle.visibility == View.INVISIBLE )
                    colorRecycle.visibility = View.VISIBLE
                //Log.d("tst1","${binding.mainColorView.visibility} == ${View.VISIBLE}")
            }

*/
        }


        binding.colorRecycle.setOnTouchListener { v, event ->
            true
        }

        this.view?.setOnClickListener {
            binding.colorRecycle.visibility = View.INVISIBLE
        }

    }
}