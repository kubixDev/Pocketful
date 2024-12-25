package com.kubixdev.pocketful.utils;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcrProcessor {

    public interface OcrCallback {
        void onSuccess(String extractedText, float highestPrice);
        void onFailure(String error);
    }


    public void processImage(Bitmap bitmap, OcrCallback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(text -> {
                    float largestPrice = extractLargestPrice(text);
                    callback.onSuccess(text.getText(), largestPrice);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    private float extractLargestPrice(Text text) {
        float largestPrice = 0;
        String regex = "(?<!\\d)(\\d{1,3}(?:[.,]\\d{3})*[.,]\\d{2})(?!\\d)";
        Pattern pattern = Pattern.compile(regex);

        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                Matcher matcher = pattern.matcher(line.getText());

                while (matcher.find()) {
                    try {
                        float price = Float.parseFloat(matcher.group().replace(',', '.'));

                        if (price > largestPrice) {
                            largestPrice = price;
                        }
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return largestPrice;
    }

}