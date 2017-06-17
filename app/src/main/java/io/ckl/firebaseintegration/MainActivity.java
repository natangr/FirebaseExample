package io.ckl.firebaseintegration;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    TextView loginResultTextView;

    Button fetchDataButton;
    TextView fetchDataResultTextView;

    Button createDataButton;
    TextView createDataResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginResultTextView = (TextView) findViewById(R.id.loginResultTextView);

        fetchDataButton = (Button) findViewById(R.id.fetchDataButton);
        fetchDataResultTextView = (TextView) findViewById(R.id.fetchResultTextView);

        createDataButton = (Button) findViewById(R.id.createDataButton);
        createDataResultTextView = (TextView) findViewById(R.id.createResultTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                authenticate(username, password);
            }
        });

        fetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

        createDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createData();
            }
        });

        fillLoginData();
    }

    void fillLoginData() {
        usernameEditText.setText("natan@ckl.io");
        passwordEditText.setText("123456");
    }

    void authenticate(String username, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String success = task.isSuccessful() ? "success" : "failure";
                loginResultTextView.setText(success);
            }
        });
    }

    void fetchData() {
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("data");
        databaseReference.orderByChild("thisIsABigName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) { return; }
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren()
                        .iterator();
                ArrayList<FirebaseData> list = new ArrayList<>();
                while (iterator.hasNext()) {
                    FirebaseData data = iterator.next().getValue(FirebaseData.class);
                    list.add(data);
                }
                String result = "";
                for (FirebaseData element: list) {
                    result += element.getName() + "\n";
                }
                fetchDataResultTextView.setText(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fetchSingleData();
    }

    void fetchSingleData() {
        DatabaseReference lastAddedDatabaseReference =  FirebaseDatabase.getInstance().getReference().child("lastAddedData");
        lastAddedDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) { return; }
                FirebaseData data = dataSnapshot.getValue(FirebaseData.class);
                Log.d("Firebase", "Last added data name: " + data.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void createData() {
        FirebaseData firebaseData = new FirebaseData();
        firebaseData.setData("this is data");
        firebaseData.setName("this is name");
        firebaseData.setThisIsABigName(9);
        DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("data");
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(firebaseData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String success = task.isSuccessful() ? "success" : "failure";
                createDataResultTextView.setText(success);
            }
        });

        DatabaseReference lastAddedDatabaseReference =  FirebaseDatabase.getInstance().getReference().child("lastAddedData");
        lastAddedDatabaseReference.setValue(firebaseData);
    }
}
