package com.example.cs571hw9;

import com.example.cs571hw9.MusicInfo.sort_type;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FaceBookShare extends FragmentActivity  {

	WebDialog feedDialog;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }


    private void toLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }


    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	publishFeed();
        }
    }
	
	private void publishFeed() {
		//Bundle b = getIntent().getExtras();
		//String type = b.getString("type");
		//String id = b.getString("id");
		String name="";
		String description="";
		String link="";
		String picture="";
		String type = "type";
		String id = "id";
		
		/*
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
		*/
	    Bundle params = new Bundle();
	    params.putString("name",name);
	    params.putString("properties", "{ 'Look at details ': { 'text': 'here', 'href': '" +
	    		link + "' } }"); 
	    //params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", description);
	    params.putString("link", link);
	    params.putString("picture", picture);
	    
	    feedDialog = (
	        new WebDialog.FeedDialogBuilder(this,
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
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	  
}
