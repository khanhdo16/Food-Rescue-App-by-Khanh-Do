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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodrescue.R;
import com.example.foodrescue.data.DatabaseHelper;
import com.example.foodrescue.model.Post;
import com.example.foodrescue.util.PostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements PostAdapter.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String email;
    List<Post> postList;
    PostAdapter postAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String email) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView homeRecyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        DatabaseHelper db = new DatabaseHelper(getContext());
        postList = db.fetchAllPosts();

        postAdapter = new PostAdapter(postList, getContext(), this);
        homeRecyclerView.setAdapter(postAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        homeRecyclerView.setLayoutManager(layoutManager);


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
