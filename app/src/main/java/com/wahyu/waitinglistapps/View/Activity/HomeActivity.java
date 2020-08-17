package com.wahyu.waitinglistapps.View.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wahyu.waitinglistapps.Adapter.MyQueueAdapter;
import com.wahyu.waitinglistapps.Model.PatientModel;
import com.wahyu.waitinglistapps.Model.UserModel;
import com.wahyu.waitinglistapps.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView civProfileUser;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private String userType, imageURL, name, email;

    //display list antrianku
    private RecyclerView rvMyQueue;
    private MyQueueAdapter myQueueAdapter;
    private TextView tvEmptyQueue;
    private ImageView emptyBox;
    private ProgressBar progressBar;
    private ArrayList<PatientModel> patientModelArrayList;
//    private ArrayList<PatientModel> patientModelFinishArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.topbar_home);
        setSupportActionBar(toolbar);
        setTitle("");

        civProfileUser = findViewById(R.id.civ_imageProfileHome);
        CardView cvDoctorList = findViewById(R.id.cv_doctorlist_home);
        CardView pickQueue = findViewById(R.id.cv_pickqueue_home);

        //inisialisasi component list antrian
        tvEmptyQueue = findViewById(R.id.emptyMyQueue);
        emptyBox = findViewById(R.id.emptyBox_home);
        progressBar = findViewById(R.id.progressBar_home);
        rvMyQueue = findViewById(R.id.rv_myqueue_home);
        rvMyQueue.setLayoutManager(new LinearLayoutManager(this));
        rvMyQueue.setHasFixedSize(true);
        rvMyQueue.smoothScrollToPosition(0);

        //inisialisasi
        reference = FirebaseDatabase.getInstance().getReference("Users");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getMyQueue();

//        if (!patientModelFinishArrayList.isEmpty()) {
//            if (!patientModelArrayList.isEmpty()){
//                tvEmptyQueue.setVisibility(View.GONE);
//                emptyBox.setVisibility(View.GONE);
//            }else {
//                tvEmptyQueue.setVisibility(View.VISIBLE);
//                emptyBox.setVisibility(View.VISIBLE);
//            }
//        }

        pickQueue.setOnClickListener(this);
        cvDoctorList.setOnClickListener(this);
        civProfileUser.setOnClickListener(view -> {
            Intent toProfile = new Intent(HomeActivity.this, ProfileActivity.class);
            toProfile.putExtra("usertype", userType);
            toProfile.putExtra("image", imageURL);
            toProfile.putExtra("name", name);
            toProfile.putExtra("email", email);
            startActivity(toProfile);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv_doctorlist_home:
                startActivity(new Intent(this, DoctorListActivity.class));
                break;
            case R.id.cv_pickqueue_home:
                if (!patientModelArrayList.isEmpty()) {
                    Toast.makeText(this, "Maaf Antrian anda belum selesai", Toast.LENGTH_SHORT).show();
                } else {
                    Intent toDaftar = new Intent(HomeActivity.this, RegisPatientActivity.class);
                    toDaftar.putExtra("namapasien", name);
                    toDaftar.putExtra("imagepasien", imageURL);
                    startActivity(toDaftar);
                }
                break;
            default:
                break;
        }
    }

    private void getMyQueue() {
        patientModelArrayList = new ArrayList<>();
//        patientModelFinishArrayList = new ArrayList<>();
        DatabaseReference dbRefMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue");

        dbRefMyQueue.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patientModelArrayList.clear();
//                patientModelFinishArrayList.clear();

                if (!snapshot.exists()) {
                    tvEmptyQueue.setVisibility(View.VISIBLE);
                    emptyBox.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PatientModel patientModel = dataSnapshot.getValue(PatientModel.class);
                        assert patientModel != null;

                        if (patientModel.getStatus().equals("MENUNGGU")) {
                            patientModelArrayList.add(patientModel);
                            tvEmptyQueue.setVisibility(View.GONE);
                            emptyBox.setVisibility(View.GONE);
                        }
//                        else {
//                            patientModelFinishArrayList.add(patientModel);
//                            tvEmptyQueue.setVisibility(View.VISIBLE);
//                            emptyBox.setVisibility(View.VISIBLE);
//                        }
                    }
                    myQueueAdapter = new MyQueueAdapter(HomeActivity.this, patientModelArrayList);
                    rvMyQueue.setAdapter(myQueueAdapter);
                    myQueueAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataUser() {
        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if (snapshot.exists()) {
                    assert userModel != null;
                    if (userModel.getImageURL().substring(0, 4).equals("http")) {
                        Picasso.get().load(userModel.getImageURL()).into(civProfileUser);
                    } else { // default atau file:\\
                        Picasso.get().load(R.drawable.icon_default_profile).into(civProfileUser);
                    }
                    userType = userModel.getType();
                    imageURL = userModel.getImageURL();
                    name = userModel.getUsername();
                    email = userModel.getEmail();
                } else {
                    Toast.makeText(HomeActivity.this, "Tidak ada user yang ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.logout:
                showAlertDialogLogout();
                return true;
        }
        return false;
    }

    private void showAlertDialogLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("KELUAR");
        alertDialogBuilder
                .setMessage("Apakah Anda yakin ingin keluar dari Aplikasi ?")
                .setCancelable(false)
                .setPositiveButton("Ya, tentu", (dialog, id) -> {

                    FirebaseAuth.getInstance().signOut();

                    startActivity(new Intent(HomeActivity.this, LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .setNegativeButton("Gak jadi", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}