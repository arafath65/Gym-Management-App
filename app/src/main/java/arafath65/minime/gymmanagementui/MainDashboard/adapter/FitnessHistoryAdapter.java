package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.ApiResponse;
import arafath65.minime.gymmanagementui.model.MemberFitnessAssessmentResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FitnessHistoryAdapter
        extends RecyclerView.Adapter<FitnessHistoryAdapter.ViewHolder> {


    // =====================================================
    // ACTION LISTENER
    // =====================================================

    public interface OnFitnessActionListener {

        void onEditFitness(
                MemberFitnessAssessmentResponse assessment
        );

        void onDeleteFitness(
                MemberFitnessAssessmentResponse assessment
        );
    }


    // =====================================================
    // DATA
    // =====================================================

    private final List<MemberFitnessAssessmentResponse>
            assessments;

    private final OnFitnessActionListener listener;

    private final ApiService apiService;

    private final String tenantNo;

    private final String deviceToken;


    // =====================================================
    // EXPANDED ITEM
    // =====================================================

    private int expandedPosition = -1;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public FitnessHistoryAdapter(

            List<MemberFitnessAssessmentResponse> assessments,

            OnFitnessActionListener listener,

            ApiService apiService,

            String tenantNo,

            String deviceToken) {

        this.assessments =
                assessments;

        this.listener =
                listener;

        this.apiService =
                apiService;

        this.tenantNo =
                tenantNo;

        this.deviceToken =
                deviceToken;
    }


    // =====================================================
    // CREATE VIEW
    // =====================================================

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_fitness_history,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    // =====================================================
    // BIND
    // =====================================================

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MemberFitnessAssessmentResponse item =
                assessments.get(position);


        // =================================================
        // DATE
        // =================================================

        holder.txtHistoryDate.setText(
                safeText(
                        item.getAssessmentDate()
                )
        );


        // =================================================
        // WEIGHT
        // =================================================

        holder.txtHistoryWeight.setText(
                formatValue(
                        item.getWeight(),
                        " kg"
                )
        );


        // =================================================
        // BMI
        // =================================================

        holder.txtHistoryBmi.setText(
                "BMI " +
                        formatValue(
                                item.getBmi(),
                                ""
                        )
        );


        // =================================================
        // CHEST
        // =================================================

        holder.txtHistoryChest.setText(
                "Chest " +
                        formatValue(
                                item.getChest(),
                                " cm"
                        )
        );


        // =================================================
        // WAIST
        // =================================================

        holder.txtHistoryWaist.setText(
                "Waist " +
                        formatValue(
                                item.getWaist(),
                                " cm"
                        )
        );


        // =================================================
        // HIPS
        // =================================================

        holder.txtHistoryHips.setText(
                "Hips " +
                        formatValue(
                                item.getHips(),
                                " cm"
                        )
        );


        // =================================================
        // ARMS
        // =================================================

        holder.txtHistoryArms.setText(
                "Arms " +
                        formatValue(
                                item.getArms(),
                                " cm"
                        )
        );


        // =================================================
        // THIGH
        // =================================================

        holder.txtHistoryThigh.setText(
                "Thigh " +
                        formatValue(
                                item.getThigh(),
                                " cm"
                        )
        );


        // =================================================
        // RESET IMAGE VIEWS
        // =================================================

        holder.imgHistoryFront.setImageResource(
                R.drawable.ic_person
        );

        holder.imgHistorySide.setImageResource(
                R.drawable.ic_person
        );

        holder.imgHistoryBack.setImageResource(
                R.drawable.ic_person
        );


        // =================================================
        // EXPANSION STATE
        // =================================================

        boolean expanded =
                position == expandedPosition;


        holder.layoutFitnessPhotos.setVisibility(
                expanded
                        ? View.VISIBLE
                        : View.GONE
        );


        // =================================================
        // LOAD IMAGES ONLY WHEN EXPANDED
        // =================================================

        if (expanded) {

            loadImage(
                    holder.imgHistoryFront,
                    item.getFitnessId(),
                    "front"
            );

            loadImage(
                    holder.imgHistorySide,
                    item.getFitnessId(),
                    "side"
            );

            loadImage(
                    holder.imgHistoryBack,
                    item.getFitnessId(),
                    "back"
            );
        }


        // =================================================
        // CARD CLICK
        // =================================================

        holder.itemView.setOnClickListener(
                v -> {

                    int currentPosition =
                            holder.getBindingAdapterPosition();


                    if (currentPosition ==
                            RecyclerView.NO_POSITION) {

                        return;
                    }


                    toggleExpanded(
                            currentPosition
                    );
                }
        );


        // =================================================
        // THREE DOT MENU
        // =================================================

        holder.btnFitnessMenu.setOnClickListener(
                v -> showFitnessMenu(
                        holder.btnFitnessMenu,
                        item
                )
        );
    }


    // =====================================================
    // TOGGLE EXPANSION
    // =====================================================

    private void toggleExpanded(
            int position) {

        if (expandedPosition ==
                position) {

            expandedPosition =
                    -1;

            notifyItemChanged(
                    position
            );

            return;
        }


        int previousPosition =
                expandedPosition;


        expandedPosition =
                position;


        if (previousPosition >= 0 &&
                previousPosition <
                        assessments.size()) {

            notifyItemChanged(
                    previousPosition
            );
        }


        notifyItemChanged(
                position
        );
    }


    // =====================================================
    // LOAD IMAGE
    // =====================================================

    private void loadImage(

            ImageView imageView,

            int fitnessId,

            String imageType) {


        // -------------------------------------------------
        // SAFETY
        // -------------------------------------------------

        if (apiService == null ||
                tenantNo == null ||
                tenantNo.trim().isEmpty() ||
                deviceToken == null ||
                deviceToken.trim().isEmpty()) {

            return;
        }


        apiService
                .getFitnessImage(
                        tenantNo,
                        deviceToken,
                        getMemberId(),
                        fitnessId,
                        imageType
                )
                .enqueue(
                        new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(

                                    Call<ResponseBody> call,

                                    Response<ResponseBody>
                                            response) {


                                if (!response.isSuccessful() ||
                                        response.body() == null) {

                                    return;
                                }


                                try {

                                    InputStream inputStream =
                                            response.body()
                                                    .byteStream();


                                    Bitmap bitmap =
                                            BitmapFactory
                                                    .decodeStream(
                                                            inputStream
                                                    );


                                    inputStream.close();


                                    if (bitmap == null) {

                                        return;
                                    }


                                    imageView.post(() ->
                                            imageView
                                                    .setImageBitmap(
                                                            bitmap
                                                    )
                                    );


                                } catch (Exception ignored) {
                                }
                            }


                            @Override
                            public void onFailure(

                                    Call<ResponseBody> call,

                                    Throwable t) {

                                // Keep placeholder.
                            }
                        }
                );
    }


    // =====================================================
    // MEMBER ID
    // =====================================================

    private int memberId;


    public void setMemberId(
            int memberId) {

        this.memberId =
                memberId;
    }


    private int getMemberId() {

        return memberId;
    }


    // =====================================================
    // FITNESS MENU
    // =====================================================

    private void showFitnessMenu(

            View anchor,

            MemberFitnessAssessmentResponse assessment) {

        PopupMenu popupMenu =
                new PopupMenu(
                        anchor.getContext(),
                        anchor
                );


        popupMenu.getMenu().add(
                "Edit"
        );


        popupMenu.getMenu().add(
                "Delete"
        );


        popupMenu.setOnMenuItemClickListener(
                menuItem -> {

                    String title =
                            menuItem
                                    .getTitle()
                                    .toString();


                    if (title.equals("Edit")) {

                        if (listener != null) {

                            listener.onEditFitness(
                                    assessment
                            );
                        }

                        return true;
                    }


                    if (title.equals("Delete")) {

                        if (listener != null) {

                            listener.onDeleteFitness(
                                    assessment
                            );
                        }

                        return true;
                    }


                    return false;
                }
        );


        popupMenu.show();
    }


    // =====================================================
    // COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return assessments.size();
    }


    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class ViewHolder
            extends RecyclerView.ViewHolder {


        TextView txtHistoryDate;

        TextView txtHistoryWeight;

        TextView txtHistoryBmi;

        TextView txtHistoryChest;

        TextView txtHistoryWaist;

        TextView txtHistoryHips;

        TextView txtHistoryArms;

        TextView txtHistoryThigh;


        ImageButton btnFitnessMenu;


        View layoutFitnessPhotos;

        ImageView imgHistoryFront;

        ImageView imgHistorySide;

        ImageView imgHistoryBack;


        ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtHistoryDate =
                    itemView.findViewById(
                            R.id.txtHistoryDate
                    );


            txtHistoryWeight =
                    itemView.findViewById(
                            R.id.txtHistoryWeight
                    );


            txtHistoryBmi =
                    itemView.findViewById(
                            R.id.txtHistoryBmi
                    );


            txtHistoryChest =
                    itemView.findViewById(
                            R.id.txtHistoryChest
                    );


            txtHistoryWaist =
                    itemView.findViewById(
                            R.id.txtHistoryWaist
                    );


            txtHistoryHips =
                    itemView.findViewById(
                            R.id.txtHistoryHips
                    );


            txtHistoryArms =
                    itemView.findViewById(
                            R.id.txtHistoryArms
                    );


            txtHistoryThigh =
                    itemView.findViewById(
                            R.id.txtHistoryThigh
                    );


            btnFitnessMenu =
                    itemView.findViewById(
                            R.id.btnFitnessMenu
                    );


            layoutFitnessPhotos =
                    itemView.findViewById(
                            R.id.layoutFitnessPhotos
                    );


            imgHistoryFront =
                    itemView.findViewById(
                            R.id.imgHistoryFront
                    );


            imgHistorySide =
                    itemView.findViewById(
                            R.id.imgHistorySide
                    );


            imgHistoryBack =
                    itemView.findViewById(
                            R.id.imgHistoryBack
                    );
        }
    }


    // =====================================================
    // FORMAT
    // =====================================================

    private String formatValue(
            BigDecimal value,
            String suffix) {

        if (value == null) {

            return "--";
        }


        return value
                .stripTrailingZeros()
                .toPlainString()
                + suffix;
    }


    // =====================================================
    // SAFE TEXT
    // =====================================================

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "--";
        }


        return value;
    }
}