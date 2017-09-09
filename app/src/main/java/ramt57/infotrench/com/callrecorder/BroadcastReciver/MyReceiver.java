package ramt57.infotrench.com.callrecorder.BroadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import ramt57.infotrench.com.callrecorder.SqliteDatabase.DatabaseHelper;
import ramt57.infotrench.com.callrecorder.contacts.ContactProvider;

/**
 * Created by sandhya on 22-Aug-17.
 */

public abstract class MyReceiver extends BroadcastReceiver {
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    static MediaRecorder recorder= new MediaRecorder();
    static AudioManager audioManager;
    static File audiofile;
    Context context;
    public static boolean record = false;


    public MyReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

        } else {
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                state = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }

    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
    }

    protected void onMissedCall(Context ctx, String number, Date start) {
    }

    protected void onIncomingCallAnswered(Context context, String savedNumber, Date callStartTime) {
    }

    public void onCallStateChanged(Context context, int state, String number) {
        if (lastState == state) {
            //No change
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
//
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, savedNumber, callStartTime);
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //call ended
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    // a miss call
                    onMissedCall(context, savedNumber, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    isIncoming = false;
                } else {
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
    public  void startRecord(String name){
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        int source=Integer.parseInt(SP.getString("RECORDER","2"));
        File sampleDir;
        String dir= ContactProvider.getFolderPath(context);
        if(dir.isEmpty()){
            sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/CallRecorder");
        }else {
            sampleDir = new File(dir);
        }
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String file_name = name;
        try {
            audiofile = File.createTempFile(file_name, ".3gp", sampleDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            recorder.prepare();
            record = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    public void stopRecording() {
        if (record){
            recorder.stop();
        }
//        if(audioManager!=null){
//            audioManager.setSpeakerphoneOn(false);
//        }
    }

//    commente out code
//switch (source){
//            case 0:
//                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                break;
//            case 1:
//                audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setMode(AudioManager.MODE_IN_CALL);
//                audioManager.setSpeakerphoneOn(true);
//                //speaker
//                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                break;
//            case 2:
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//                break;
//            case 3:
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
//                break;
//            case 4:
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
//                break;
//            default:
//                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
//                break;
//        }
}


