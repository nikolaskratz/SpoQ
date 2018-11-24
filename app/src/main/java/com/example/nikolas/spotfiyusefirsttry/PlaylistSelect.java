package com.example.nikolas.spotfiyusefirsttry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class PlaylistSelect extends AppCompatActivity {

    String playlistID;
    String playlistUser;

    PlaylistInfo playlistInfo = new PlaylistInfo();
    private Button b1,b2,b3,b4,b5,b6,b7,b8;

    public static PlaylistSelect playlistSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_select);
        playlistSelect=this;

        initControl();
        setPlaylistNameButton();
        listen();
    }

    private void initControl(){
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        b6 = (Button) findViewById(R.id.button6);
        b7 = (Button) findViewById(R.id.button7);
        b8 = (Button) findViewById(R.id.button8);
    }

    public void listen(){
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(0);
                playlistUser=playlistInfo.getPlaylistUser().get(0);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(1);
                playlistUser=playlistInfo.getPlaylistUser().get(1);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(2);
                playlistUser=playlistInfo.getPlaylistUser().get(2);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(3);
                playlistUser=playlistInfo.getPlaylistUser().get(3);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(4);
                playlistUser=playlistInfo.getPlaylistUser().get(4);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(5);
                playlistUser=playlistInfo.getPlaylistUser().get(5);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(6);
                playlistUser=playlistInfo.getPlaylistUser().get(6);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playlistID=playlistInfo.getPlaylistID().get(7);
                playlistUser=playlistInfo.getPlaylistUser().get(7);
                startActivity(new Intent(PlaylistSelect.this, PlayQuiz.class));
                finish();
            }
        });
    }

    void setPlaylistNameButton (){
        b1.setText(playlistInfo.getPlaylistName().get(0));
        b2.setText(playlistInfo.getPlaylistName().get(1));
        b3.setText(playlistInfo.getPlaylistName().get(2));
        b4.setText(playlistInfo.getPlaylistName().get(3));
        b5.setText(playlistInfo.getPlaylistName().get(4));
        b6.setText(playlistInfo.getPlaylistName().get(5));
        b7.setText(playlistInfo.getPlaylistName().get(6));
        b8.setText(playlistInfo.getPlaylistName().get(7));

    }

    public String getPlaylistID() {
        return playlistID;
    }

    public String getPlaylistUser() {
        return playlistUser;
    }

    public static PlaylistSelect getPlaylistSelect() {
        return playlistSelect;
    }
}
