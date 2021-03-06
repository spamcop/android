package com.twofours.surespot.friends;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.twofours.surespot.R;
import com.twofours.surespot.common.SurespotLog;
import com.twofours.surespot.network.IAsyncCallbackTriplet;

public class FriendMenuFragment extends SherlockDialogFragment {
	protected static final String TAG = "FriendMenuFragment";
	private Friend mFriend;
	private ArrayList<String> mItems;
	private IAsyncCallbackTriplet<DialogInterface, Friend, String> mSelectionCallback;

	public void setActivityAndFriend(Friend friend, IAsyncCallbackTriplet<DialogInterface, Friend, String> selectionCallback) {
		mFriend = friend;
		mSelectionCallback = selectionCallback;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// builder.setTitle(R.string.pick_color);

		if (savedInstanceState != null) {
			try {
				String sFriend = savedInstanceState.getString("friend");
				if (sFriend != null) {
					mFriend = Friend.toFriend(new JSONObject(sFriend));
				}
			}
			catch (JSONException e) {
				SurespotLog.e(TAG, e, "could not create friend from saved instance state");
				return null;
			}
		}

		if (mFriend == null) {
			SurespotLog.w(TAG, "there is no friend assigned");
			return null;
		}

		mItems = new ArrayList<String>(5);

		if (mFriend.isFriend()) {
			if (mFriend.isChatActive()) {
				mItems.add(getString(R.string.menu_close_tab));
			}

			mItems.add(getString(R.string.menu_delete_all_messages));
			if (!mFriend.isDeleted()) {
				mItems.add(getString(R.string.verify_key_fingerprints));
				mItems.add(getString(R.string.menu_assign_image));
			}
		}
		if (!mFriend.isInviter()) {
			mItems.add(getString(R.string.menu_delete_friend));

		}

		builder.setItems(mItems.toArray(new String[mItems.size()]), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialogi, int which) {
				if (mFriend == null)
					return;

				AlertDialog dialog = (AlertDialog) FriendMenuFragment.this.getDialog();
				ListView listview = dialog.getListView();

				if (listview == null || !listview.getChildAt(which).isEnabled()) {
					return;
				}

				String itemText = mItems.get(which);

				mSelectionCallback.handleResponse(dialogi, mFriend, itemText);
				// dialogi.cancel();

			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putString("friend", mFriend.toJSONObject().toString());
	}

}
