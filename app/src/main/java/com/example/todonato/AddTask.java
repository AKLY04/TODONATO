package com.example.todonato;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTask extends AppCompatActivity {

    Button addBtn;
    EditText taskContent;
    FirebaseFirestore fStore;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        fStore = FirebaseFirestore.getInstance();

        addBtn = findViewById(R.id.addButton);
        taskContent = findViewById(R.id.taskContent);
        user = FirebaseAuth.getInstance().getCurrentUser();


        Calendar calendar = Calendar.getInstance();
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        //LOGIC FOR ADD BUTTON
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = taskContent.getText().toString();


                Calendar calendar = Calendar.getInstance();
                int YEAR = calendar.get(Calendar.YEAR);
                int MONTH = calendar.get(Calendar.MONTH);
                int DATE = calendar.get(Calendar.DATE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                if(content.isEmpty()){
                    Toast.makeText(AddTask.this, "Field is Empty: Can't Add TODO Task.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,androidx.navigation.ui.R. style.Theme_AppCompat_DayNight_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                            month = month + 1;
                            String dateString = year + "-" + month + "-" + date;

                            TimePickerDialog picker = new TimePickerDialog(AddTask.this, androidx.navigation.ui.R. style.Theme_AppCompat_DayNight_Dialog, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {

                                    String AM_PM = " AM";
                                    String mm_precede = "";
                                    if (sHour >= 12) {
                                        AM_PM = " PM";
                                        if ( sHour >=13 &&  sHour < 24) {
                                            sHour -= 12;
                                        }
                                        else {
                                            sHour = 12;
                                        }
                                    } else if ( sHour == 0) {
                                        sHour = 12;
                                    }
                                    if (sMinute < 10) {
                                        mm_precede = "0";
                                    }

                                    String times = sHour + ":" + mm_precede + sMinute + AM_PM;
                                    String dateTime = dateString + " " + times;

                            DocumentReference docref = fStore.collection("tasks").document(user.getUid()).collection("myTasks").document();
                            Map<String, Object> task = new HashMap<>();
                            task.put("content", content);
                            task.put("date",dateTime);

                            docref.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddTask.this, "Task Added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), task_main.class));
                                    AddTask.this.finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddTask.this, "Error", Toast.LENGTH_SHORT).show();

                                }
                            });


                                }
                            },hour, minutes, false);
                            picker.show();

                        }
                    }, YEAR, MONTH, DATE);
                    datePickerDialog.show();
                }

            }
        });
    }
}