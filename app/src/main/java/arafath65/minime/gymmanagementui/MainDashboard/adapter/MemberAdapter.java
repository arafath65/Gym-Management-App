package arafath65.minime.gymmanagementui.MainDashboard.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
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
import java.util.List;

import arafath65.minime.gymmanagementui.Network.ApiService;
import arafath65.minime.gymmanagementui.R;
import arafath65.minime.gymmanagementui.model.Member;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberAdapter
        extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final List<Member> members;

    private final OnMemberActionListener listener;

    private final ApiService apiService;

    private final String tenantNo;

    private final String deviceToken;

    private final LruCache<Integer, Bitmap> imageCache;


    // MEMBER ACTION LISTENER

    // MEMBER ACTION LISTENER

    public interface OnMemberActionListener {

        void onEditMember(Member member);

        void onDeactivateMember(Member member);

        void onMemberClick(Member member);

        void onGenerateQr(Member member);
    }


    // CONSTRUCTOR

    public MemberAdapter(
            List<Member> members,
            OnMemberActionListener listener,
            ApiService apiService,
            String tenantNo,
            String deviceToken) {

        this.members =
                members;

        this.listener =
                listener;

        this.apiService =
                apiService;

        this.tenantNo =
                tenantNo;

        this.deviceToken =
                deviceToken;


        final int maxMemory =
                (int) (
                        Runtime.getRuntime()
                                .maxMemory()
                                / 1024
                );

        final int cacheSize =
                maxMemory / 8;


        imageCache =
                new LruCache<Integer, Bitmap>(
                        cacheSize) {

                    @Override
                    protected int sizeOf(
                            Integer key,
                            Bitmap bitmap) {

                        return bitmap.getByteCount()
                                / 1024;
                    }
                };
    }


    // CREATE VIEW HOLDER

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.item_member,
                                parent,
                                false
                        );

        return new MemberViewHolder(
                view
        );
    }


    // BIND

    @Override
    public void onBindViewHolder(
            @NonNull MemberViewHolder holder,
            int position) {

        Member member =
                members.get(position);


        // MEMBER NUMBER

        holder.txtMemberNo.setText(
                safeText(
                        member.getMemberNo()
                )
        );


        // MEMBER NAME

        holder.txtMemberName.setText(
                safeText(
                        member.getFullName()
                )
        );


        // MOBILE

        holder.txtMemberMobile.setText(
                safeText(
                        member.getMobile()
                )
        );


        // BRANCH

        holder.txtMemberBranch.setText(
                safeText(
                        member.getBranchName()
                )
        );


        // MEMBERSHIP

        holder.txtMembership.setText(
                safeText(
                        member.getMembershipName()
                )
        );


        // MEMBERSHIP STATUS

        holder.txtMembershipStatus.setText(
                safeText(
                        member.getMembershipStatus()
                )
        );


        // RESET IMAGE

        holder.imgMember.setImageResource(
                R.drawable.ic_person
        );


        int memberId =
                member.getMemberId();


        // LOAD IMAGE

        Bitmap cachedBitmap =
                imageCache.get(
                        memberId
                );


        if (cachedBitmap != null) {

            holder.imgMember.setImageBitmap(
                    cachedBitmap
            );

        } else {

            loadMemberImage(
                    holder.imgMember,
                    memberId
            );
        }


        // MEMBER CARD CLICK

        holder.memberCard.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onMemberClick(
                                member
                        );
                    }
                }
        );


        // THREE DOT MENU

        holder.btnMemberMore.setOnClickListener(
                v -> {

                    showMemberMenu(
                            v,
                            member
                    );
                }
        );
    }


    // LOAD MEMBER IMAGE

    private void loadMemberImage(
            ImageView imageView,
            int memberId) {

        if (apiService == null ||
                tenantNo == null ||
                tenantNo.trim().isEmpty() ||
                deviceToken == null ||
                deviceToken.trim().isEmpty() ||
                memberId <= 0) {

            return;
        }

        imageView.setTag(
                memberId
        );

        apiService
                .getMemberImage(
                        tenantNo,
                        deviceToken,
                        memberId
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


                                    imageCache.put(
                                            memberId,
                                            bitmap
                                    );


                                    imageView.post(() -> {

                                        Object tag =
                                                imageView.getTag();

                                        if (tag != null &&
                                                tag instanceof Integer &&
                                                (Integer) tag
                                                        == memberId) {

                                            imageView.setImageBitmap(
                                                    bitmap
                                            );

                                        }

                                    });


                                } catch (Exception ignored) {
                                }
                            }


                            @Override
                            public void onFailure(

                                    Call<ResponseBody> call,

                                    Throwable t) {

                            }
                        }
                );



    }


    // THREE DOT MENU

    private void showMemberMenu(
            View anchor,
            Member member) {

        PopupMenu popupMenu =
                new PopupMenu(
                        anchor.getContext(),
                        anchor
                );


        popupMenu.getMenu()
                .add(
                        "View Profile"
                );


        popupMenu.getMenu()
                .add(
                        "Edit Member"
                );

        popupMenu.getMenu()
                .add(
                        "Generate QR"
                );


        popupMenu.getMenu()
                .add(
                        "Deactivate"
                );


        popupMenu.setOnMenuItemClickListener(
                menuItem -> {

                    String title =
                            menuItem
                                    .getTitle()
                                    .toString();


                    if (title.equals(
                            "View Profile")) {

                        if (listener != null) {

                            listener.onMemberClick(
                                    member
                            );
                        }

                        return true;
                    }


                    if (title.equals(
                            "Edit Member")) {

                        if (listener != null) {

                            listener.onEditMember(
                                    member
                            );
                        }

                        return true;
                    }

                    // GENERATE QR

                    if (title.equals(
                            "Generate QR")) {

                        if (listener != null) {

                            listener.onGenerateQr(
                                    member
                            );
                        }

                        return true;
                    }


                    if (title.equals(
                            "Deactivate")) {

                        if (listener != null) {

                            listener.onDeactivateMember(
                                    member
                            );
                        }

                        return true;
                    }


                    return false;
                }
        );


        popupMenu.show();
    }


    // ITEM COUNT

    @Override
    public int getItemCount() {

        return members.size();
    }


    // VIEW HOLDER

    static class MemberViewHolder
            extends RecyclerView.ViewHolder {

        View memberCard;

        ImageView imgMember;

        TextView txtMemberNo;

        TextView txtMemberName;

        TextView txtMemberMobile;

        TextView txtMemberBranch;

        TextView txtMembership;

        TextView txtMembershipStatus;

        ImageButton btnMemberMore;


        MemberViewHolder(
                @NonNull View itemView) {

            super(itemView);


            memberCard =
                    itemView.findViewById(
                            R.id.memberCard
                    );


            imgMember =
                    itemView.findViewById(
                            R.id.imgMember
                    );


            txtMemberNo =
                    itemView.findViewById(
                            R.id.txtMemberNo
                    );


            txtMemberName =
                    itemView.findViewById(
                            R.id.txtMemberName
                    );


            txtMemberMobile =
                    itemView.findViewById(
                            R.id.txtMemberMobile
                    );


            txtMemberBranch =
                    itemView.findViewById(
                            R.id.txtMemberBranch
                    );


            txtMembership =
                    itemView.findViewById(
                            R.id.txtMembership
                    );


            txtMembershipStatus =
                    itemView.findViewById(
                            R.id.txtMembershipStatus
                    );


            btnMemberMore =
                    itemView.findViewById(
                            R.id.btnMemberMore
                    );
        }
    }


    // SAFE TEXT

    private String safeText(
            String value) {

        if (value == null ||
                value.trim().isEmpty()) {

            return "--";
        }

        return value;
    }
}