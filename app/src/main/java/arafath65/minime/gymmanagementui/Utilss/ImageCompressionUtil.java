package arafath65.minime.gymmanagementui.Utilss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressionUtil {

    // =====================================================
    // SETTINGS
    // =====================================================

    private static final int MAX_WIDTH = 1600;
    private static final int MAX_HEIGHT = 1600;

    private static final int JPEG_QUALITY = 80;


    // =====================================================
    // COMPRESS IMAGE
    // =====================================================

    public static File compressImage(
            Context context,
            Uri imageUri)
            throws IOException {

        if (imageUri == null) {

            throw new IOException(
                    "Image URI is null."
            );
        }


        // -------------------------------------------------
        // READ IMAGE SIZE FIRST
        // -------------------------------------------------

        BitmapFactory.Options options =
                new BitmapFactory.Options();

        options.inJustDecodeBounds = true;


        try (InputStream inputStream =
                     context.getContentResolver()
                             .openInputStream(imageUri)) {

            if (inputStream == null) {

                throw new IOException(
                        "Unable to open image."
                );
            }

            BitmapFactory.decodeStream(
                    inputStream,
                    null,
                    options
            );
        }


        if (options.outWidth <= 0 ||
                options.outHeight <= 0) {

            throw new IOException(
                    "Invalid image."
            );
        }


        // -------------------------------------------------
        // CALCULATE SAMPLE SIZE
        // -------------------------------------------------

        int sampleSize = 1;

        int width =
                options.outWidth;

        int height =
                options.outHeight;


        while (
                width / sampleSize > MAX_WIDTH ||
                        height / sampleSize > MAX_HEIGHT
        ) {

            sampleSize *= 2;
        }


        // -------------------------------------------------
        // LOAD SCALED BITMAP
        // -------------------------------------------------

        BitmapFactory.Options bitmapOptions =
                new BitmapFactory.Options();

        bitmapOptions.inSampleSize =
                sampleSize;

        bitmapOptions.inPreferredConfig =
                Bitmap.Config.ARGB_8888;


        Bitmap bitmap;

        try (InputStream inputStream =
                     context.getContentResolver()
                             .openInputStream(imageUri)) {

            if (inputStream == null) {

                throw new IOException(
                        "Unable to open image."
                );
            }

            bitmap =
                    BitmapFactory.decodeStream(
                            inputStream,
                            null,
                            bitmapOptions
                    );
        }


        if (bitmap == null) {

            throw new IOException(
                    "Unable to decode image."
            );
        }


        // -------------------------------------------------
        // FINAL RESIZE
        // -------------------------------------------------

        bitmap =
                resizeBitmap(
                        bitmap,
                        MAX_WIDTH,
                        MAX_HEIGHT
                );


        // -------------------------------------------------
        // CREATE TEMP DIRECTORY
        // -------------------------------------------------

        File directory =
                new File(
                        context.getCacheDir(),
                        "fitness_images"
                );


        if (!directory.exists() &&
                !directory.mkdirs()) {

            bitmap.recycle();

            throw new IOException(
                    "Unable to create image cache directory."
            );
        }


        // -------------------------------------------------
        // CREATE OUTPUT FILE
        // -------------------------------------------------

        File outputFile =
                new File(
                        directory,
                        "fitness_"
                                + System.currentTimeMillis()
                                + ".jpg"
                );


        // -------------------------------------------------
        // COMPRESS
        // -------------------------------------------------

        try (FileOutputStream outputStream =
                     new FileOutputStream(
                             outputFile
                     )) {

            boolean success =
                    bitmap.compress(
                            Bitmap.CompressFormat.JPEG,
                            JPEG_QUALITY,
                            outputStream
                    );


            if (!success) {

                bitmap.recycle();

                throw new IOException(
                        "Image compression failed."
                );
            }
        }


        bitmap.recycle();


        return outputFile;
    }


    // =====================================================
    // RESIZE BITMAP
    // =====================================================

    private static Bitmap resizeBitmap(
            Bitmap bitmap,
            int maxWidth,
            int maxHeight) {


        int width =
                bitmap.getWidth();

        int height =
                bitmap.getHeight();


        // -------------------------------------------------
        // NO RESIZE REQUIRED
        // -------------------------------------------------

        if (width <= maxWidth &&
                height <= maxHeight) {

            return bitmap;
        }


        // -------------------------------------------------
        // CALCULATE SCALE
        // -------------------------------------------------

        float widthRatio =
                (float) maxWidth /
                        width;

        float heightRatio =
                (float) maxHeight /
                        height;


        float scale =
                Math.min(
                        widthRatio,
                        heightRatio
                );


        int newWidth =
                Math.round(
                        width * scale
                );

        int newHeight =
                Math.round(
                        height * scale
                );


        // -------------------------------------------------
        // CREATE RESIZED BITMAP
        // -------------------------------------------------

        Bitmap resized =
                Bitmap.createScaledBitmap(
                        bitmap,
                        newWidth,
                        newHeight,
                        true
                );


        if (resized != bitmap) {

            bitmap.recycle();
        }


        return resized;
    }
}