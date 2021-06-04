package com.example.foodrescue.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.foodrescue.R;
import com.example.foodrescue.model.Box;
import com.example.foodrescue.util.GraphicOverlay;
import com.example.foodrescue.util.Util;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;


public class DetectFragment extends Fragment {

    private GraphicOverlay graphicOverlay;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private PreviewView previewView;

    public DetectFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Toast.makeText(getContext(), "Camera permission granted!", Toast.LENGTH_SHORT) .show();
                initializeCamera();
            }
            else {
                Toast.makeText(getContext(), "No camera permission. Please grant permission to continue.", Toast.LENGTH_SHORT) .show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graphicOverlay = (GraphicOverlay) view.findViewById(R.id.overlay);
        previewView = view.findViewById(R.id.viewFinder);


        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
        else {
            initializeCamera();
        }
    }


    private void initializeCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();

                // Set up the capture use case to allow users to analyze immge
                ImageAnalysis imageAnalysis =
                    new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(getResources().getDisplayMetrics().widthPixels,
                            getResources().getDisplayMetrics().heightPixels))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext()),
                    new FoodDetectionAnalyzer());


                CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                    ((LifecycleOwner) this),
                    cameraSelector,
                    preview,
                    imageAnalysis);

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                    previewView.getSurfaceProvider());
            } catch (InterruptedException | ExecutionException e) {
                Log.e("cameraProvider", "initializeCamera: ", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));


    }


    private void updateOverlay(List<DetectedObject> objects) {

        if (graphicOverlay == null) {
            return;
        }

        if (objects == null) {
            graphicOverlay.set(Collections.emptyList());
            return;
        }

        graphicOverlay.setSize(getResources().getDisplayMetrics().widthPixels,
            getResources().getDisplayMetrics().heightPixels);

        List<Box> boxList = new ArrayList<>();

        for (DetectedObject detectedObject : objects) {

            Rect boundingBox = detectedObject.getBoundingBox();
            for (DetectedObject.Label label : detectedObject.getLabels()) {
                String text = label.getText();
                int confidence = (int) (label.getConfidence() * 100);

                boxList.add(new Box(boundingBox, text + " (" + String.valueOf(confidence) + "%)"));
            }
        }

        graphicOverlay.set(boxList);
    }

    private class FoodDetectionAnalyzer implements ImageAnalysis.Analyzer {
        List<DetectedObject> detectedObjects;
        LocalModel localModel;
        ObjectDetector objectDetector;

        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {


            @SuppressLint("UnsafeOptInUsageError")
            Image image = imageProxy.getImage();
            if (image != null) {

                InputImage inputImage = InputImage.fromMediaImage(
                    image,
                    imageProxy.getImageInfo().getRotationDegrees()
                );

                try {
                    localModel =
                        new LocalModel.Builder()
                            .setAssetFilePath("food_model.tflite")
                            .build();


                    // Live detection and tracking
                    CustomObjectDetectorOptions customObjectDetectorOptions =
                        new CustomObjectDetectorOptions.Builder(localModel)
                            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                            .enableMultipleObjects()
                            .enableClassification()
                            .setClassificationConfidenceThreshold(0.5f)
                            .setMaxPerObjectLabelCount(1)
                            .build();


                    objectDetector =
                        ObjectDetection.getClient(customObjectDetectorOptions);

                    objectDetector.process(inputImage)
                        .addOnSuccessListener(results -> {
                            Log.i("detectedObject", "onSuccess Size" + results.size());
                            detectedObjects = results;
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show())
                        .addOnCompleteListener(result -> imageProxy.close());


                    if (graphicOverlay != null) {
                        graphicOverlay.post(() -> updateOverlay(detectedObjects));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}


