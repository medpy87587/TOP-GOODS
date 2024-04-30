package com.example.version1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class register1 extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private ImageView googleSignInButton;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        initializeViews();

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        configureGoogleSignIn();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidRegistrationData()) {
                    registerUser();
                }
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.user);
        emailEditText = findViewById(R.id.email1);
        passwordEditText = findViewById(R.id.password1);
        confirmPasswordEditText = findViewById(R.id.confirmpassword1);
        registerButton = findViewById(R.id.register1);
        googleSignInButton = findViewById(R.id.googl);
    }

    private void configureGoogleSignIn() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private boolean isValidRegistrationData() {
        // Validation code as before
        return true;
    }

    private void registerUser() {
        FirebaseAuth.getInstance().signOut(); // Sign out current user
        final String username = usernameEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        // Check if the username and email are unique
        // This involves querying Firestore to see if the username and email already exist
        db.collection("users")
                .document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                // Username is not unique
                                showToast("Username already exists. Choose a different one.");
                            } else {
                                // Username is unique, check for email
                                checkUniqueEmail(email, username, password);
                            }
                        } else {
                            showToast("Error checking username uniqueness");
                        }
                    }
                });
    }

    private void checkUniqueEmail(final String email, final String username, final String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Email is not unique
                                showToast("Email already exists. Choose a different one.");
                            } else {
                                // Email is unique, proceed with user registration
                                performUserRegistration(email, username, password);
                            }
                        } else {
                            showToast("Error checking email uniqueness");
                        }
                    }
                });
    }

    private void performUserRegistration(final String email, final String username, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful, save user data to Firestore
                            saveUserDataToFirestore(email, username);
                            onRegistrationSuccess();
                        } else {
                            onRegistrationFailure(task.getException());
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(String email, String username) {
        // Check if the document with the email already exists
        db.collection("users")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document with the email already exists, show a message or take appropriate action
                                showToast("User all ready registered");
                            } else {
                                // Document with the email doesn't exist, proceed to save user data
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("username", username);

                                db.collection("users")
                                        .document(email)
                                        .set(userData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    showToast("User data saved to Firestore");
                                                } else {
                                                    showToast("Error saving user data to Firestore");
                                                }
                                            }
                                        });
                            }
                        } else {
                            showToast("Error checking user existence in Firestore");
                        }
                    }
                });
    }


    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            showToast("Google Sign In Failed: " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserDataToFirestore(user.getEmail(), user.getDisplayName());
                            onRegistrationSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            showToast("Google Authentication Failed.");
                        }
                    }
                });
    }

    private void onRegistrationSuccess() {
        showToast("Registration successful!");

        // Proceed to the main activity or any other logic
        startActivity(new Intent(register1.this, chose.class));
    }

    private void onRegistrationFailure(Exception exception) {
        showToast("Registration failed: " + exception.getMessage());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
