package com.bancempo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.data.Conversation
import com.bancempo.data.MessageAdapter
import com.bancempo.models.SharedViewModel


class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val sharedVM: SharedViewModel by activityViewModels()

    private var currentConversation: Conversation? = null

    private lateinit var title: String
    private lateinit var idAdv: String
    private lateinit var idBidder: String

    private lateinit var sendButton: Button

    private lateinit var textAcceptOrRefuse: TextView
    private lateinit var acceptButton: Button
    private lateinit var refuseButton: Button

    private lateinit var textMsg: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.recycler_gchat)
        rv.layoutManager = LinearLayoutManager(context)


        title = arguments?.getString("title")!!
        idAdv = arguments?.getString("idAdv")!!
        idBidder = arguments?.getString("idBidder")!!

        sendButton = view.findViewById(R.id.button_gchat_send)
        textMsg = view.findViewById(R.id.edit_gchat_message)

        textAcceptOrRefuse = view.findViewById(R.id.textAcceptOrRefuse)
        acceptButton = view.findViewById(R.id.acceptProposal)
        refuseButton = view.findViewById(R.id.refuseProposal)


        currentConversation = sharedVM.conversations.value!!.getOrDefault(idAdv, null)

        if(idBidder == sharedVM.currentUser.value!!.email){
            textAcceptOrRefuse.visibility = View.VISIBLE
            acceptButton.visibility = View.VISIBLE
            refuseButton.visibility = View.VISIBLE
        }
        else{
            textAcceptOrRefuse.visibility = View.GONE
            acceptButton.visibility = View.GONE
            refuseButton.visibility = View.GONE
        }

        sendButton.setOnClickListener{
            if(currentConversation == null){
                if(textMsg.text.isEmpty()){
                    textMsg.error = "Write a message!"
                }
                else{
                    sharedVM.createNewConversation(idAdv, idBidder, textMsg.text.toString())
                    sharedVM.loadMessages(idAdv)
                    textMsg.setText("")
                }
            }
            else{
                if (textMsg.text.isEmpty()) {
                    textMsg.error = "Write a message!"
                } else {
                    sharedVM.createNewMessage(idAdv, textMsg.text.toString()
                        , to = idBidder, from = sharedVM.currentUser.value!!.email)
                    textMsg.setText("")
                }
            }

        }


        var idConv: String? = null
        val _convs = sharedVM.conversations.value!!.values.filter { x -> !x.closed && x.idAdv == idAdv}
        if( _convs.isNotEmpty())
            idConv = _convs[0].idConv

        acceptButton.setOnClickListener{
            println("now: setta $idAdv come accepted")
            sharedVM.bookAdv(idAdv)
            acceptButton.visibility = View.GONE
            refuseButton.visibility = View.GONE
            textAcceptOrRefuse.visibility = View.GONE
        }

        refuseButton.setOnClickListener{
            println("now: setta $idConv")
            sharedVM.closeConversation(idConv!!)
            acceptButton.visibility = View.GONE
            refuseButton.visibility = View.GONE
            textAcceptOrRefuse.visibility = View.GONE
        }

        sharedVM.messages.observe(viewLifecycleOwner){ messagesConv ->
            rv.adapter =
                MessageAdapter(messagesConv.values.sortedBy { x -> x.date }.toList(), sharedVM)
        }

    }

}