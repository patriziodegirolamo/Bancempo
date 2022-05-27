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

    private var conversations: HashMap<String, Conversation>? = null
    private var conversationAdv: Conversation? = null

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

        acceptButton.setOnClickListener{

        }

        refuseButton.setOnClickListener{

        }

        sharedVM.messages.observe(viewLifecycleOwner){ messagesConv ->
            println("----------MESSAGES ${messagesConv}")
            rv.adapter =
                MessageAdapter(messagesConv.values.sortedBy { x -> x.date }.toList(), sharedVM)
        }

        sharedVM.conversations.observe(viewLifecycleOwner){
            conversations = it
            conversationAdv = conversations!!.values.filter { conv -> conv.idAdv  ==  idAdv}.getOrNull(0)

            if(conversationAdv == null){
                //NON HA CONVERSAZIONE PER QUELL'ANNUNCIO
                //CREA NUOVA CONVERSAZIONE
                println("---------NO CONV PER ADV")
                sendButton.setOnClickListener{
                    if(textMsg.text.isEmpty()){
                        textMsg.error = "Write a message!"
                    }
                    else{
                        sharedVM.createNewConversation(idAdv, idBidder, textMsg.text.toString())
                        textMsg.setText("")
                    }
                }
                println("------CONVERSATIONS ${sharedVM.conversations.value}")
            }
            else {
                //CARICA MESSAGGI VECCHI DELLA CONVERSAZIONE
                println("-----SI CONV PER ADV")
                println("------CONVERSATIONS ${conversationAdv}")
                sharedVM.loadMessages(conversationAdv!!.idConv)

                sendButton.setOnClickListener {
                    if (textMsg.text.isEmpty()) {
                        textMsg.error = "Write a message!"
                    } else {
                        sharedVM.createNewMessage(conversationAdv!!.idConv, textMsg.text.toString()
                        , to = idBidder, from = sharedVM.currentUser.value!!.email)
                        textMsg.setText("")                    }

                }
            }

        }

    }

}