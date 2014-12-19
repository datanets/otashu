
package com.andrewsummers.otashu.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.andrewsummers.otashu.R;
import com.andrewsummers.otashu.data.EmotionsDataSource;
import com.andrewsummers.otashu.data.NotesDataSource;
import com.andrewsummers.otashu.data.NotesetsDataSource;
import com.andrewsummers.otashu.model.Emotion;
import com.andrewsummers.otashu.model.Note;
import com.andrewsummers.otashu.model.Noteset;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ApprenticeActivity extends Activity implements OnClickListener {

    private File path = Environment.getExternalStorageDirectory();
    private String externalDirectory = path.toString() + "/otashu/";
    private File musicSource = new File(externalDirectory + "otashu_preview.mid");
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private List<Note> notesToInsert = new ArrayList<Note>();
    private Noteset newlyInsertedNoteset = new Noteset();
    private Noteset notesetToInsert = new Noteset();
    private Emotion chosenEmotion = new Emotion();
    private Button buttonYes = null;
    private Button buttonNo = null;
    private Button buttonPlayNoteset = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get specific layout for content view
        setContentView(R.layout.activity_apprentice);

        buttonNo = (Button) findViewById(R.id.button_yes);
        buttonYes = (Button) findViewById(R.id.button_no);

        try {
            // add listeners to buttons
            buttonNo.setOnClickListener(this);
            buttonYes.setOnClickListener(this);

            Button buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
            buttonPlayNoteset.setOnClickListener(this);
        } catch (Exception e) {
            Log.d("MYLOG", e.getStackTrace().toString());
        }

        // disable buttons while playing
        buttonYes.setClickable(false);
        buttonNo.setClickable(false);
        buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
        buttonPlayNoteset.setClickable(false);

        apprenticeAskProcess();

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer aMediaPlayer) {
                // enable play button again
                buttonYes.setClickable(true);
                buttonNo.setClickable(true);
                buttonPlayNoteset.setClickable(true);
            }
        });
    }

    public List<Note> generateNotes(int fromIndex, int toIndex) {
        String[] noteValuesArray = getResources().getStringArray(R.array.note_values_array);

        int randomNoteIndex = 0;
        String randomNote = "";
        float randomLength = 0.0f;
        int randomVelocity = 100;
        float lengthValues[] = {
                0.25f, 0.5f, 0.75f, 1.0f
        };

        for (int i = 0; i < 4; i++) {
            randomNoteIndex = new Random().nextInt((toIndex - fromIndex) + 1) + fromIndex;
            randomNote = noteValuesArray[randomNoteIndex];
            int randomLengthIndex = new Random().nextInt(lengthValues.length);
            randomLength = lengthValues[randomLengthIndex];
            randomVelocity = new Random().nextInt(120 - 60 + 1) + 60;

            Note note = new Note();
            note.setNotevalue(Integer.valueOf((randomNote)));
            note.setLength(randomLength);
            note.setVelocity(randomVelocity);
            note.setPosition(i + 1);

            notesToInsert.add(note);

            Log.d("MYLOG", "random note: " + randomNote);
            Log.d("MYLOG", "random length: " + randomLength);
            Log.d("MYLOG", "random velocity: " + randomVelocity);
        }

        return notesToInsert;
    }

    public void askQuestion() {
        TextView apprenticeText = (TextView) findViewById(R.id.apprentice_text);

        // get random emotion
        EmotionsDataSource eds = new EmotionsDataSource(this);
        chosenEmotion = eds.getRandomEmotion();
        eds.close();

        apprenticeText.setText("Does this sound " + chosenEmotion.getName() + "?");
    }

    public void playMusic(File musicSource) {
        // get media player ready
        mediaPlayer = MediaPlayer.create(this, Uri.fromFile(musicSource));

        // play music
        mediaPlayer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_no:
                // TODO: do something with "no" response (learning)

                // disable buttons while playing
                buttonYes = (Button) findViewById(R.id.button_yes);
                buttonYes.setClickable(false);
                buttonNo = (Button) findViewById(R.id.button_no);
                buttonNo.setClickable(false);
                buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
                buttonPlayNoteset.setClickable(false);

                // try another noteset
                apprenticeAskProcess();

                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer aMediaPlayer) {
                        // enable play button again
                        buttonYes.setClickable(true);
                        buttonNo.setClickable(true);
                        buttonPlayNoteset.setClickable(true);
                    }
                });

                break;
            case R.id.button_yes:

                // disable buttons while playing
                buttonYes = (Button) findViewById(R.id.button_yes);
                buttonYes.setClickable(false);
                buttonNo = (Button) findViewById(R.id.button_no);
                buttonNo.setClickable(false);
                buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
                buttonPlayNoteset.setClickable(false);

                // save noteset
                notesetToInsert.setEmotion((int) chosenEmotion.getId());
                saveNoteset(v, notesetToInsert);

                // save notes
                for (int i = 0; i < notesToInsert.size(); i++) {
                    Note note = notesToInsert.get(i);
                    note.setNotesetId(newlyInsertedNoteset.getId());

                    saveNote(v, notesToInsert.get(i));
                }

                // try another noteset
                apprenticeAskProcess();

                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer aMediaPlayer) {
                        // enable play button again
                        buttonYes.setClickable(true);
                        buttonNo.setClickable(true);
                        buttonPlayNoteset.setClickable(true);
                    }
                });

                break;
            case R.id.button_play_noteset:

                // disable buttons while playing
                buttonYes = (Button) findViewById(R.id.button_yes);
                buttonYes.setClickable(false);
                buttonNo = (Button) findViewById(R.id.button_no);
                buttonNo.setClickable(false);
                buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
                buttonPlayNoteset.setClickable(false);

                // play generated notes for user
                playMusic(musicSource);

                mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer aMediaPlayer) {
                        // enable play button again
                        buttonYes.setClickable(true);
                        buttonNo.setClickable(true);
                        buttonPlayNoteset.setClickable(true);
                    }
                });

                break;
        }
    }

    public void apprenticeAskProcess() {

        // clear old generated notes
        notesToInsert.clear();

        List<Note> notes = new ArrayList<Note>();

        // stay within 39..50 for now (C4..B4)
        notes = generateNotes(39, 50);

        // get default instrument for playback
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultInstrument = sharedPref.getString("pref_default_instrument", "");

        GenerateMusicActivity generateMusic = new GenerateMusicActivity();
        generateMusic.generateMusic(notes, musicSource, defaultInstrument);

        // does generated noteset sounds like chosen emotion?
        askQuestion();

        // disable play button while playing
        buttonPlayNoteset = (Button) findViewById(R.id.button_play_noteset);
        buttonPlayNoteset.setClickable(false);

        // play generated notes for user
        playMusic(musicSource);

        // return to previous activity when done playing
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer aMediaPlayer) {
                // enable play button again
                buttonPlayNoteset.setClickable(true);
            }
        });
    }

    /**
     * Save noteset data.
     * 
     * @param v Incoming view.
     * @param data Incoming string of data to be saved.
     */
    private void saveNoteset(View v, Noteset noteset) {

        // save noteset in database
        NotesetsDataSource nds = new NotesetsDataSource(this);
        newlyInsertedNoteset = nds.createNoteset(noteset);
        nds.close();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context,
                context.getResources().getString(R.string.noteset_saved),
                duration);
        toast.show();
    }

    private void saveNote(View v, Note note) {

        Log.d("MYLOG", Long.toString(note.getNotesetId()));
        Log.d("MYLOG", Integer.toString(note.getNotevalue()));

        // save noteset in database
        NotesDataSource nds = new NotesDataSource(this);
        nds.createNote(note);
        nds.close();

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context,
                context.getResources().getString(R.string.noteset_saved),
                duration);
        toast.show();
    }

    /**
     * onBackPressed override used to stop playing music when done with activity
     */
    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying()) {
            // stop playing music
            mediaPlayer.stop();
        }

        super.onBackPressed();
    }
}
