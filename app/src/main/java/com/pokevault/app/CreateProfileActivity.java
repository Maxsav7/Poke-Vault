/*
 * SCREEN CONTROLLER: Checks the create-profile form and saves a new user.
 * Its matching screen design is res/layout/activity_create_profile.xml.
 */
package com.pokevault.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        findViewById(R.id.saveProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfile();
            }
        });
        findViewById(R.id.backToLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void createProfile() {
        EditText usernameInput = findViewById(R.id.newUsernameInput);
        EditText passwordInput = findViewById(R.id.newPasswordInput);
        EditText confirmInput = findViewById(R.id.confirmPasswordInput);
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmation = confirmInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmation)) {
            Toast.makeText(this, "The passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!new PokeVaultData(this).createUser(username, password)) {
            Toast.makeText(this, "That username already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Profile created. You can log in.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
