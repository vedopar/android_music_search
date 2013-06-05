package com.example.cs571hw9;

import com.example.cs571hw9.MusicInfo.sort_type;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class SearchActivity extends Activity{

	// UI references.
	private EditText search_keyword;
	private Spinner search_sort;
	private AlertDialog input_error;
	private Button search_submit;
	private int selected_sort_index;
	private String[] search_sorts;
	private String search_keyword_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);
		
		search_submit = (Button)this.findViewById(R.id.search_submit);
		search_keyword = (EditText)this.findViewById(R.id.search_keyword);
		search_sort=(Spinner)this.findViewById(R.id.search_sort);
		selected_sort_index=0;
		search_sorts = getResources().getStringArray(R.array.search_sort);
		//input_error= new AlertDialog.Builder(this).create();
		
		//init sort selection
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, search_sorts);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		search_sort.setAdapter(dataAdapter);
		search_sort.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				selected_sort_index=arg2;
				//choice=search_sorts[arg2];
				//if(selected_sort_index!=0)
					//Toast.makeText(SearchActivity.this, arg2, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//init the alert
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT")
        .setMessage(getResources().getString(R.string.error_message))
        .setCancelable(false)
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        input_error = builder.create();
		
        //init the submit button
		search_submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Intent intent;
				search_keyword_value=search_keyword.getText().toString();
				if(search_keyword_value.trim().length() == 0)
					input_error.show();
				else{
				//start next activity
					Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
		            Bundle bundle=new Bundle();
		            bundle.putString("search_keyword", search_keyword_value);
		            bundle.putString("search_sort", search_sorts[selected_sort_index]);
		            bundle.putInt("search_sort_type", selected_sort_index);
		            intent.putExtras(bundle);
			        startActivity(intent); 
			        finish();
				}
			}
		});
		
	}
}
