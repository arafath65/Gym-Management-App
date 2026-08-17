package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_fitness_assessments",
        indexes = {
                @Index(
                        name = "idx_fitness_member",
                        columnList = "member_id"
                ),
                @Index(
                        name = "idx_fitness_date",
                        columnList = "assessment_date"
                )
        }
)
public class MemberFitnessAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fitness_id")
    private Integer fitnessId;


    // =====================================================
    // MEMBER
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false
    )
    private Member member;


    // =====================================================
    // ASSESSMENT
    // =====================================================

    @Column(
            name = "assessment_date",
            nullable = false
    )
    private LocalDate assessmentDate;


    // =====================================================
    // BODY MEASUREMENTS
    // =====================================================

    @Column(
            name = "weight",
            precision = 6,
            scale = 2
    )
    private BigDecimal weight;


    @Column(
            name = "height",
            precision = 6,
            scale = 2
    )
    private BigDecimal height;


    @Column(
            name = "bmi",
            precision = 5,
            scale = 2
    )
    private BigDecimal bmi;


    @Column(
            name = "body_fat",
            precision = 5,
            scale = 2
    )
    private BigDecimal bodyFat;


    @Column(
            name = "chest",
            precision = 6,
            scale = 2
    )
    private BigDecimal chest;


    @Column(
            name = "waist",
            precision = 6,
            scale = 2
    )
    private BigDecimal waist;


    @Column(
            name = "hips",
            precision = 6,
            scale = 2
    )
    private BigDecimal hips;


    @Column(
            name = "arms",
            precision = 6,
            scale = 2
    )
    private BigDecimal arms;


    @Column(
            name = "thigh",
            precision = 6,
            scale = 2
    )
    private BigDecimal thigh;


    // =====================================================
    // PROGRESS PHOTOS
    // =====================================================

    @Column(
            name = "front_image",
            length = 500
    )
    private String frontImage;


    @Column(
            name = "side_image",
            length = 500
    )
    private String sideImage;


    @Column(
            name = "back_image",
            length = 500
    )
    private String backImage;


    // =====================================================
    // NOTES
    // =====================================================

    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;


    // =====================================================
    // AUDIT
    // =====================================================

    @Column(
            name = "recorded_by"
    )
    private Integer recordedBy;


    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getFitnessId() {
        return fitnessId;
    }

    public void setFitnessId(Integer fitnessId) {
        this.fitnessId = fitnessId;
    }


    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }


    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(
            LocalDate assessmentDate) {

        this.assessmentDate = assessmentDate;
    }


    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }


    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }


    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }


    public BigDecimal getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(BigDecimal bodyFat) {
        this.bodyFat = bodyFat;
    }


    public BigDecimal getChest() {
        return chest;
    }

    public void setChest(BigDecimal chest) {
        this.chest = chest;
    }


    public BigDecimal getWaist() {
        return waist;
    }

    public void setWaist(BigDecimal waist) {
        this.waist = waist;
    }


    public BigDecimal getHips() {
        return hips;
    }

    public void setHips(BigDecimal hips) {
        this.hips = hips;
    }


    public BigDecimal getArms() {
        return arms;
    }

    public void setArms(BigDecimal arms) {
        this.arms = arms;
    }


    public BigDecimal getThigh() {
        return thigh;
    }

    public void setThigh(BigDecimal thigh) {
        this.thigh = thigh;
    }


    public String getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(String frontImage) {
        this.frontImage = frontImage;
    }


    public String getSideImage() {
        return sideImage;
    }

    public void setSideImage(String sideImage) {
        this.sideImage = sideImage;
    }


    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public Integer getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Integer recordedBy) {
        this.recordedBy = recordedBy;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }


    // =====================================================
    // PRE-PERSIST
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}