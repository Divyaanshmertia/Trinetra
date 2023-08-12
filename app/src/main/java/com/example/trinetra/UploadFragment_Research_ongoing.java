package com.example.trinetra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadFragment_Research_ongoing extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView addressTextView;
    private Button cameraButton;
    private Button videoButton;
    private Button uploadButton;

    private Uri imageUri;
    private Uri videoUri;
    private String currentAddress;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        titleEditText = view.findViewById(R.id.title_edit_text);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        addressTextView = view.findViewById(R.id.address_text_view);
        cameraButton = view.findViewById(R.id.camera_button);
        videoButton = view.findViewById(R.id.video_button);
        uploadButton = view.findViewById(R.id.upload_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        getCurrentAddress();

        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {

                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("UploadFragment", ex.getMessage());
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(getActivity(),
                        "com.trinetra.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
                Log.e("UploadFragment", ex.getMessage());
            }
            if (videoFile != null) {
                videoUri = FileProvider.getUriForFile(getActivity(),
                        "com.trinetra.fileprovider",
                        videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return imageFile;
    }

    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String videoFileName = "MP4_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(
                videoFileName,
                ".mp4",
                storageDir
        );
        return videoFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity(), "Image captured successfully", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity(), "Video recorded successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String userId = firebaseUser.getUid();

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            descriptionEditText.requestFocus();
            return;
        }

        if (imageUri == null && videoUri == null) {
            Toast.makeText(getActivity(), "Please capture an image or video", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAddress == null) {
            Toast.makeText(getActivity(), "Unable to retrieve current address", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference imageRef = null;
        StorageReference videoRef = null;

        if (imageUri != null) {
            imageRef = storageReference.child("uploads/" + userId + "/images/" + imageUri.getLastPathSegment());
            UploadTask imageUploadTask = imageRef.putFile(imageUri);
            imageUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("UploadFragment", "Image uploaded successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {Log.e("UploadFragment", e.getMessage());
                }
            });
        }

        if (videoUri != null) {
            videoRef = storageReference.child("uploads/" + userId + "/videos/" + videoUri.getLastPathSegment());
            UploadTask videoUploadTask = videoRef.putFile(videoUri);
            videoUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("UploadFragment", "Video uploaded successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("UploadFragment", e.getMessage());
                }
            });
        }

        Map<String, Object> incident = new HashMap<>();
        incident.put("title", title);
        incident.put("description", description);
        incident.put("userId", userId);
        incident.put("address", currentAddress);

        if (imageRef != null) {
            incident.put("imageUrl", imageRef.getPath());
        }

        if (videoRef != null) {
            incident.put("videoUrl", videoRef.getPath());
        }

        String key = databaseReference.child("incidents").push().getKey();
        databaseReference.child("incidents").child(key).setValue(incident);

        Toast.makeText(getActivity(), "Data uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void getCurrentAddress() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            currentAddress = addresses.get(0).getAddressLine(0);
                            addressTextView.setText(currentAddress);
                        }
                    } catch (IOException ex) {
                        Log.e("UploadFragment", ex.getMessage());
                    }
                }
            }
        });
    }
}
