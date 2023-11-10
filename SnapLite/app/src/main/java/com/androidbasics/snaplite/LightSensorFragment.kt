package edu.iu.habahram.sensorssample

import android.graphics.Color
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import edu.iu.habahram.sensorssample.databinding.FragmentLightSensorBinding


/**
 * A simple [Fragment] subclass.
 * Use the [LightSensorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LightSensorFragment : Fragment() {
    private val TAG = "MainActivity"
    private var _binding: FragmentLightSensorBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLightSensorBinding.inflate(inflater, container, false)
        val view = binding.root
        val viewModel : SensorViewModel by activityViewModels()
        viewModel.initializeSensors(AccelerometerSensor(this.requireContext()))
        binding.lifecycleOwner = viewLifecycleOwner

        if (viewModel.isDark.value == false){
            binding.tvLight.text = "It is Shaked"
            binding.tvLight.setTextColor(Color.WHITE)
            binding.root.setBackgroundColor(Color.DKGRAY)
        }
        else{
            binding.tvLight.text = "It is not Shaked"
            binding.tvLight.setTextColor(Color.RED)
            binding.root.setBackgroundColor(Color.DKGRAY)
        }
        binding.floatingActionButton.setOnClickListener{
            val action = LightSensorFragmentDirections.actionLightSensorFragmentToTakePhotoFragment()
            view.findNavController().navigate(
                action
            )
        }

      return view
    }





}