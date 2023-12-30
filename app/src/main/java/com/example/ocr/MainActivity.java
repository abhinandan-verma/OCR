package com.example.ocr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 123;
    //Widgets
    ImageView image;
    TextView textView;
    Button imageBTN, speechBTN, secondBtn;

    //Variables
    InputImage inputImage;
    TextRecognizer recognizer;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        imageBTN = findViewById(R.id.chooseBtn);
        image = findViewById(R.id.imageView);
        textView = findViewById(R.id.text);
        speechBTN = findViewById(R.id.speechBTN);
        secondBtn = findViewById(R.id.secondBTN);


        imageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if ( status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });

        speechBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @SuppressLint("IntentReset")
    private void OpenGallery() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("Image/");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE){
            if (data != null) {

                byte[] byteArray  = new byte[0];
                String filePath = null;

                try {
                    inputImage = InputImage.fromFilePath(this, Objects.requireNonNull(data.getData()));
                    Bitmap resultUri = inputImage.getBitmapInternal();

                    Glide.with(MainActivity.this)
                            .load(resultUri)
                            .into(image);

                    //Process the image
                    Task<Text> result =
                            recognizer.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text text) {
                                            ProcessTextBlock(text);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "Process Failed :(", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void ProcessTextBlock(Text text) {

        //Start ML Kit - Process Text Block
        String resultText = text.getText();
        for (Text.TextBlock block : text.getTextBlocks()){

            String blockText = block.getText();
            textView.append("\n");
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();

            for (Text.Line line : block.getLines()){
                String lineText = line.getText();

                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();

                for (Text.Element element : line.getElements()){
                    textView.append(" ");
                    String elementText = element.getText();
                    textView.append(elementText);

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();

                }
            }
        }
        Intent intent = new Intent(MainActivity.this,TextActivity.class);
        intent.putExtra("key",textView.getText().toString());
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        if (!textToSpeech.isSpeaking()) {
            super.onPause();
        }
    }


}