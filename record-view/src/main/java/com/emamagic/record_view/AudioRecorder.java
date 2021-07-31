package com.emamagic.record_view;

import android.annotation.SuppressLint;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;


    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }


    public void start(String filePath) throws IOException {
        if (mediaRecorder == null) {
            initMediaRecorder();
        }
        try {
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Log.e("TAG", "start: done");
        } catch (Throwable throwable) {
            Log.e("TAG", "start: "+throwable.getMessage());
        }
    }

    public void stop() {
        try {
            mediaRecorder.stop();
            destroyMediaRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void destroyMediaRecorder() {
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void stopRecording(boolean deleteFile, File recordFile) {
        mediaRecorder.stop();
        destroyMediaRecorder();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    @SuppressLint("DefaultLocale")
    public String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

}
