package arafath65.minime.gymmanagementui.Utilss;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeGenerator {

    private QrCodeGenerator() {
    }

    // GENERATE QR

    public static Bitmap generate(
            String content,
            int size) throws WriterException {

        QRCodeWriter writer =
                new QRCodeWriter();

        BitMatrix matrix =
                writer.encode(
                        content,
                        BarcodeFormat.QR_CODE,
                        size,
                        size
                );

        Bitmap bitmap =
                Bitmap.createBitmap(
                        size,
                        size,
                        Bitmap.Config.ARGB_8888
                );

        for (int x = 0; x < size; x++) {

            for (int y = 0; y < size; y++) {

                bitmap.setPixel(
                        x,
                        y,
                        matrix.get(
                                x,
                                y
                        )
                                ? Color.BLACK
                                : Color.WHITE
                );
            }
        }

        return bitmap;
    }
}