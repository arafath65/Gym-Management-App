package arafath65.minime.gymmanagementui.model;

import java.math.BigDecimal;

public class MemberFitnessAssessmentRequest {

    private String assessmentDate;

    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal bodyFat;

    private BigDecimal chest;
    private BigDecimal waist;
    private BigDecimal hips;
    private BigDecimal arms;
    private BigDecimal thigh;

    private String notes;


    public String getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(String assessmentDate) {
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


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}