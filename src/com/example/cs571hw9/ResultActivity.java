package com.example.cs571hw9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cs571hw9.MusicInfo.sort_type;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends ListActivity {
//	public class ResultActivity extends Activity {
	TextView result_title;
	String title;
	String search_keyword;
	String search_sort;
	Intent intent;
	JSONArray  result_array;
	String[][] detail_array;
	MusicInfo[] info_array;
	sort_type search_sort_type;
	
	int result_row_id;
	int row_height;
	int row_ele_width;
	int chosen_row=0;
	boolean isChosen=false;
	
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
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
        	isChosen=true;
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
        	isChosen=true;
            Session.openActiveSession(this, true, statusCallback);
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		
		setContentView(R.layout.activity_result);
		intent=this.getIntent();
		search_keyword=intent.getExtras().getString("search_keyword");
		search_sort=intent.getExtras().getString("search_sort");
		Display display = getWindowManager().getDefaultDisplay();
		//Point size = new Point();
		int width = display.getWidth(); 
		//int height = display.getHeight();
		switch(intent.getExtras().getInt("search_sort_type")){
			case 0:{
				search_sort_type=sort_type.ARTIST;
				result_row_id=R.layout.artist_result_row;
				MusicInfo.icon_height=width/4;
				MusicInfo.icon_width=MusicInfo.icon_height;
				break;
			}
			case 1:{
				search_sort_type=sort_type.ALBUM;
				result_row_id=R.layout.album_result_row;
				MusicInfo.icon_height=width/5;
				MusicInfo.icon_width=MusicInfo.icon_height;
				break;
			}
			case 2:{
				search_sort_type=sort_type.SONG;
				result_row_id=R.layout.song_result_row;
				MusicInfo.icon_height=width/4;
				MusicInfo.icon_width=MusicInfo.icon_height;
				break;
			}
		}
		title="Input:"+search_keyword+"  Sort:"+search_sort;
		result_title=(TextView)this.findViewById(R.id.result_title);
		//result_title.setText(title);
		//row_height=getResources().getDimensionPixelSize(R.dimen.row_height);
		//MusicInfo.icon_height=getResources().getDimensionPixelSize(R.dimen.row_height);
		//row_ele_width=getResources().getDimensionPixelSize(R.dimen.row_ele_width);
		//MusicInfo.icon_width=getResources().getDimensionPixelSize(R.dimen.row_ele_width);
		
		new RetreiveJsonTask().execute();
		//setListAdapter(new detailAdapter());
       //Toast.makeText( this, "Response:"+response, Toast.LENGTH_SHORT).show();
	}
	
    class RetreiveJsonTask extends AsyncTask<String, Integer,Void> {

    	String response;
    	String result_str;
        @Override
        protected void onPreExecute() {
         // update the UI immediately after the task is executed
         super.onPreExecute();              
        }
		protected Void doInBackground(String... params) {
			search_keyword=URLEncoder.encode(search_keyword);
	   		String sUrl="http://cs-server.usc.edu:35372/examples/servlets/hw8_servlet?search_keyword="+search_keyword+"&search_sort="+search_sort;

			URL url;
	         response="";
	         try {
	                 url = new URL(sUrl);
	                 BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
	                 String line;
	                 try {
	                         while ((line = rd.readLine()) != null) {
	                                 response=line;
	                         }
	                 } catch (IOException e) {
	                         // TODO Auto-generated catch block
	                         e.printStackTrace();
	                 }
	                 rd.close();
	         } catch (MalformedURLException e) {
	                 // TODO Auto-generated catch block
	                 e.printStackTrace();
	         } catch (IOException e) {
	                 // TODO Auto-generated catch block
	                 e.printStackTrace();
	         }
	         try {      
		         result_array=new JSONObject(response).getJSONObject("results").getJSONArray("result");//.getJSONObject(0).getString("cover");
		        //detail_array=new String[result_array.length()][2];
		         info_array=new MusicInfo[result_array.length()];
		         for(int i=0;i<result_array.length();i++){
		        	 //detail_array[i][0]=new String(result_array.getJSONObject(i).getString("cover"));
		        	 //detail_array[i][1]=new String(result_array.getJSONObject(i).getString("name"));
		        	 info_array[i]=new MusicInfo(result_array.getJSONObject(i),search_sort_type);
		         }
	         } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	         
			return null;
        }
		  @Override
		  protected void onPostExecute(Void result) {
		   super.onPostExecute(result);
		   if(info_array!=null){
			   setListAdapter(new detailAdapter());
		   }
		   result_title.setText(title);	
		  }
     }
    
    @SuppressWarnings("deprecation")
	@Override
	public void onListItemClick(ListView parent, View v, final int position,
			long id) {	
		AlertDialog popup=new AlertDialog.Builder(this).create();
		popup.setTitle("Post to Facebook");
		popup.setButton("Facebook", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//intent = new Intent(ResultActivity.this, FaceBookShare.class);
		        //startActivity(intent); 
				chosen_row=position;
				 toLogin() ;
		       //finish();
			}
		});
		if(search_sort_type==sort_type.SONG)
			popup.setButton2("Sample", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
		        	String url =info_array[position].detail;
					//String url="http://rovimusic.rovicorp.com/playback.mp3?c=LJ9SjGxeoOlEa_u2Ogl3DTqpU4hxl5saPDaNIVORh-E=&f=J";
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
		popup.show();
		
			}

    public class detailAdapter extends ArrayAdapter {

    	Method method;
    	public detailAdapter() {
    		super(ResultActivity.this,result_row_id,info_array);
    	}

		public View getView(int position, View convertView,
				ViewGroup parent) {
				LayoutInflater inflater=getLayoutInflater();
				View row=inflater.inflate(result_row_id, parent, false);
				//LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(row_ele_width, row_height);
				switch(search_sort_type){
					case ARTIST:{
						TextView artist_name=(TextView)row.findViewById(R.id.artist_name);
						MusicInfo.writeText(artist_name,info_array[position].name);
						TextView artist_genre=(TextView)row.findViewById(R.id.artist_genre);
						MusicInfo.writeText(artist_genre,info_array[position].genre);
						TextView artist_year=(TextView)row.findViewById(R.id.artist_year);
						MusicInfo.writeText(artist_year,info_array[position].year);
						ImageView icon=(ImageView)row.findViewById(R.id.artist_cover);
						info_array[position].drawIcon(icon);
						break;
					}
					case ALBUM:{
						TextView album_title=(TextView)row.findViewById(R.id.album_title);
						MusicInfo.writeText(album_title,info_array[position].title);
						TextView album_artist=(TextView)row.findViewById(R.id.album_artist);
						MusicInfo.writeText(album_artist,info_array[position].artist);
						TextView album_genre=(TextView)row.findViewById(R.id.album_genre);
						MusicInfo.writeText(album_genre,info_array[position].genre);
						TextView album_year=(TextView)row.findViewById(R.id.album_year);
						MusicInfo.writeText(album_year,info_array[position].year);
						ImageView icon=(ImageView)row.findViewById(R.id.album_cover);
						info_array[position].drawIcon(icon);
						break;
					}
					case SONG:{
						TextView song_composer=(TextView)row.findViewById(R.id.song_composer);
						MusicInfo.writeText(song_composer,info_array[position].composer);
						TextView song_title=(TextView)row.findViewById(R.id.song_title);
						MusicInfo.writeText(song_title,info_array[position].title);
						TextView song_performer=(TextView)row.findViewById(R.id.song_performer);
						MusicInfo.writeText(song_performer,info_array[position].performer);
						break;
					}
				}
				return(row);
			}
    }
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	 if (session.isOpened() && isChosen==true) {
        		 publishFeed();
        		 isChosen=false;
        	 }
        }
    }
	
	private void publishFeed() {
		String name="";
		String description="";
		String link="";
		String picture="";
		
		
		switch(search_sort_type){
			case SONG:{
				name = info_array[chosen_row].title;
				description = "I like song "+name+" composed by " + info_array[chosen_row].composer+"\n Performer: "+info_array[chosen_row].performer;
				link = info_array[chosen_row].detail;
				//picture=info_array[chosen_row].cover;
				break;
			}
			
			case ALBUM:{
				name = info_array[chosen_row].title;
				description = "I like "+name+" released in " + info_array[chosen_row].year+" \n Artist: "+info_array[chosen_row].artist+" \n Genre: "+info_array[chosen_row].genre;
				link = info_array[chosen_row].detail;
				picture=info_array[chosen_row].cover;
				break;
			}
			
			case ARTIST:{
				name = info_array[chosen_row].name;
				description = "I like "+name+" who is active since  " + info_array[chosen_row].year+" \n Genre of Music is: "+info_array[chosen_row].genre;
				link = info_array[chosen_row].detail;
				picture=info_array[chosen_row].cover;
				break;
			}
		}
	    Bundle params = new Bundle();
	    params.putString("name",name);
	    params.putString("properties", "{ 'Look at details ': { 'text': 'here', 'href': '" +
	    		link + "' } }"); 
	    //params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", description);
	    params.putString("link", link);
	    if(search_sort_type!=sort_type.SONG)
	    	params.putString("picture", picture);
	    
	    WebDialog feedDialog = (
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
    
	@Override
	public void onBackPressed() 
	{
				intent=new Intent(ResultActivity.this,SearchActivity.class);
				startActivity(intent);
				ResultActivity.this.finish();
	}
}

