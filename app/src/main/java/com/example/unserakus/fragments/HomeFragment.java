package com.example.unserakus.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unserakus.LoadingAlert;
import com.example.unserakus.R;
import com.example.unserakus.SharedPreferencesHelper;
import com.example.unserakus.activities.CreateThreadActivity;
import com.example.unserakus.activities.ThreadDetailActivity;
import com.example.unserakus.adapters.ThreadAdapter;
import com.example.unserakus.api.models.ApiError;
import com.example.unserakus.api.ApiService;
import com.example.unserakus.api.models.Like;
import com.example.unserakus.api.models.Thread;
import com.example.unserakus.storages.Prefences;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rvThreads;
    FloatingActionButton fabCreateThread;
    Toolbar toolbar;

    ThreadAdapter threadAdapter;
    List<Thread> threadList = new ArrayList<>();
    ApiService apiService;
    LoadingAlert loadingAlert;
    EditText etSearch;

    private int loggedInUserId;
    private Runnable workRunnable;
    private final Handler handler = new Handler(Looper.getMainLooper());

    String threadQuery;
    String threadSortBy = "terbaru";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Ambil token dari SharedPreferencesHelper
        String token = SharedPreferencesHelper.getToken(getContext());
        if (token == null) {
            return v;
        }
        apiService = new ApiService(getContext(), token);
        loadingAlert = new LoadingAlert(getActivity());

        // Ambil ID user yang login
        loggedInUserId = SharedPreferencesHelper.getLoggedInUserId(getContext());

        toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Baris ini PENTING untuk menampilkan menu
        setHasOptionsMenu(true);

        rvThreads = v.findViewById(R.id.rvThreads);
        fabCreateThread = v.findViewById(R.id.fabCreateThread);
        etSearch = v.findViewById(R.id.etSearch);

        setupRecyclerView();

        fabCreateThread.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), CreateThreadActivity.class));
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable);
                }

                workRunnable = () -> onUserStoppedTyping(s.toString());
                handler.postDelayed(workRunnable, 700);
            }
        });

        return v;
    }

    private void hideKeyboard() {
        Activity activity = this.getActivity();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void onUserStoppedTyping(String text) {
        Log.d("THREAD_QUERY", text);

        threadQuery = text;

        hideKeyboard();
        loadThreads();
    }

    private void handleDeleteThread(Thread thread, int position) {
        loadingAlert.startLoading();

        apiService.deleteThread(thread.getId(), new ApiService.ApiSuccessListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(getContext(), "Thread dihapus", Toast.LENGTH_SHORT).show();
                // Hapus item dari list dan update adapter
                threadList.remove(position);
                threadAdapter.notifyItemRemoved(position);
                loadingAlert.dismissDialog();
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();
                Toast.makeText(getContext(), "Gagal menghapus: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        threadAdapter = new ThreadAdapter(threadList, new ThreadAdapter.OnThreadActionsListener() {
            @Override
            public void onLikeClick(int position, Thread thread) {
                handleLikeClick(position, thread);
            }

            @Override
            public void onCommentClick(Thread thread) {
                Intent intent = new Intent(getContext(), ThreadDetailActivity.class);
                intent.putExtra(ThreadDetailActivity.EXTRA_THREAD_ID, thread.getId());
                startActivity(intent);
            }

            // --- Implementasi Hapus (BARU) ---
            @Override
            public void onDeleteClick(Thread thread, int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Hapus Thread")
                        .setMessage("Apakah Anda yakin ingin menghapus thread ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            handleDeleteThread(thread, position);
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        }, loggedInUserId); // <-- Pass ID user ke adapter

        rvThreads.setLayoutManager(new LinearLayoutManager(getContext()));
        rvThreads.setAdapter(threadAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadThreads();
    }

    private void loadThreads() {
        Log.d("HomeFragment", "Memuat threads... sort by: " + threadSortBy);

        loadingAlert.startLoading();

        String sortByTranslated = "recent";

        if (threadSortBy.equals("trending")) {
            sortByTranslated = "trending";
        }

        apiService.listThreads(sortByTranslated, threadQuery, new ApiService.ApiResponseListener<List<Thread>>() {
            @Override
            public void onSuccess(List<Thread> response) {
                loadingAlert.dismissDialog();
                threadList.clear();
                threadList.addAll(response);
                threadAdapter.notifyDataSetChanged();
                Log.d("HomeFragment", "Berhasil memuat " + response.size() + " threads.");
            }

            @Override
            public void onError(ApiError error) {
                loadingAlert.dismissDialog();

                Log.e("HomeFragment", "Gagal memuat threads: " + error.getDetailMessage());
                Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLikeClick(int position, Thread thread) {
        // Logika Like/Unlike
        if (thread.isLiked()) {
            apiService.deleteLike(thread.getId(), new ApiService.ApiSuccessListener() {
                @Override
                public void onSuccess() {
                    loadThreads();
                }

                @Override
                public void onError(ApiError error) {

                }
            });

        } else {
            apiService.createLike(thread.getId(), new ApiService.ApiResponseListener<Like>() {
                @Override
                public void onSuccess(Like response) {
                    loadThreads(); // Cara mudah untuk refresh
                    // Cara lebih baik:
                    // thread.setLiked(true);
                    // thread.setLikesCount(thread.getLikesCount() + 1);
                    // threadAdapter.notifyItemChanged(position);
                }
                @Override
                public void onError(ApiError error) {
                    Toast.makeText(getContext(), "Gagal Like: " + error.getDetailMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // --- Menu Handling ---

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sort_newest) {
            threadSortBy = "terbaru";

            loadThreads();
            return true;
        } else if (itemId == R.id.sort_trending) {
            threadSortBy = "trending";

            loadThreads();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}