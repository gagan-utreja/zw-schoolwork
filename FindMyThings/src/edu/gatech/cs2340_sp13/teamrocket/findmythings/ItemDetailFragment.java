package edu.gatech.cs2340_sp13.teamrocket.findmythings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {

	/**
	 * The content this fragment is presenting.
	 */
	private Item mItem;
	
	/**
	 * The data source for all Item lists (for now).
	 */
	private Controller control = Controller.shared();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(Item.ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			Item.Type mItemClass = ((ItemDetailActivity)getActivity()).getItemType();
			mItem = control.getItem(mItemClass, getArguments().getString(Item.ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.item_detail))
					.setText(mItem.getDescription());
			((TextView) rootView.findViewById(R.id.loc_detail))
			.setText(mItem.getLoc());
			((TextView) rootView.findViewById(R.id.cat_detail))
			.setText(mItem.getCat().getLocalizedValue(getActivity()));
			((TextView) rootView.findViewById(R.id.date_detail))
			.setText(mItem.getDateString());
		}

		return rootView;
	}
}
