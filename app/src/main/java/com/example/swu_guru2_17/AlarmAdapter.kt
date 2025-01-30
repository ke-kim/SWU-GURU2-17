package com.example.swu_guru2_17

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class AlarmAdapter(
    private val alarmList: MutableList<AlarmData>,
    private val onAlarmClick: (AlarmData, Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeTextView: TextView = view.findViewById(R.id.alarmTimeTextView)
        val nameTextView: TextView = view.findViewById(R.id.alarmNameTextView)
        val alarmSwitch: Switch = view.findViewById(R.id.alarmSwitch)

        fun bind(alarm: AlarmData, position: Int) {
            val ampm = if (alarm.hour >= 12) "PM" else "AM"
            val hour = if (alarm.hour > 12) alarm.hour - 12 else alarm.hour
            val timeText = String.format("%d:%02d %s", hour, alarm.minute, ampm)

            timeTextView.text = timeText
            nameTextView.text = alarm.name
            alarmSwitch.isChecked = alarm.isEnabled

            itemView.setOnClickListener {
                onAlarmClick(alarm, position)
            }

            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                alarm.isEnabled = isChecked
                updateAlarmStatus(itemView.context, position, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarmList[position], position)
    }

    override fun getItemCount(): Int = alarmList.size

    fun updateAlarms(newAlarms: List<AlarmData>) {
        alarmList.clear()
        alarmList.addAll(newAlarms)
        notifyDataSetChanged()
    }

    private fun updateAlarmStatus(context: Context, position: Int, isEnabled: Boolean) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        alarmList[position].isEnabled = isEnabled
        sharedPreferences.edit().putString("alarms", Gson().toJson(alarmList)).apply()
    }
}
