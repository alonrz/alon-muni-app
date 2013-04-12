package com.alon.testing;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {

	private MyCustomAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new MyCustomAdapter();
		setListAdapter(mAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class MyCustomAdapter extends BaseAdapter {

		// public MyCustomAdapter(Context context, int resource,
		// int textViewResourceId, List<Person> Persons) {
		// super(context, resource, textViewResourceId, Persons);
		// }
		public MyCustomAdapter() {
			super();
			// Init the inflater
			this.mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		List<Person> mList = new ArrayList<Person>() {
			{
				add(new Person("M", "Ocean Line"));
				add(new Person("ccc", "ddd"));
				add(new Person("eee", "fff"));
			}
		};
		private LayoutInflater mInflater;
		List<String> mSeperators = new ArrayList<String>() 
		{{
			add("Person 1");
			add("Person 2");
			add("Person 3");
		}};

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			convertView = mInflater.inflate(R.layout.activity_main, null);
			TextView titleView = (TextView)convertView.findViewById(R.id.titleView1);
			TextView textView = (TextView)convertView.findViewById(R.id.textView1);
			titleView.setText(mList.get(position).firstName);
			textView.setText(mList.get(position).firstName + " - " + mList.get(position).lastName );
			//convertView.
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}

	public class Person {
		private String firstName;
		private String lastName;

		public Person(String firstName, String lastName) {

			this.lastName = lastName;
			this.firstName = firstName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

	}
}
