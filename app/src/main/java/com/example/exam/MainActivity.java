package com.example.exam;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    boolean live = true;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    DatabaseReference myRefNick = database.getReference("nickname");

    private static final short MAX_MESSAGE_LENGTH = 255;
    private static final short MAX_NICKNAME_LENGTH = 255;
    EditText mEditTextMessage, mEditNickname;
    TextView mTimeTextView;
    Button mSendButton;
    RecyclerView mMessageRecycler;
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> nicknames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendButton = findViewById(R.id.send_message_b);
        mEditTextMessage = findViewById(R.id.message_input);
        mEditNickname = findViewById(R.id.nickname_input);

        mTimeTextView = (TextView) findViewById(R.id.time);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchTime();
                    }
                });
            }
        }, 0, 1000);

        mMessageRecycler = findViewById(R.id.message_recycler);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        DataAdapter dataAdapter = new DataAdapter(this, messages, nicknames);
        mMessageRecycler.setAdapter(dataAdapter);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mEditTextMessage.getText().toString();
                String nick = mEditNickname.getText().toString();
                String time = mTimeTextView.getText().toString();
                nick = nick + " " + time;
                if (msg.equals("") || nick.equals("")) {
                    Toast.makeText(MainActivity.this,R.string.empty_message_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (msg.length() > MAX_MESSAGE_LENGTH || nick.length() > MAX_NICKNAME_LENGTH) {
                    Toast.makeText(MainActivity.this, R.string.length_message_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                myRef.push().setValue(msg);
                myRefNick.push().setValue(nick);
                mEditTextMessage.setText("");
            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String msg = snapshot.getValue(String.class);
                messages.add(msg);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRefNick.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String nick = snapshot.getValue(String.class);
                nicknames.add(nick);
                dataAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(nicknames.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void fetchTime() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://date.jsontest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsontestAPI api = retrofit.create(JsontestAPI.class);
        Call<ServerTime> serverTimeCall = api.getServerDateTime();
        serverTimeCall.enqueue(new Callback<ServerTime>() {
            @Override
            public void onResponse(Call<ServerTime> call, Response<ServerTime> response) {
                ServerTime serverTime = response.body();
                mTimeTextView.setText(serverTime.getTime());
            }

            @Override
            public void onFailure(Call<ServerTime> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Error" + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}