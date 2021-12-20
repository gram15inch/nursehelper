package org.techtown.nursehelper.Schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.MainActivity
import org.techtown.nursehelper.R
import org.techtown.nursehelper.Schedule.day_item_fragment.DayItemDetailFragment
import org.techtown.nursehelper.databinding.UserItemPBinding
import java.text.SimpleDateFormat
import kotlin.properties.Delegates
class patientAdapter(var mainActivity: MainActivity): RecyclerView.Adapter<patientAdapter.patientHolder>(){

    //searchPatientFragment에서 초기화
    lateinit var patientUpdate : (userPatient)->Unit
    lateinit var keyFocusClear : ()->Unit

    var Users: List<userPatient> by Delegates.observable(emptyList()) { _, old, new ->
        userPatientDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }

    val birthFormat = SimpleDateFormat("yyMMdd")

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): patientHolder {
        val binding = UserItemPBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return patientHolder(binding)
    }

    override fun onBindViewHolder(holder: patientHolder, position: Int) {
        holder.bind(Users[position])
    }

    override fun getItemCount() : Int = Users.size
    inner class patientHolder(var binding: UserItemPBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user:userPatient){
            binding.apply {
                textAddr.text = user.addr
                textName.text = user.name
                textGender.text = user.sex
                textBirth.text = birthFormat.format(user.birth)
            }
        this.itemView.setOnClickListener {
            //부모 프래그먼트
            //키보드내리기,포커스 해제
            keyFocusClear.invoke()
            mainActivity.supportFragmentManager.popBackStack("search_patient", 1)
           patientUpdate.invoke(user)


        }
        }
    }




}