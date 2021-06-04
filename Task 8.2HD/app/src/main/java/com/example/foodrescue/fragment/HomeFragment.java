package com.example.foodrescue.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
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

public class HomeFragment extends Fragment implements PostAdapter.OnClickListener{

    private String email;
    private NavHostFragment navHostFragment;
    List<Post> postList;
    PostAdapter postAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager()
            .findFragmentById(R.id.fragment);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("currentAccount", Activity.MODE_PRIVATE);
        email = sharedPref.getString("email", null);
        RecyclerView homeRecyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        DatabaseHelper db = new DatabaseHelper(getContext());
        postList = db.fetchAllPosts();

        postAdapter = new PostAdapter(postList, getContext(), this);
        homeRecyclerView.setAdapter(postAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        homeRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClick(int position) {
        if(position >= 0 && position < postList.size()) {
            Post post = postList.get(position);

            FloatingActionButton fab = requireActivity().findViewById(R.id.floatingActionButton);
            fab.setVisibility(View.GONE);

            Bundle args = new Bundle();
            args.putInt("post_id", post.getId());
            args.putString("email", email);

            if (navHostFragment != null) {
                NavHostFragment.findNavController(navHostFragment).navigate(R.id.action_homeFragment_to_postDetailsFragment, args);
            }
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
