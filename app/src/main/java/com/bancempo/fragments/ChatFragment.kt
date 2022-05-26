package com.bancempo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bancempo.R
import com.bancempo.data.Conversation
import com.bancempo.data.Message
import com.bancempo.models.SharedViewModel


class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val sharedVM: SharedViewModel by activityViewModels()

    private var conversations: HashMap<String, Conversation>? = null
    private var conversationAdv: Conversation? = null
    private var messagesConv: HashMap<String, Message>? = null

    private lateinit var title: String
    private lateinit var idAdv: String
    private lateinit var idBidder: String

    private lateinit var sendButton: Button
    private lateinit var textMsg: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("------BUNDLE CHAT ${arguments}")
        println("------CONVERSATIONS ${sharedVM.conversations.value}")

        title = arguments?.getString("title")!!
        idAdv = arguments?.getString("idAdv")!!
        idBidder = arguments?.getString("idBidder")!!

        sendButton = view.findViewById(R.id.button_gchat_send)
        textMsg = view.findViewById(R.id.edit_gchat_message)



        sharedVM.messages.observe(viewLifecycleOwner){
            messagesConv = it
            println("----------MESSAGES ${messagesConv}")
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
                        sharedVM.createNewMessage(conversationAdv!!.idConv, textMsg.text.toString())
                        textMsg.setText("")                    }

                }
            }

        }

    }

}