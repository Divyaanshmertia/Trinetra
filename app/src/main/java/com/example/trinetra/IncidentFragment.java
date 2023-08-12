package com.example.trinetra;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.trinetra.model.Incident;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
// for reference undo purpose
public class IncidentFragment extends Fragment {
    private static final String TAG = "IncidentFragment";

    static final int REQUEST_VIDEO_CAPTURE = 2;

    private EditText titleEditText;
    private EditText descriptionEditText;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button cameraButton;
    private Button videoButton;
    private Button submitButton;

    private DatabaseReference incidentsRef;
    private StorageReference mediaStorageRef;
    private String userId;
    private String mediaType;
    private Uri mediaUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incident, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        cameraButton = view.findViewById(R.id.cameraButton);
        videoButton = view.findViewById(R.id.videoButton);
        submitButton = view.findViewById(R.id.submitButton);

        incidentsRef = FirebaseDatabase.getInstance().getReference("incidents");
        mediaStorageRef = FirebaseStorage.getInstance().getReference("media");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaType = "image";
                dispatchTakePictureIntent();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaType = "video";
                dispatchTakeVideoIntent();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    titleEditText.setError("Title is required");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    descriptionEditText.setError("Description is required");
                    return;
                }

                if (mediaUri == null) {
                    Toast.makeText(getActivity(), "Please capture media", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve user's current location from Firebase Realtime Database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            double latitude = dataSnapshot.child("current_address").child("latitude").getValue(Double.class);
                            double longitude = dataSnapshot.child("current_address").child("longitude").getValue(Double.class);

                            // Upload media to Firebase Storage
                            String mediaName = UUID.randomUUID().toString();
                            StorageReference mediaRef = mediaStorageRef.child(mediaType).child(mediaName);
                            mediaRef.putFile(mediaUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            mediaRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String mediaUrl = uri.toString();

                                                    // Create a new incident with a unique key and store it in Firebase Realtime Database
                                                    String incidentKey = incidentsRef.push().getKey();
                                                    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                                    double upvote = 0;
                                                    double downvote = 0;
                                                    Incident incident = new Incident(incidentKey, userId, title, description, mediaUrl, mediaType, date, latitude, longitude, upvote,downvote);
                                                    incidentsRef.child(incidentKey).setValue(incident)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "Incident added successfully");
                                                                    Toast.makeText(getActivity(), "Incident added successfully", Toast.LENGTH_SHORT).show();
                                                                    clearForm();
                                                                    // Send the incident details to the FCM server.
                                                                    sendIncidentDetailsToFCMServer(incident.getTitle(), incident.getDescription());

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {Log.e(TAG, "Error adding incident: " + e.getMessage());
                                                                    Toast.makeText(getActivity(), "Error adding incident", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error uploading media: " + e.getMessage());
                                            Toast.makeText(getActivity(), "Error uploading media", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Log.e(TAG, "User not found");
                            Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error retrieving user location: " + databaseError.getMessage());
                        Toast.makeText(getActivity(), "Error retrieving user location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }

    private void clearForm() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        mediaUri = null;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
                Toast.makeText(getActivity(), "Error creating image file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.trinetra.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating video file: " + ex.getMessage());
                Toast.makeText(getActivity(), "Error creating video file", Toast.LENGTH_SHORT).show();
            }
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(getActivity(), "com.trinetra.fileprovider", videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        mediaUri = Uri.fromFile(imageFile);
        return imageFile;
    }

    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(videoFileName, ".mp4", storageDir);
        mediaUri = Uri.fromFile(videoFile);
        return videoFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Toast.makeText(getActivity(), "Image captured successfully", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                Toast.makeText(getActivity(), "Video captured successfully", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Capture cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Capture failed", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendIncidentDetailsToFCMServer(String incidentTitle, String incidentDescription) {
        // Create the JSON payload.
        JSONObject payload = new JSONObject();
        try {
            payload.put("title", incidentTitle);
            payload.put("description", incidentDescription);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON payload", e);
            return;
        }

        // Create the HTTP request.
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "FCM message sent successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error sending FCM message", error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set the Authorization header with the FCM server key.
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "key=AAAAmfZBSnM:APA91bEc4DdZhOUrpD9bxXENdR88lHGC-ky1v4rexetnDKmT9t7CF0OPMsrR5QljXTpQYjfiCwGg-6zdRiUpNmI_KqdDVdwqCov1RUyys-6J51FCGLskXin3naLwjgRqBBEfx3pI2pNW");
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Parse the response and return a JSONObject.
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        // Add the request to the request queue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

}
