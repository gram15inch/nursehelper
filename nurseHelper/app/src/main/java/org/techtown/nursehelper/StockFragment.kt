package org.techtown.nursehelper

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.techtown.nursehelper.databinding.FragmentStockBinding
import org.techtown.nursehelper.databinding.FragmentWritingBinding


class StockFragment : Fragment() {
    lateinit var binding: FragmentStockBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentStockBinding.inflate(layoutInflater)


        binding.apply {
            s1.setOnClickListener {  Log.d("tstclk","click s1")}
            l1.setOnClickListener {  Log.d("tstclk","click l1")}
            inf1.setOnClickListener {  Log.d("tstclk","click inf1")}
            f1.setOnClickListener {  Log.d("tstclk","click f1")}

            s1.setOnTouchListener { v, event ->
                Log.d("tstclk","touch s1")
                true
            }
        }
        this.view?.setOnClickListener {  Log.d("tstclk","click fragment")}

        return binding.root
    }


}