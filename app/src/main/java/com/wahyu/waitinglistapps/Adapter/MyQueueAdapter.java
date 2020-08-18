package com.wahyu.waitinglistapps.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wahyu.waitinglistapps.Model.DoctorModel;
import com.wahyu.waitinglistapps.Model.PatientModel;
import com.wahyu.waitinglistapps.R;
import com.wahyu.waitinglistapps.View.Activity.PatientDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wahyu_septiadi on 17, August 2020.
 * Visit My GitHub --> https://github.com/WahyuSeptiadi
 */

public class MyQueueAdapter extends RecyclerView.Adapter<MyQueueAdapter.ViewHolder> {

    private Activity mActivity;
    private ArrayList<PatientModel> patientModelArrayList;

    public MyQueueAdapter(Activity mActivity, ArrayList<PatientModel> patientModelArrayList) {
        this.mActivity = mActivity;
        this.patientModelArrayList = patientModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.list_item_queue, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientModel patientModel = patientModelArrayList.get(position);

        if (patientModel.getImageDoctor().substring(0, 4).equals("http")) {
            Picasso.get().load(patientModel.getImageDoctor()).into(holder.civProfileDoctor);
        } else {
            Picasso.get().load(R.drawable.icon_default_profile).into(holder.civProfileDoctor);
        }

        holder.tvNameDoctor.setText(patientModel.getNamaDokter());
        holder.tvSpesialisDoctor.setText(patientModel.getSpesialis());
        holder.tvDateRegist.setText(patientModel.getTanggalDaftar());
        holder.tvTimeFinish.setText(patientModel.getWaktuSelesai());
        holder.tvStatus.setText(patientModel.getStatus());

        if (patientModel.getStatus().equals("MENUNGGU")) {
            holder.tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.tvStatus.setTextColor(mActivity.getResources().getColor(R.color.colorAccent)); // gak kanggo cok wkwkw
        }

        holder.ivFinish.setOnClickListener(view -> {
            holder.tvStatus.setText(patientModel.getStatus());

            String myQueueId = patientModel.getIdAntrian();
            showDialogAlertUpdate(myQueueId, patientModel.getIdDokter(), patientModel.getNamaDokter());
        });

        holder.ivCancel.setOnClickListener(view -> {
            String myQueueId = patientModel.getIdAntrian();
            showDialogAlertCancel(myQueueId, patientModel.getIdDokter(), patientModel.getNamaDokter());
        });

        holder.tvBtnDetails.setOnClickListener(view -> {
            String image = patientModel.getImageURL();
            String name = patientModel.getNamaPasien();
            String keluhan = patientModel.getKeluhanPasien();
            String penyakit = patientModel.getPenyakitPasien();
            String alamat = patientModel.getAlamatPasien();
            String umur = patientModel.getUmurPasien();
            String jenis = patientModel.getJenisPasien();
            String daftar = patientModel.getWaktuDaftar();
            String selesai = patientModel.getWaktuSelesai();
            //FOR MY QUEUE DETAILS
            String imageDoctor = patientModel.getImageDoctor();
            String nameDoctor = patientModel.getNamaDokter();
            String spesialisDoctor = patientModel.getSpesialis();
            String dateRegist = patientModel.getTanggalDaftar();

            Intent toPatientDetails = new Intent(mActivity, PatientDetailsActivity.class);
            toPatientDetails.putExtra("image", image);
            toPatientDetails.putExtra("name", name);
            toPatientDetails.putExtra("keluhan", keluhan);
            toPatientDetails.putExtra("penyakit", penyakit);
            toPatientDetails.putExtra("alamat", alamat);
            toPatientDetails.putExtra("umur", umur);
            toPatientDetails.putExtra("jenis", jenis);
            toPatientDetails.putExtra("daftar", daftar);
            toPatientDetails.putExtra("selesai", selesai);
            //my queue details
            toPatientDetails.putExtra("imagedoctor", imageDoctor);
            toPatientDetails.putExtra("namedoctor", nameDoctor);
            toPatientDetails.putExtra("spesialisdoctor", spesialisDoctor);
            toPatientDetails.putExtra("dateregist", dateRegist);
            mActivity.startActivity(toPatientDetails);
        });

    }

    @Override
    public int getItemCount() {
        return patientModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civProfileDoctor;
        private TextView tvNameDoctor, tvSpesialisDoctor;
        private TextView tvTimeFinish, tvDateRegist;
        private ImageView ivFinish, ivCancel;
        private TextView tvBtnDetails;
        private TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            civProfileDoctor = itemView.findViewById(R.id.civ_mydoctor_queuelist);
            tvNameDoctor = itemView.findViewById(R.id.tv_namedoctor_queuelist);
            tvSpesialisDoctor = itemView.findViewById(R.id.tv_spesialisdoctor_queuelist);
            tvDateRegist = itemView.findViewById(R.id.tv_dateRegist_queuelist);
            tvTimeFinish = itemView.findViewById(R.id.tv_start_estimation_queuelist);
            tvStatus = itemView.findViewById(R.id.tv_status_queuelist);

            ivFinish = itemView.findViewById(R.id.checklist_queuelist);
            ivCancel = itemView.findViewById(R.id.cancel_queuelist);
            tvBtnDetails = itemView.findViewById(R.id.tv_btnCheck_queuelist);
        }
    }

    private void showDialogAlertUpdate(String myQueueId, String dokterId, String namaDokter) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue").child(userid);
        DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList").child(dokterId);
        DatabaseReference rootDoctors = FirebaseDatabase.getInstance().getReference("Doctors").child(dokterId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("SELESAI");
        alertDialogBuilder
                .setMessage("Apakah Anda telah selesai diperiksa oleh dokter " + namaDokter + " ?")
                .setCancelable(false)
                .setPositiveButton("Sudah", (dialog, id) -> {

                    //sementara gini aja dah
                    rootMyQueue.child(dokterId).removeValue();

                    //cek apakah saya pasient terakhir ?
//                    int lastPos = patientModelArrayList.size() - 1;
//                    PatientModel patientModel = patientModelArrayList.get(lastPos);
//                    String lastPatientId = patientModel.getIdPasien();
//
//                    if (lastPatientId.equals(firebaseUser.getUid())) {
//                        HashMap<String, Object> lastPatient = new HashMap<>();
//                        lastPatient.put("lastPatient", "kosong");
//                        rootDoctors.updateChildren(lastPatient);
//                    }

                    //for patientlist per doctor
                    HashMap<String, Object> waitingList = new HashMap<>();
                    waitingList.put("waktuSelesai", "SELESAI");
                    rootWaitingList.child(myQueueId).updateChildren(waitingList);

                    mActivity.overridePendingTransition(0, 0);
                    mActivity.startActivity(mActivity.getIntent());
                    mActivity.overridePendingTransition(0, 0);

                    Toast.makeText(mActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Belum", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showDialogAlertCancel(String myQueueId, String dokterId, String namaDokter) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference rootMyQueue = FirebaseDatabase.getInstance().getReference("MyQueue").child(userid);
        DatabaseReference rootWaitingList = FirebaseDatabase.getInstance().getReference("WaitingList").child(dokterId);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setTitle("BATAL");
        alertDialogBuilder
                .setMessage("Apakah Anda ingin membatalkan antrian tiket pada dokter " + namaDokter + " ?")
                .setCancelable(false)
                .setPositiveButton("iya", (dialog, id) -> {

                    //fix delete
                    rootMyQueue.child(dokterId).removeValue();
                    rootWaitingList.child(myQueueId).removeValue();

                    mActivity.overridePendingTransition(0, 0);
                    mActivity.startActivity(mActivity.getIntent());
                    mActivity.overridePendingTransition(0, 0);

                    Toast.makeText(mActivity, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
