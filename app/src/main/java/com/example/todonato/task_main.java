package com.example.todonato;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.todonato.Model.Task;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class task_main extends AppCompatActivity {

    Intent data;
    RecyclerView taskLists;
    ImageView img_logout;
    FloatingActionButton fab;
    FirebaseFirestore fstore;
    FirebaseUser user;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter<Task, TaskViewHolder> taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_main);

        data = getIntent();
        taskLists = findViewById(R.id.taskList);
        fab = findViewById(R.id.fab);
        fstore = FirebaseFirestore.getInstance();
       fAuth = FirebaseAuth.getInstance();
       user = fAuth.getCurrentUser();
        img_logout = findViewById(R.id.img_logout);
        createNotificationChannel();


        Query query = fstore.collection("tasks").document(user.getUid()).collection("myTasks").orderBy("date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Task> allTasks = new FirestoreRecyclerOptions.Builder<Task>().
                setQuery(query, Task.class)
                .build();


        taskAdapter = new FirestoreRecyclerAdapter<Task, TaskViewHolder>(allTasks) {
            @Override
            protected void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, @SuppressLint("RecyclerView") int position, @NonNull final Task task) {


                SimpleDateFormat dateFormatformat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                Date currentTime = Calendar.getInstance().getTime();



                String newCurrentDate1 = dateFormatformat.format(currentTime);
                String newTaskDate1 = task.getDate();

                try {
                    Date newCurrentDate2 = dateFormatformat.parse(newCurrentDate1);
                    Date newTaskDate2 = dateFormatformat.parse(newTaskDate1);

                    if (newTaskDate2.compareTo(newCurrentDate2) <= 0) {
                        String expired = "EXPIRED";

                        taskViewHolder.taskContent.setText(task.getContent());
                        taskViewHolder.taskDate.setText(expired);
                        taskViewHolder.taskDate.setBackgroundColor((Color.parseColor("#FF0000")));
                        Intent intent = new Intent(task_main.this, ReminderBroadcast.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(task_main.this, 0,intent,0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, 1, pendingIntent);
                    }
                    else{
                        taskViewHolder.taskContent.setText(task.getContent());
                        taskViewHolder.taskDate.setText(task.getDate());


                    }

                }
                catch (ParseException e){
                    e.printStackTrace();


                }


                final String docId = taskAdapter.getSnapshots().getSnapshot(position).getId();


                //Edit
               taskViewHolder.icon_pen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Intent i = new Intent(view.getContext(), EditTaskNew.class);
                        i.putExtra("content", task.getContent());
                        i.putExtra("date", task.getDate());
                        i.putExtra("taskId", docId);
                      startActivity(i);
                        finish();
                    }
                });

               //Delete
                taskViewHolder.icon_trash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(task_main.this, androidx.navigation.ui.R. style.Theme_AppCompat_DayNight_Dialog);
                        builder.setTitle("Confirm");
                        builder.setMessage("Are you sure you want to delete this?");

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                DocumentReference docRef = fstore.collection("tasks").document(user.getUid()).collection("myTasks").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(task_main.this, "Error Deleting TODO Task", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();


                    }
                });


            }

            @NonNull
            @Override
            public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_view_layout, parent, false);
                return new TaskViewHolder(view);
            }


        };


        taskLists.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        taskLists.setAdapter(taskAdapter);


        //Add
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(task_main.this, AddTask.class));
                finish();
            }
        });

        //Logout
        img_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkUser();



            }
        });


    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TODOnatoReminderChannel";
            String description = "Channel for TODOnato Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    //Check if user is anonymous or has account
    private void checkUser() {
        if(user.isAnonymous()){
            displayAlert();
        }
        else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this, androidx.navigation.ui.R. style.Theme_AppCompat_DayNight_Dialog)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with a temporary account. All TODO task would be deleted.")
                .setPositiveButton("Signup", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                     Intent mIntent = new Intent(task_main.this, Registration.class);
                     mIntent.putExtra("FROM_ACTIVITY", "A");
                     startActivity(mIntent);
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                startActivity(new Intent(getApplicationContext(), Login.class ));
                                finish();
                            }
                        });
                    }
                });
warning.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Toast.makeText(task_main.this, "Logged Out", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (taskAdapter != null) {
            taskAdapter.stopListening();
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskContent, taskDate;
        View view;
        CardView mCardView;
        ImageView icon_pen, icon_trash;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskContent = itemView.findViewById(R.id.cardview_content);
            taskDate = itemView.findViewById(R.id.cardview_date);
            view = itemView;
            mCardView = itemView.findViewById(R.id.taskCard);
            icon_pen = itemView.findViewById(R.id.icon_pen);
            icon_trash = itemView.findViewById(R.id.icon_trash);
        }
    }
}