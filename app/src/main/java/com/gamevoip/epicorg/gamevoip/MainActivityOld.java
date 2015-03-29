package com.gamevoip.epicorg.gamevoip;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.content.Context;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * Activity di test per verificare il funzionamento di AudioStream
 *
 *@author Luca
 */

public class MainActivityOld extends ActionBarActivity {

    private AudioGroup audioGroup = null;
    private AudioStream audioStream = null;
    private boolean _doubleBackToExitPressedOnce  = false;

    /**
     *
     * Inizializza l'AudioGroup, mixer di più AudioStream
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioGroup = new AudioGroup();
        audioGroup.setMode(AudioGroup.MODE_NORMAL);
        setContentView(R.layout.activity_main_old);
    }


    /**
    @Override
    public void onBackPressed() {
        if (_doubleBackToExitPressedOnce) {
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this._doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.info_press_again_to_quit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                _doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    */



    /**
     * Inizializza l'AudioStream (la porta locale viene assegnata casualmente)
     *
     * @param view
     */
    public void init(View view){

        try {

            EditText myiptext = (EditText) findViewById(R.id.myip);

            audioStream = new AudioStream(InetAddress.getByName(myiptext.getText().toString()));
            audioStream.setCodec(AudioCodec.AMR);
            audioStream.setMode(RtpStream.MODE_NORMAL);


            System.out.println(audioStream.getLocalPort());

            TextView textView = (TextView) findViewById(R.id.textView);
            System.out.println(audioStream.getLocalPort());
            Integer port = audioStream.getLocalPort();
            textView.setText(port.toString());

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * Associa l'AudioStream con un altro tramite IP e porta e inizia la comunicazione
     *
     * @param view
     */
    public void asociate(View view){

        try {

            EditText otheriptext = (EditText) findViewById(R.id.otherip);
            EditText otherporttext = (EditText) findViewById(R.id.otherport);

            audioStream.associate(InetAddress.getByName(otheriptext.getText().toString()), Integer.parseInt(otherporttext.getText().toString()));
            audioStream.join(audioGroup);
            AudioManager Audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            Audio.setMode(AudioManager.MODE_IN_CALL);
            Audio.setSpeakerphoneOn(false);


        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     * Attiva e disattiva il vivavoce
     *
     * @param view
     */
    public void vivavoce(View view) {

        AudioManager Audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (Audio.isSpeakerphoneOn())
            Audio.setSpeakerphoneOn(false);
        else {
            Audio.setSpeakerphoneOn(true);
            for (int i = 0; i < 100; i++) {
                Audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, Audio.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),AudioManager.FLAG_SHOW_UI);
                Audio.setStreamVolume(AudioManager.STREAM_MUSIC, Audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.FLAG_SHOW_UI);
                Audio.setStreamVolume(AudioManager.STREAM_SYSTEM, Audio.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),AudioManager.FLAG_SHOW_UI);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
