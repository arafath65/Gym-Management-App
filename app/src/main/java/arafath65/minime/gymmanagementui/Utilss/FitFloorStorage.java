package arafath65.minime.gymmanagementui.Utilss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FitFloorStorage {

    private static final String ROOT_FOLDER =
            "FitFloor";

    private static final String MEMBERS_FOLDER =
            "members";


    // =====================================================
    // FITFLOOR ROOT DIRECTORY
    // =====================================================

    private static File getRootDirectory() {

        File storage =
                Environment.getExternalStorageDirectory();

        File directory =
                new File(
                        storage,
                        ROOT_FOLDER
                );

        if (!directory.exists()) {

            directory.mkdirs();
        }

        return directory;
    }


    // =====================================================
    // MEMBERS DIRECTORY
    // =====================================================

    private static File getMembersDirectory() {

        File directory =
                new File(
                        getRootDirectory(),
                        MEMBERS_FOLDER
                );

        if (!directory.exists()) {

            directory.mkdirs();
        }

        return directory;
    }


    // =====================================================
    // SAVE MEMBER IMAGE
    // =====================================================

    public static String saveMemberImage(
            Bitmap bitmap,
            String memberNo) {

        if (bitmap == null ||
                memberNo == null ||
                memberNo.trim().isEmpty()) {

            return null;
        }


        File directory =
                getMembersDirectory();


        String fileName =
                memberNo + ".jpg";


        File imageFile =
                new File(
                        directory,
                        fileName
                );


        if (imageFile.exists()) {

            imageFile.delete();
        }


        try (
                FileOutputStream outputStream =
                        new FileOutputStream(
                                imageFile
                        )
        ) {

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    85,
                    outputStream
            );

            outputStream.flush();


            return ROOT_FOLDER
                    + "/"
                    + MEMBERS_FOLDER
                    + "/"
                    + fileName;


        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }


    // =====================================================
    // LOAD MEMBER IMAGE
    // =====================================================

    public static Bitmap loadMemberImage(
            String imagePath) {

        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            return null;
        }


        File imageFile =
                new File(
                        Environment
                                .getExternalStorageDirectory(),
                        imagePath
                );


        if (!imageFile.exists()) {

            return null;
        }


        return BitmapFactory.decodeFile(
                imageFile.getAbsolutePath()
        );
    }


    // =====================================================
    // DELETE MEMBER IMAGE
    // =====================================================

    public static boolean deleteMemberImage(
            String imagePath) {

        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            return false;
        }


        File imageFile =
                new File(
                        Environment
                                .getExternalStorageDirectory(),
                        imagePath
                );


        if (!imageFile.exists()) {

            return false;
        }


        return imageFile.delete();
    }


    // =====================================================
    // CHECK IMAGE
    // =====================================================

    public static boolean memberImageExists(
            String imagePath) {

        if (imagePath == null ||
                imagePath.trim().isEmpty()) {

            return false;
        }


        File imageFile =
                new File(
                        Environment
                                .getExternalStorageDirectory(),
                        imagePath
                );


        return imageFile.exists();
    }
}