package com.example.todolistfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "com.example.todolistfirebase.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.todolistfirebase.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.todolistfirebase.EXTRA_PRIORITY";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Spinner prioritySpinner;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        Button saveTaskButton = findViewById(R.id.button_save_task);
        prioritySpinner = findViewById(R.id.spinner_priority);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");

        // Check if we're editing or adding a new task
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_TITLE)) {
            setTitle("Edit Task");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));

            int priority = intent.getIntExtra(EXTRA_PRIORITY, 1);
            prioritySpinner.setSelection(priority - 1);
        } else {
            setTitle("Add Task");
        }

        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        int priority = prioritySpinner.getSelectedItemPosition() + 1; // High = 1, Medium = 2, Low = 3

        // Validate input
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_PRIORITY, priority);

        String existingTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if (existingTitle == null) {
            // Adding a new task
            Task task = new Task(title, description, priority);
            databaseReference.child(title).setValue(task);
        } else {
            // Updating an existing task
            updateTask(title, description, priority);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateTask(String title, String description, int priority) {
        Task task = new Task(title, description, priority);
        databaseReference.child(title).setValue(task);
        Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
    }
}
