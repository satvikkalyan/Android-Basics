package edu.iu.habahram.sensorssample

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class SensorViewModel
: ViewModel() {
    private lateinit var shakeSensor: MeasurableSensor

    private var _isDark: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDark: LiveData<Boolean>
        get() = _isDark


    fun initializeSensors(lSensor: MeasurableSensor) {
        shakeSensor = lSensor
        shakeSensor.startListening()
        shakeSensor.setOnSensorValuesChangedListener { values ->
            val x_a = values[0]
            val y_a = values[1]
            val z_a = values[2]
            if(x_a>2 || x_a<-2 || y_a>12 || y_a<-12 || z_a>2 || z_a<-2){
                Log.i("In Sensor","SHAKING!!")
            }
            else{
//                Log.i("In Sensor","NOT SHAKING!!")
            }
        }
    }
}