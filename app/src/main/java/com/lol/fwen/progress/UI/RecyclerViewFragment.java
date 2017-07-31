package com.lol.fwen.progress.UI;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.lol.fwen.progress.R;
import com.lol.fwen.progress.asynctask.DownloadDataAsyncTask;
import com.lol.fwen.progress.data.Feed;
import com.lol.fwen.progress.data.FeedRequest;
import com.lol.fwen.progress.data.SocialNetWorkRequest;

import java.util.HashMap;

public class RecyclerViewFragment extends Fragment {

    private final String TAG = "RecyclerViewFragment";
    private final String LAYOUT_MANAGER_KEY = "LayoutManager";
    private final int SPAN_COUNT = 2;

    private enum LayoutManagerType {
        LINEAR_LAYOUT,
        GRID_LAYOUT
    }

    private OnFragmentInteractionListener mListener;
    private RecyclerViewAdapter adapter;
    private LayoutManagerType curLayoutManagerType;
    private RecyclerView.LayoutManager curLayoutManager;

    private SwipeRefreshLayout swipeLayout;
    private RadioButton linearLayoutRadioButton;
    private RadioButton gridLayoutRadioButton;
    private RecyclerView recyclerView;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(((FeedActivity)getActivity()).imageCache);
        recyclerView.setAdapter(adapter);

        swipeLayout = (SwipeRefreshLayout)view.findViewById(R.id.rv_swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("swipeLayout", "OnRefresh");
                updateList(((FeedActivity)getActivity()).requestMap);
                swipeLayout.setRefreshing(false);
            }
        });

        curLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT;
        if (savedInstanceState != null) {
            curLayoutManagerType = (LayoutManagerType) savedInstanceState.
                    getSerializable(LAYOUT_MANAGER_KEY);
        }

        setLayoutManager(curLayoutManagerType);

        linearLayoutRadioButton = (RadioButton) view.findViewById(R.id.linear_layout_rb);
        linearLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutManager(LayoutManagerType.LINEAR_LAYOUT);
            }
        });

        gridLayoutRadioButton = (RadioButton) view.findViewById(R.id.grid_layout_rb);
        gridLayoutRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutManager(LayoutManagerType.GRID_LAYOUT);
            }
        });

        return (view);
    }

    private void setLayoutManager(LayoutManagerType type) {
        int position = 0;

        if (recyclerView.getLayoutManager() != null) {
            if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                int[] firstCompletelyVisible = null;
                StaggeredGridLayoutManager layoutManager =
                        (StaggeredGridLayoutManager) recyclerView.getLayoutManager();

                layoutManager.findFirstCompletelyVisibleItemPositions(firstCompletelyVisible);
                if (firstCompletelyVisible != null && firstCompletelyVisible.length > 0) {
                    position = firstCompletelyVisible[0];
                }
            } else {
                position = ((LinearLayoutManager) recyclerView.getLayoutManager()).
                        findFirstCompletelyVisibleItemPosition();
            }
        }

        switch (type) {
            case GRID_LAYOUT:
                curLayoutManagerType = LayoutManagerType.GRID_LAYOUT;
                curLayoutManager = new StaggeredGridLayoutManager(2, 1);
                break;

            case LINEAR_LAYOUT:
            default:
                curLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT;
                curLayoutManager = new LinearLayoutManager(getActivity());
        }

        recyclerView.setLayoutManager(curLayoutManager);
        curLayoutManager.scrollToPosition(position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(((FeedActivity)getActivity()).requestMap);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(LAYOUT_MANAGER_KEY, curLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentItemClick(Feed item);
    }

    public void updateList(HashMap<FeedRequest.RequestType, SocialNetWorkRequest> map) {
        Log.e("updateList", "update");
        new DownloadDataAsyncTask(recyclerView, adapter, map).execute();
    }
}
