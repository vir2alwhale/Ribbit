package com.vir2alwhale.ribbit;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class RecipientsActivity extends ListActivity {
	
	public static final String TAG = EditFriendsActivity.class.getSimpleName();
	
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected MenuItem mSendMenuItem;
	
	protected Uri mMediaUri;
	protected String mFileType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_recipients);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		mMediaUri = getIntent().getData();
		mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		setProgressBarIndeterminateVisibility(true);
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_FRIENDS_RELATION);
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e==null) {
					mFriends = friends;
					String[] friends_usernames = new String[friends.size()];
					int i = 0;
					for (ParseUser friend : mFriends) {
						friends_usernames[i] = friend.getUsername();
						i++;
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							RecipientsActivity.this,
							android.R.layout.simple_list_item_checked,
							friends_usernames);
					setListAdapter(adapter);
				}
				else {
					Log.e(TAG, e.getMessage());
					
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(e.getMessage())
					.setTitle(R.string.error_title)
					.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				
			}
		});
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(l.getCheckedItemCount() > 0) {
			mSendMenuItem.setVisible(true);
		} else {
			mSendMenuItem.setVisible(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		case R.id.action_send:
			ParseObject message = createMessage();
			// send(message);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipentIds());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
				
		return message;
	}

	protected ArrayList<String> getRecipentIds() {
		ArrayList<String> recipientIds =  new ArrayList<String>();
			for(int i=0; i<getListView().getCount(); i++) {
				if(getListView().isItemChecked(i)) {
					recipientIds.add(mFriends.get(i).getObjectId());
				}
			}
		return recipientIds;
	}
}
