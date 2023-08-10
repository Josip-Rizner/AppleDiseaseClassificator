package com.example.applediseaseclassificator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RecommendationSystemsFragment extends Fragment{

    private RecyclerView rvRecommendationSystems;
    private SystemRecommendationAdapter systemRecommendationAdapter;
    private TextView tvNoRecommendationSystems;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference databaseReference;
    private List<RecommendationSystem> recommendationSystemList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public RecommendationSystemsFragment() {
        // Required empty public constructor
    }

    public static RecommendationSystemsFragment newInstance(String param1, String param2) {
        RecommendationSystemsFragment fragment = new RecommendationSystemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendation_systems, container, false);

        rvRecommendationSystems = view.findViewById(R.id.rvSystemRecommendations);
        tvNoRecommendationSystems = view.findViewById(R.id.tvNoRecommendationSystems);
        rvRecommendationSystems.setHasFixedSize(true);
        rvRecommendationSystems.setLayoutManager(new LinearLayoutManager(getContext()));

        recommendationSystemList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("RecommendationSystems").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    RecommendationSystem recommendationSystem = postSnapshot.getValue(RecommendationSystem.class);
                    recommendationSystemList.add(recommendationSystem);
                }

                systemRecommendationAdapter = new SystemRecommendationAdapter(getContext(), recommendationSystemList, (RecommendationSystemRecyclerViewOnClickInterface) getContext());

                if(systemRecommendationAdapter.getItemCount() > 0){
                    tvNoRecommendationSystems.setVisibility(View.GONE);
                }
                rvRecommendationSystems.setAdapter(systemRecommendationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT);
            }
        });

        return view;
    }
}