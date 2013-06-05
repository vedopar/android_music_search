package com.example.cs571hw9;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainFragment extends Fragment {
	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private Button publishButton;
	private Button listenSampleButton;

	
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState) {
	    View view = inflater.inflate(R.layout.activity_face_book, container, false);
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	    
	    publishButton = (Button) view.findViewById(R.id.publishButton);
	    publishButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            publishFeedDialog();        
	        }
	    });
	    
	    listenSampleButton=(Button)view.findViewById(R.id.sample);
	    listenSampleButton.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	String url = "http://rovimusic.rovicorp.com/playback.mp3?c=LJ9SjGxeoOlEa_u2Ogl3DTqpU4hxl5saPDaNIVORh-E=&f=J";
	            MediaPlayer mediaPlayer = new MediaPlayer();
	            try {
	            	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	                mediaPlayer.setDataSource(url);
	                mediaPlayer.prepare();
	                mediaPlayer.start();
				} catch (Exception e) {
					// TODO: handle exception
				}
	        }
	    });

	    return view;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        publishButton.setVisibility(View.VISIBLE);
	        Bundle b = getActivity().getIntent().getExtras();
	        if (b.getString("type").equals("songs")) {
	        	listenSampleButton.setVisibility(View.VISIBLE);
			}
	        
	    } else if (state.isClosed()) {
	        publishButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void publishFeedDialog() {
		Bundle b = getActivity().getIntent().getExtras();
		String type = b.getString("type");
		String id = b.getString("id");
		String name=null;
		String description=null;
		String link=null;
		String picture=null;
		
		if (type.equals("songs")) {
			name = b.getString("title"+id);
			description = "I like song "+name+" composed by " + b.getString("composer"+id)+"\n Performer: "+b.getString("performer"+id);
			link = b.getString("detail"+id);
			picture=b.getString("cover");

		}
		
		else if (type.equals("albums")){
			name = b.getString("title"+id);
			description = "I like "+name+" released in " + b.getString("year"+id)+" \n Artist: "+b.getString("artist"+id)+" \n Genre: "+b.getString("genre"+id);
			link = b.getString("detail"+id);
			picture=b.getString("cover"+id);
		}
		
		else if (type.equals("artists")){
			name = b.getString("name"+id);
			description = "I like "+name+" who is active since  " + b.getString("year"+id)+" \n Genre of Music is: "+b.getString("genre"+id);
			link = b.getString("detail"+id);
			picture=b.getString("cover"+id);
		}
		
	    Bundle params = new Bundle();
	    params.putString("name",name);
	    params.putString("properties", "{ 'Look at details ': { 'text': 'here', 'href': '" +
	    		link + "' } }"); 
	    //params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", description);
	    params.putString("link", link);
	    params.putString("picture", picture);
	    
	    



	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getActivity(),
	                            "successfully post",
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getActivity().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
}
