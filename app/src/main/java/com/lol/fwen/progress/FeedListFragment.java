package com.lol.fwen.progress;

import android.content.Context;
import android.os.Bundle;
import android.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lol.fwen.progress.asynctask.SendRequestAsyncTask;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.SocialNetWorkRequest;

import java.util.LinkedList;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FeedListFragment
        extends ListFragment {

    private OnListFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeLayout;
    private FeedViewAdapter adapter;
    private String prevTime = "0";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FeedListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("hi", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        updateList(((FeedActivity)getActivity()).requestSet);
                        swipeLayout.setRefreshing(false);
                    }


        });

        // Set the adapter
        adapter = new FeedViewAdapter(getActivity(), R.layout.fragment_item_list, new LinkedList<Feed>());
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

  //  @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mListener.onListFragmentItemClick(adapter.getItem(position));
        Log.d("gg", "gg");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentItemClick(Feed item);
    }

    public void updateList(Set<SocialNetWorkRequest> set) {
        Log.e("updateList", "update");
        new SendRequestAsyncTask(getListView(), adapter, set).execute();
    }
}
