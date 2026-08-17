package arafath65.minime.gymmanagementui.dialog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.zxing.WriterException;

import java.io.OutputStream;

import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.Utilss.QrCodeGenerator;

public class MemberQrDialog {

    private MemberQrDialog() {
    }

    // SHOW QR

    public static void show(
            Context context,
            int memberId,
            String memberName,
            String memberNo,
            String joinedDate) {

        if (context == null) {
            return;
        }

        if (memberId <= 0) {

            Toast.makeText(
                    context,
                    "Invalid member.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.dialog_member_qr,
                                null
                        );

        TextView btnClose =
                view.findViewById(
                        R.id.btnClose
                );

        ImageView imgQrCode =
                view.findViewById(
                        R.id.imgQrCode
                );

        TextView txtMemberName =
                view.findViewById(
                        R.id.txtMemberName
                );

        TextView txtMemberNo =
                view.findViewById(
                        R.id.txtMemberNo
                );

        TextView txtJoinedDate =
                view.findViewById(
                        R.id.txtJoinedDate
                );

        TextView btnShareQr =
                view.findViewById(
                        R.id.btnShareQr
                );

        TextView btnSaveQr =
                view.findViewById(
                        R.id.btnSaveQr
                );

        String safeName =
                safeText(
                        memberName
                );

        String safeMemberNo =
                safeText(
                        memberNo
                );

        String safeJoinedDate =
                safeText(
                        joinedDate
                );

        txtMemberName.setText(
                safeName
        );

        txtMemberNo.setText(
                safeMemberNo
        );

        if (safeJoinedDate.isEmpty()) {

            txtJoinedDate.setVisibility(
                    View.GONE
            );

        } else {

            txtJoinedDate.setText(
                    "Joined: "
                            + safeJoinedDate
            );

            txtJoinedDate.setVisibility(
                    View.VISIBLE
            );
        }

        Bitmap qrBitmap;

        try {

            qrBitmap =
                    QrCodeGenerator.generate(
                            "GYM-MEMBER:1:"
                                    + memberId,
                            700
                    );

        } catch (WriterException e) {

            Toast.makeText(
                    context,
                    "Unable to generate QR code.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        imgQrCode.setImageBitmap(
                qrBitmap
        );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        context
                )
                        .setView(
                                view
                        )
                        .create();

        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
        }

        btnClose.setOnClickListener(
                v -> dialog.dismiss()
        );

        btnSaveQr.setOnClickListener(
                v -> saveQr(
                        context,
                        qrBitmap,
                        safeMemberNo,
                        memberId
                )
        );

        btnShareQr.setOnClickListener(
                v -> shareQr(
                        context,
                        qrBitmap,
                        safeMemberNo,
                        memberId
                )
        );

        dialog.show();
    }

    // SAVE QR

    private static void saveQr(
            Context context,
            Bitmap bitmap,
            String memberNo,
            int memberId) {

        String fileName =
                "Member_QR_"
                        + safeFileName(memberNo)
                        + "_"
                        + memberId
                        + ".png";

        ContentValues values =
                new ContentValues();

        values.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                fileName
        );

        values.put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/png"
        );

        values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/GymManagement"
        );

        ContentResolver resolver =
                context.getContentResolver();

        Uri imageUri =
                resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );

        if (imageUri == null) {

            Toast.makeText(
                    context,
                    "Unable to save QR code.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try (
                OutputStream outputStream =
                        resolver.openOutputStream(
                                imageUri
                        )
        ) {

            if (outputStream == null) {

                throw new Exception(
                        "Output stream unavailable."
                );
            }

            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
            );

            outputStream.flush();

            Toast.makeText(
                    context,
                    "QR saved to Gallery.",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            resolver.delete(
                    imageUri,
                    null,
                    null
            );

            Toast.makeText(
                    context,
                    "Unable to save QR code.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // SHARE QR

    private static void shareQr(
            Context context,
            Bitmap bitmap,
            String memberNo,
            int memberId) {

        String fileName =
                "Member_QR_"
                        + safeFileName(memberNo)
                        + "_"
                        + memberId
                        + ".png";

        ContentValues values =
                new ContentValues();

        values.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                fileName
        );

        values.put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/png"
        );

        values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/GymManagement/Shared"
        );

        ContentResolver resolver =
                context.getContentResolver();

        Uri imageUri =
                resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );

        if (imageUri == null) {

            Toast.makeText(
                    context,
                    "Unable to prepare QR.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        try (
                OutputStream outputStream =
                        resolver.openOutputStream(
                                imageUri
                        )
        ) {

            if (outputStream == null) {

                throw new Exception(
                        "Output stream unavailable."
                );
            }

            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    outputStream
            );

            outputStream.flush();

            Intent shareIntent =
                    new Intent(
                            Intent.ACTION_SEND
                    );

            shareIntent.setType(
                    "image/png"
            );

            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    imageUri
            );

            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

            context.startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Member QR"
                    )
            );

        } catch (Exception e) {

            resolver.delete(
                    imageUri,
                    null,
                    null
            );

            Toast.makeText(
                    context,
                    "Unable to share QR code.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    // SAFE TEXT

    private static String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "";
        }

        return value.trim();
    }

    // SAFE FILE NAME

    private static String safeFileName(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "member";
        }

        return value
                .trim()
                .replaceAll(
                        "[^a-zA-Z0-9._-]",
                        "_"
                );
    }
}