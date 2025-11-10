package com.example.unserakus.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.unserakus.R;
import com.example.unserakus.activities.EditProfileActivity;
import com.example.unserakus.activities.LoginActivity;
import com.example.unserakus.api.ApiService;
import com.example.unserakus.api.models.ApiError;
import com.example.unserakus.api.models.User;
import com.example.unserakus.storages.Prefences;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    TextView tvName, tvUsername;
    Button btnEditProfile, btnLogout;
    Prefences prefences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        tvName = v.findViewById(R.id.tvName);
        tvUsername = v.findViewById(R.id.tvUsername);
        btnEditProfile = v.findViewById(R.id.btnEditProfile);
        btnLogout = v.findViewById(R.id.btnLogout);

        prefences = new Prefences(getActivity());
        String token = prefences.getToken();

        ApiService apiService = new ApiService(requireContext(), token);

        apiService.getCurrentUser(new ApiService.ApiResponseListener<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(User response) {
                tvName.setText(response.getFirstName() + " " + response.getLastName());
                tvUsername.setText('@' + response.getUsername());
            }

            @Override
            public void onError(ApiError error) {

            }
        });

        btnEditProfile.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), EditProfileActivity.class));
        });

        btnLogout.setOnClickListener(view -> {
            prefences.setToken(null);

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        return v;
    }
}