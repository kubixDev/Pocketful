package com.kubixdev.pocketful.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kubixdev.pocketful.R;
import com.kubixdev.pocketful.adapters.PaymentAdapter;
import com.kubixdev.pocketful.models.PaymentItem;
import com.kubixdev.pocketful.utils.OcrProcessor;

public class HomeFragment extends Fragment {

    private PaymentAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;


    // handling camera and gallery permission requests
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                }
                else Toast.makeText(requireContext(), "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show();
            });

    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openGallery();
                }
                else Toast.makeText(requireContext(), "Gallery permission is required to select an image", Toast.LENGTH_SHORT).show();
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btnScanOCR = view.findViewById(R.id.buttonScanOCR);
        RecyclerView recyclerView = view.findViewById(R.id.operationsRecyclerView);

        // setting up recyclerview
        adapter = new PaymentAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // handling OCR bottom sheet
        btnScanOCR.setOnClickListener(v -> {
            showBottomSheetDialog();
        });

        return view;
    }


    private void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_ocr, null);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        Button btnCaptureImage = bottomSheetView.findViewById(R.id.buttonCaptureImage);
        Button btnSelectImage = bottomSheetView.findViewById(R.id.buttonSelectImage);

        btnCaptureImage.setOnClickListener(v -> checkCameraPermissionAndOpen());
        btnSelectImage.setOnClickListener(v -> checkGalleryPermissionAndOpen());
    }


    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
    }


    private void checkGalleryPermissionAndOpen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
            else requestGalleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

        }
        else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
            else requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        }
        else Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        galleryLauncher.launch(intent);
    }


    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                    // processing the image as a bitmap
                    Bundle extras = result.getData().getExtras();

                    if (extras != null) {
                        Bitmap bitmap = (Bitmap) extras.get("data");

                        if (bitmap != null) {
                            processCapturedImage(bitmap);
                        }
                    }

                }

                // dismiss the bottom sheet after processing the image
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            });


    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                    try {
                        Uri imageUri = result.getData().getData();

                        if (imageUri != null) {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(
                                    ImageDecoder.createSource(requireContext().getContentResolver(), imageUri)
                            );

                            processCapturedImage(bitmap);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // dismiss the bottom sheet after processing the image
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                }
            });


    private void processCapturedImage(Bitmap bitmap) {
        OcrProcessor ocrProcessor = new OcrProcessor();

        ocrProcessor.processImage(bitmap, new OcrProcessor.OcrCallback() {
            @Override
            public void onSuccess(String extractedText, float highestAmount) {
                if (highestAmount > 0) {
                    addItemToRecyclerView(extractedText, highestAmount);
                }
                else Toast.makeText(requireContext(), "No price found in the image", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Failed to extract text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addItemToRecyclerView(String description, float amount) {
        PaymentItem item = new PaymentItem(description, amount);
        adapter.addItem(item);
    }
}