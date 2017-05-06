package com.example.monko.foreach;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText inputMsg;
    private Button send;
    private TextView chatConversation;
    private String user_id,roomName;
    private DatabaseReference Room;
    private DatabaseReference mdatabseusers;


    private String tempKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        inputMsg=(EditText)findViewById(R.id.inputmsg);
        send=(Button)findViewById(R.id.send);
        chatConversation=(TextView)findViewById(R.id.msg);
        user_id=getIntent().getExtras().get("user_id").toString();
        roomName=getIntent().getExtras().get("roomName").toString();
        setTitle(roomName);
        Room= FirebaseDatabase.getInstance().getReference().child("Rooms").child(roomName);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        mdatabseusers=ref.child("users").child(user_id).child("name");
        mdatabseusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String userName = dataSnapshot.getValue(String.class);

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (inputMsg.getText().toString()!="") {
                            Map<String, Object> map = new HashMap<String, Object>();
                            tempKey = Room.push().getKey();
                            Room.updateChildren(map);
                            DatabaseReference message = Room.child(tempKey);
                            Map<String, Object> map2 = new HashMap<String, Object>();
                            map2.put("name", userName);
                            map2.put("msg", inputMsg.getText().toString());
                            message.updateChildren(map2);
                            inputMsg.setText("");
                        }


                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        Room.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversation(dataSnapshot);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
private String chatMsg,chatUserName;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i=dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            chatMsg= (String) ((DataSnapshot)i.next()).getValue();

            chatUserName= (String) ((DataSnapshot)i.next()).getValue();
            chatConversation.append( chatUserName+" :   "+chatMsg+"\n");


        }
    }
}
