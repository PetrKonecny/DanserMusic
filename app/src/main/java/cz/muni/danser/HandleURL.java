package cz.muni.danser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.muni.danser.model.DanceRecording;

public class HandleURL extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_url);

        Intent intent = getIntent();
        //DanceRecording searchDanceTrack = new DanceRecording();
        if(Intent.ACTION_SEND.equals(intent.getAction()) && "text/plain".equals(intent.getType()) && intent.hasExtra(Intent.EXTRA_TEXT)){
            String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
            Matcher youtubeM, spotifyM;
            if((youtubeM = Pattern.compile("^https://youtu.be/(.*)$").matcher(extraText)).matches()){
                //searchDanceTrack.setYoutubeId(youtubeM.group(1));
            }
            else if((spotifyM = Pattern.compile("^https://open.spotify.com/track/(\\w+)$").matcher(extraText)).matches()){
                //searchDanceTrack.setSpotifyId(spotifyM.group(1));
            }
            else if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
                String extraSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
                Pattern p1 = Pattern.compile("^(.*) - (.*)$");
                Pattern[] textPatterns = new Pattern[]{
                        Pattern.compile(".*Shazam.*"),
                        Pattern.compile(".*SoundCloud$"),
                        Pattern.compile(".*soundhound\\.com"),
                        Pattern.compile(".*Deezer.*"),
                        Pattern.compile(getString(R.string.regexp_trackid)),
                        Pattern.compile(".*Apple Music.*"),
                        };
                Pattern[] subjectPatterns = new Pattern[]{
                        p1,
                        Pattern.compile("^(.*) - (.*) by .* - SoundCloud$"),
                        Pattern.compile(getString(R.string.regexp_soundhound)),
                        p1,
                        Pattern.compile("TrackID™"),
                        Pattern.compile("^Listen to \"(.*)\" from .* by (.*) on Apple Music\\..*")
                };
                boolean[] artistFirst = new boolean[]{
                        false,
                        true,
                        false,
                        true,
                        false,
                        false
                };
                Matcher mText, mSubject, winner;
                int i;
                for(i=0;i<textPatterns.length;i++){
                    mText = textPatterns[i].matcher(extraText);
                    mSubject = subjectPatterns[i].matcher(extraSubject);
                    if(mText.matches() && mSubject.matches()){
                        if(mText.groupCount() >= 2){
                            winner = mText;
                        }
                        else{
                            winner = mSubject;
                        }
                        //searchDanceTrack = fillRecording(searchDanceTrack, artistFirst[i], winner.group(1), winner.group(2));
                        break;
                    }
                }
            } else {
                //...
            }
        } else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            /*
            TO DO: handle URLs
             */
        }
        /*
        TO DO: search against known danceTracks if any matches by given (not null) attributes
        + show danceTrack detail
        + handle non-matching intents etc...
         */
    }
}