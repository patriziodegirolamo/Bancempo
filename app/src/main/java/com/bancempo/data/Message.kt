package com.bancempo.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.models.SharedViewModel


data class Message(
    val idMsg: String,
    val idConv: String,
    val date: String,
    val text: String,
    val from: String,
    val to: String,
    val readed: Boolean
)

private object Const {
    const val MYMESSAGE = 1
    const val OTHERMESSAGE = 0
}

class MessageAdapter(
    private val messageList: List<Message>,
    private val sharedVM: SharedViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int = messageList.size

    inner class MyMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val dateMsg = v.findViewById<TextView>(R.id.text_gchat_date_me)
        private val textMsg = v.findViewById<TextView>(R.id.text_gchat_message_me)
        private val timeMsg = v.findViewById<TextView>(R.id.text_gchat_timestamp_me)

        fun bind(mes: Message) {
            val arrDate = mes.date.split(" ")
            val effDate = arrDate[0]
            val arrTime = arrDate[1].split(":")
            val effTime = arrTime[0].plus(":").plus(arrTime[1])

            textMsg.text = mes.text
            dateMsg.text = effDate
            timeMsg.text = effTime
        }
    }

    inner class OtherMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val nickname = v.findViewById<TextView>(R.id.text_gchat_user_other)
        private val dateMsg = v.findViewById<TextView>(R.id.text_gchat_date_other)
        private val textMsg = v.findViewById<TextView>(R.id.text_gchat_message_other)
        private val timeMsg = v.findViewById<TextView>(R.id.text_gchat_timestamp_other)

        fun bind(mes: Message) {
            val arrDate = mes.date.split(" ")
            val effDate = arrDate[0]
            val arrTime = arrDate[1].split(":")
            val effTime = arrTime[0].plus(":").plus(arrTime[1])
            val sender = sharedVM.users.value!![mes.from]

            textMsg.text = mes.text
            dateMsg.text = effDate
            timeMsg.text = effTime
            nickname.text = sender!!.nickname
            if (mes.from != sharedVM.currentUser.value!!.email) {
                sharedVM.readMessage(mes.idMsg)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (sharedVM.currentUser.value!!.email == messageList[position].from)
            Const.MYMESSAGE
        else Const.OTHERMESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Const.MYMESSAGE) {
            val vg = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_chat_me, parent, false)
            MyMessageViewHolder(vg)
        } else {
            val vg = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_chat_other, parent, false)
            OtherMessageViewHolder(vg)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Const.MYMESSAGE) {
            (holder as MyMessageViewHolder).bind(messageList[position])
        } else {
            (holder as OtherMessageViewHolder).bind(messageList[position])
        }

    }

}