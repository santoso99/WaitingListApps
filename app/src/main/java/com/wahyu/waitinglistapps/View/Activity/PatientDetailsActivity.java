package com.wahyu.waitinglistapps.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wahyu.waitinglistapps.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientDetailsActivity extends AppCompatActivity {

    private String getImage, getName, getKeluhan, getPenyakit, getAlamat, getUmur, getJenis, getDaftar, getSelesai;
    private String getImageDoctor, getNameDoctor, getSpesialisDoctor, getDateRegist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        CircleImageView civProfilePatient = findViewById(R.id.civ_profile_patientdetails);
        TextView tvName = findViewById(R.id.tv_nama_patientdetails);
        TextView tvKeluhan = findViewById(R.id.tv_keluhan_patientdetails);
        TextView tvPenyakit = findViewById(R.id.tv_penyakit_patientdetails);
        TextView tvAlamat = findViewById(R.id.tv_alamat_patientdetails);
        TextView tvUmur = findViewById(R.id.tv_umur_patientdetails);
        TextView tvJenis = findViewById(R.id.tv_jenis_patientdetails);
        TextView tvDaftar = findViewById(R.id.tv_waktu_daftar_patientdetails);
        TextView tvSelesai = findViewById(R.id.tv_estimasi_patientdetails);
        ImageView btnBack = findViewById(R.id.btnback_patientdetails);

        //details in home trigger
        CircleImageView civProfileDoctor = findViewById(R.id.civ_doctor_patientdetails);
        TextView tvNameDoctor = findViewById(R.id.tv_namedoctor_patientdetails);
        TextView tvSpesialisDoctor = findViewById(R.id.tv_spesialisdoctor_patientdetails);
        TextView tvDateRegist = findViewById(R.id.tv_dateRegist_patientdetails);

        getDataIntent();

        //set init detail pasien
        if (getImage != null && getName != null && getKeluhan != null && getPenyakit != null && getAlamat != null &&
                getUmur != null && getJenis != null && getDaftar != null && getSelesai != null) {

            if (!getImage.equals("")) {
                if (getImage.substring(0, 4).equals("http")) {
                    Picasso.get().load(getImage).into(civProfilePatient);
                } else {
                    Picasso.get().load(R.drawable.icon_default_profile).into(civProfilePatient);
                }
            } else {
                Toast.makeText(this, "Silahkan batalkan pendaftaran,\ndan daftar ulang kembali ya !", Toast.LENGTH_SHORT).show();
                Picasso.get().load(R.drawable.icon_default_profile).into(civProfilePatient);
            }

            tvName.setText(getName);
            tvKeluhan.setText(getKeluhan);
            tvPenyakit.setText(getPenyakit);
            tvAlamat.setText(getAlamat);
            tvUmur.setText(getUmur);
            tvJenis.setText(getJenis);
            tvDaftar.setText(getDaftar);
            tvSelesai.setText(getSelesai);

        } else {
            Toast.makeText(this, "Silahkan reload kembali untuk set data", Toast.LENGTH_SHORT).show();
        }

        if (getImageDoctor != null && getNameDoctor != null && getSpesialisDoctor != null && getDateRegist != null) {
            if (getImageDoctor.substring(0, 4).equals("http")) {
                Picasso.get().load(getImageDoctor).into(civProfileDoctor);
            } else {
                Picasso.get().load(R.drawable.icon_default_profile).into(civProfileDoctor);
            }

            tvNameDoctor.setText(getNameDoctor);
            tvSpesialisDoctor.setText(getSpesialisDoctor);
            tvDateRegist.setText(getDateRegist);
        } else {
            RelativeLayout rlDoctorDetails = findViewById(R.id.doctordetails);
            CardView cvNote = findViewById(R.id.cv_note_queuedetails);
            rlDoctorDetails.setVisibility(View.GONE);
            cvNote.setVisibility(View.GONE);
        }

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(PatientDetailsActivity.this, HomeActivity.class));
            finish();
        });
    }

    private void getDataIntent() {
        Intent data = getIntent();
        getImage = data.getStringExtra("image");
        getName = data.getStringExtra("name");
        getKeluhan = data.getStringExtra("keluhan");
        getPenyakit = data.getStringExtra("penyakit");
        getAlamat = data.getStringExtra("alamat");
        getUmur = data.getStringExtra("umur");
        getJenis = data.getStringExtra("jenis");
        getDaftar = data.getStringExtra("daftar");
        getSelesai = data.getStringExtra("selesai");
        getImageDoctor = data.getStringExtra("imagedoctor");
        getNameDoctor = data.getStringExtra("namedoctor");
        getSpesialisDoctor = data.getStringExtra("spesialisdoctor");
        getDateRegist = data.getStringExtra("dateregist");
    }

}