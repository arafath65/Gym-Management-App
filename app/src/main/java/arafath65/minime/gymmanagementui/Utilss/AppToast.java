package arafath65.minime.gymmanagementui.Utilss;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import arafath65.minime.gymmanagementui.R;

public class AppToast {

    public enum Type {
        SUCCESS,
        WARNING,
        ERROR,
        INFO
    }


    // =========================================================
    // SUCCESS
    // =========================================================

    public static void success(
            Activity activity,
            String message) {

        show(
                activity,
                message,
                Type.SUCCESS
        );
    }


    // =========================================================
    // WARNING
    // =========================================================

    public static void warning(
            Activity activity,
            String message) {

        show(
                activity,
                message,
                Type.WARNING
        );
    }


    // =========================================================
    // ERROR
    // =========================================================

    public static void error(
            Activity activity,
            String message) {

        show(
                activity,
                message,
                Type.ERROR
        );
    }


    // =========================================================
    // INFO
    // =========================================================

    public static void info(
            Activity activity,
            String message) {

        show(
                activity,
                message,
                Type.INFO
        );
    }


    // =========================================================
    // SHOW
    // =========================================================

    private static void show(
            Activity activity,
            String message,
            Type type) {

        if (activity == null ||
                activity.isFinishing()) {

            return;
        }


        Window window =
                activity.getWindow();

        ViewGroup decorView =
                (ViewGroup) window.getDecorView();


        // -----------------------------------------------------
        // Inflate
        // -----------------------------------------------------

        View view =
                LayoutInflater
                        .from(activity)
                        .inflate(
                                R.layout.layout_app_toast,
                                decorView,
                                false
                        );


        ImageView icon =
                view.findViewById(
                        R.id.toastIcon
                );

        TextView text =
                view.findViewById(
                        R.id.toastMessage
                );


        text.setText(message);


        // -----------------------------------------------------
        // TYPE
        // -----------------------------------------------------

        int backgroundColor;
        int iconResource;


        switch (type) {

            case SUCCESS:

                backgroundColor =
                        Color.rgb(
                                18,
                                75,
                                52
                        );

                iconResource =
                        R.drawable.ic_toast_success;

                break;


            case WARNING:

                backgroundColor =
                        Color.rgb(
                                105,
                                76,
                                18
                        );

                iconResource =
                        R.drawable.ic_toast_warning;

                break;


            case ERROR:

                backgroundColor =
                        Color.rgb(
                                105,
                                30,
                                30
                        );

                iconResource =
                        R.drawable.ic_toast_error;

                break;


            default:

                backgroundColor =
                        Color.rgb(
                                30,
                                45,
                                45
                        );

                iconResource =
                        R.drawable.ic_toast_info;

                break;
        }


        icon.setImageResource(
                iconResource
        );


        // -----------------------------------------------------
        // BACKGROUND
        // -----------------------------------------------------

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                backgroundColor
        );

        background.setCornerRadius(
                22f
        );

        view.setBackground(
                background
        );


        // -----------------------------------------------------
        // LAYOUT POSITION
        // -----------------------------------------------------

        android.widget.FrameLayout.LayoutParams params =
                new android.widget.FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        params.gravity =
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        params.bottomMargin = 100;


        view.setLayoutParams(params);


        // -----------------------------------------------------
        // ADD TO WINDOW
        // -----------------------------------------------------

        decorView.addView(
                view
        );


        view.setTranslationY(
                50f
        );

        view.setAlpha(
                0f
        );


        // -----------------------------------------------------
        // ENTER ANIMATION
        // -----------------------------------------------------

        view.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(250)
                .start();


        // -----------------------------------------------------
        // EXIT ANIMATION
        // -----------------------------------------------------

        view.postDelayed(() -> {

            if (!activity.isFinishing()) {

                view.animate()
                        .translationY(30f)
                        .alpha(0f)
                        .setDuration(250)
                        .withEndAction(() -> {

                            if (view.getParent()
                                    != null) {

                                decorView.removeView(
                                        view
                                );
                            }

                        })
                        .start();
            }

        }, 2200);
    }
}