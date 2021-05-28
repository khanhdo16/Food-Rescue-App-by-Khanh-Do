package com.example.foodrescue.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.example.foodrescue.model.User;
import com.example.foodrescue.util.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyListFragment extends Fragment implements PostAdapter.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EMAIL = "email";

    // TODO: Rename and change types of parameters
    private String email;
    List<Post> postList;
    PostAdapter postAdapter;

    public MyListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment MyListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyListFragment newInstance(String email) {
        MyListFragment fragment = new MyListFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        SharedPreferences sharedPref = getActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        email = sharedPref.getString("email", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_list, container, false);
        RecyclerView myListRecyclerView = (RecyclerView) view.findViewById(R.id.myListRecyclerView);
        DatabaseHelper db = new DatabaseHelper(getContext());
        postList = db.fetchUserPosts(email);

        postAdapter = new PostAdapter(postList, getContext(), this);
        myListRecyclerView.setAdapter(postAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        myListRecyclerView.setLayoutManager(layoutManager);


        return view;
    }

    @Override
    public void onItemClick(int position) {
        if(position >= 0 && position < postList.size()) {
            Fragment fragment;
            Post post = postList.get(position);
            fragment = PostDetailsFragment.newInstance(post.getId());

            FloatingActionButton fab = getActivity().findViewById(R.id.floatingActionButton);
            fab.setVisibility(View.GONE);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
        }
        else {
            throw new IllegalArgumentException("Unexpected value: " + position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        postAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        postAdapter.notifyDataSetChanged();
    }
}
