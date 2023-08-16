package com.example.applediseaseclassificator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {

    private RecyclerView rvDiseaseInfo;
    private DatabaseReference databaseReference;

    private DiseaseInfoAdapter diseaseInfoAdapter;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_info, container, false);

        rvDiseaseInfo = view.findViewById(R.id.rvDiseaseInfo);

        databaseReference = FirebaseDatabase.getInstance().getReference("DiseaseInfo");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<DiseaseInfo> diseaseInfoList = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    DiseaseInfo diseaseInfo = postSnapshot.getValue(DiseaseInfo.class);
                    diseaseInfoList.add(diseaseInfo);
                }

                updateUI(diseaseInfoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateUI(List<DiseaseInfo> diseaseInfoList) {
        diseaseInfoAdapter = new DiseaseInfoAdapter(getContext(), diseaseInfoList);
        rvDiseaseInfo.setHasFixedSize(true);
        rvDiseaseInfo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvDiseaseInfo.setAdapter(diseaseInfoAdapter);
    }
}