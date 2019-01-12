package com.example.thanhhung.hungapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Thanh Hung on 1/12/2019.
 */

public class ImageUtils {
    public static final int ICON_HEIGH = 100;
    public static final int ICON_WIDTH = 100;

    public static ArrayList<String> getAllImagePaths(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = context.getContentResolver().query(imagesUri, projection, null, null, null);

        assert cur != null;
        if (cur.moveToFirst()) {
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                imagePaths.add(cur.getString(dataColumn));
            } while (cur.moveToNext());
        }
        cur.close();
        return imagePaths;
    }

    public static String getRandomImageFromGallery(Context context) {
        ArrayList<String> allImagePaths = getAllImagePaths(context);
        int numImagePath = allImagePaths.size();
        return allImagePaths.get(getRandomNumberinRange(0, numImagePath));

    }

    public static int getRandomNumberinRange(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
