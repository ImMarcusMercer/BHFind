package com.example.prototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PropertyCreation extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_creation);



        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance("https://project2-1899e-default-rtdb.firebaseio.com/")
                .getReference("property");
        Button submitButton = findViewById(R.id.submit_property_button);
        submitButton.setOnClickListener(
                v ->{
                    EditText propertyName = findViewById(R.id.property_name);
                    EditText propertyRate = findViewById(R.id.propery_rate);
                    EditText propertyLocation = findViewById(R.id.property_location);
                    EditText propertyDescription = findViewById(R.id.property_description);

                    String name = propertyName.getText().toString();
                    String rate = propertyRate.getText().toString();
                    String location = propertyLocation.getText().toString();
                    String description = propertyDescription.getText().toString();

                    if(!name.isEmpty() && !rate.isEmpty() && !location.isEmpty() && !description.isEmpty()){
                        Property newProperty = new Property(currentUserId, name, rate, location, description);
                        databaseRef.push().setValue(newProperty).addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(this, LandLordMainActivity.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(this, "Failed to save property data.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(this, LandLordMainActivity.class));
                                finish();
                            }

                        });
                    }
                    else{
                        Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
