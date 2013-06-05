package com.example.cs571hw9;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicInfo{
	public static enum sort_type {ARTIST,SONG,ALBUM};
	  Bitmap icon = null;
	  ImageView icon_view=null;
	  String cover= null;
	  String detail= null;
	  String name= "Name:";
	  String genre= "Genre:";
	  String year= "Year:";
	  String composer= "Composer:";
	  String performer= "Performer:";
	  String sample= null;
	  String title= "Title:";
	  String artist= "Artist:";
	  sort_type sort;
	  static int icon_height;
	  static int icon_width;
	  
	  public MusicInfo(JSONObject obj,sort_type s) throws JSONException{
		  sort=s;
		  detail=obj.getString("detail");
		  switch(sort){
			  case ARTIST:{
				  genre+=obj.getString("genre");
				  name+=obj.getString("name");
				  cover=obj.getString("cover");
				  year+=obj.getString("year");
				  break;
				  }
			  case SONG:{
				  composer+=obj.getString("composer");
				  performer+=obj.getString("performer");
				  sample=obj.getString("sample");
				  title+=obj.getString("title");
				  break;
				  }
			  case ALBUM:{
				  cover=obj.getString("cover");
				  artist+=obj.getString("artist");
				  genre+=obj.getString("genre");
				  title+=obj.getString("title");
				  year+=obj.getString("year");
				  break;
				  }
			  }
		  }
		public void drawIcon(ImageView iv){
			 icon_view=iv;
			 if(icon!=null)
				 iv.setImageBitmap(icon);
			 else
				 new DownloadImageTask().execute();
		}
		
		public static void writeText(TextView tv,String text){
			String text2;
			try {
				text=URLDecoder.decode(text,"ISO-8859-1");
				//text2 = new String(text.getBytes(), "UTF-8");			
				text2=new String(text.getBytes("ISO-8859-1"), "UTF-8");
				tv.setText(text2);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//tv.setText(text);
			tv.getLayoutParams().height=icon_height;
			tv.getLayoutParams().width=icon_width;
			//tv.setClickable(true);
		}
		
    class DownloadImageTask extends AsyncTask<String, Void, Void> {
    	InputStream is;
    	@Override
    	protected Void doInBackground(String... params) {
  	      try {
  	    	  InputStream is=new URL(cover).openStream();
  	    	  BufferedInputStream  in = new BufferedInputStream(is);
  	    	icon = BitmapFactory.decodeStream(in);
  	    	icon=Bitmap.createScaledBitmap(icon, icon_width, icon_height, false);
  	        in.close();
  	        is.close();
  	      /*  is = (InputStream) new URL(cover).getContent();
  	    icon= Drawable.createFromStream(is, "src name");
  	    is.close();*/
  	      } catch (Exception e) {
  	          e.printStackTrace();
  	      }
  	      return null;
  	  }

  	  protected void onPostExecute(Void result) {
  		icon_view.setImageBitmap(icon);
  		}
  	}
}
