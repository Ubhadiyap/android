package com.alchemistdigital.buxa.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 9/17/2016.
 */
public class CustomClearanceModel implements Parcelable {
    private int customeClearanceId;
    private int serverId;
    private String bookingId;
    private int shipmentType;
    private String stuffingType;
    private String stuffingAddress;
    private int availOption ;
    private int status ;
    private String  createdAt ;
    private String strShipmentType;

    public CustomClearanceModel() {
    }

    public CustomClearanceModel(
            int serverId,
            String bookingId,
            int shipmentType,
            String stuffingType,
            String stuffingAddress,
            int availOption,
            int status,
            String createdAt) {
        this.serverId = serverId;
        this.bookingId = bookingId;
        this.shipmentType = shipmentType;
        this.stuffingType = stuffingType;
        this.stuffingAddress = stuffingAddress;
        this.availOption = availOption;
        this.status = status;
        this.createdAt = createdAt;
    }

    public CustomClearanceModel(
            String bookingId,
            String stuffingType,
            String stuffingAddress,
            int availOption,
            int status,
            String createdAt,
            String strShipmentType) {
        this.bookingId = bookingId;
        this.stuffingType = stuffingType;
        this.stuffingAddress = stuffingAddress;
        this.availOption = availOption;
        this.status = status;
        this.createdAt = createdAt;
        this.strShipmentType = strShipmentType;
    }

    public int getCustomeClearanceId() {
        return customeClearanceId;
    }

    public void setCustomeClearanceId(int customeClearanceId) {
        this.customeClearanceId = customeClearanceId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public int getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(int shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getStuffingType() {
        return stuffingType;
    }

    public void setStuffingType(String stuffingType) {
        this.stuffingType = stuffingType;
    }

    public String getStuffingAddress() {
        return stuffingAddress;
    }

    public void setStuffingAddress(String stuffingAddress) {
        this.stuffingAddress = stuffingAddress;
    }

    public int getAvailOption() {
        return availOption;
    }

    public void setAvailOption(int availOption) {
        this.availOption = availOption;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStrShipmentType() {
        return strShipmentType;
    }

    public void setStrShipmentType(String strShipmentType) {
        this.strShipmentType = strShipmentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(serverId);
        dest.writeString(bookingId);
        dest.writeInt(shipmentType);
        dest.writeString(stuffingType);
        dest.writeString(stuffingAddress);
        dest.writeInt(availOption);
        dest.writeInt(status);
        dest.writeString(createdAt);
        dest.writeString(strShipmentType);
    }

    private CustomClearanceModel(Parcel in) {
        serverId = in.readInt();
        bookingId = in.readString();
        shipmentType = in.readInt();
        stuffingType = in.readString();
        stuffingAddress = in.readString();
        availOption = in.readInt();
        status = in.readInt();
        createdAt = in.readString();
        strShipmentType = in.readString();
    }

    public static final Parcelable.Creator<CustomClearanceModel> CREATOR = new Parcelable.Creator<CustomClearanceModel>() {

        @Override
        public CustomClearanceModel createFromParcel(Parcel source) {
            return new CustomClearanceModel(source);
        }

        @Override
        public CustomClearanceModel[] newArray(int size) {
            return new CustomClearanceModel[size];
        }
    };
}
