package com.example.todonato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditTaskNew extends AppCompatActivity {

    Intent data;
    EditText editTaskText;
    Button editBtn;
    ImageView icon_pen;
    FirebaseFirestore fStore;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_new);

        fStore = fStore.getInstance();

        data = getIntent();

        editTaskText = findViewById(R.id.editTaskText_new);

        String taskContent = data.getStringExtra("content");

        editTaskText.setText(taskContent);

        icon_pen = findViewById(R.id.icon_pen);
        editBtn = findViewById(R.id.editTaskButton_new);
        user = FirebaseAuth.getInstance().getCurrentUser();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String content = editTaskText.getText().toString();

                if (content.isEmpty()) {
                    Toast.makeText(EditTaskNew.this, "Empty Field: Cannot Save TODO.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference docref = fStore.collection("tasks").document(user.getUid()).collection("myTasks").document(data.getStringExtra("taskId"));


                Map<String, Object> task = new HashMap<>();
                task.put("content", content);

                docref.update(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditTaskNew.this, "TODO Task was Edited.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), task_main.class));
                        EditTaskNew.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditTaskNew.this, "Error, Try again.", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });



    }
}