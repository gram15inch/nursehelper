package org.techtown.nursehelper.Document

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import org.techtown.nursehelper.*
import org.techtown.nursehelper.databinding.UserItemBinding
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class userDocumentAdapter(val mainActivity: MainActivity) : RecyclerView.Adapter<userDocumentAdapter.documentItemHolder>(){
    val dateFormat = SimpleDateFormat("MM-dd")
    var Documents: List<userDocument> by Delegates.observable(emptyList()) { _, old, new ->
        userDocumentDiff(old, new).calculateDiff().dispatchUpdatesTo(this)
    }

    //DocumentFragment에서 초기화
    lateinit var dateAdapter: ()->Unit
    override fun onBindViewHolder(holder: documentItemHolder, position: Int) {
        holder.binding.apply {
            textName.text = Documents[position].name
            textAddr.text = Documents[position].addr
            textStart.text = dateFormat.format(Documents[position].date)
            textEnd.text = ""
        }
        //쓰기로 이동
        holder.itemView.setOnClickListener {

            var writingFragment = WritingFragment(1).apply {
                Document = this@userDocumentAdapter.Documents[position]
            dateAdapter =this@userDocumentAdapter.dateAdapter}
            val imm: InputMethodManager = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow( mainActivity.currentFocus?.windowToken, 0)

            //var writingFragment = StockFragment()
            mainActivity.supportFragmentManager.beginTransaction().run{
                add(R.id.SubFrame,writingFragment)
                addToBackStack("write")
                commit()
            }
            if(mainActivity.binding.SubFrame.visibility != View.VISIBLE)
                mainActivity.binding.SubFrame.visibility = View.VISIBLE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): documentItemHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(mainActivity))
        return documentItemHolder(binding)
    }

    override fun getItemCount() : Int = Documents.size

    inner class documentItemHolder(var binding:UserItemBinding): RecyclerView.ViewHolder(binding.root){}



}