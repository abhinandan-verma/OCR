package com.example.ocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognizer;

public class TextActivity extends AppCompatActivity {
    //Widgets
    TextView textView;
    Button readBTN;
    InputImage inputImage;
    TextRecognizer recognizer;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        readBTN = findViewById(R.id.readBtn);
        textView = findViewById(R.id.textView);

        Intent getIntent = new Intent();
        String textFromMain = getIntent.getStringExtra("key");

        textView.setText(textFromMain.toString());


        readBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


    }

}