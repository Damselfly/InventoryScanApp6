package rick.damselflysolutions.com.inventoryscan;

/**
 * Created by Rick on 2018-04-19.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.app.Application;

import java.io.Serializable;
import java.util.ArrayList;


public class objProdMaster extends Application{

    public String objItem;
    public String objFound;
    int listPosition = 0;

    public String getobjItem() {
        return objItem;
    }

    public void setobjItem(String objItem) {
        this.objItem = objItem;
    }

    public String getobjFound() {
        return objFound;
    }

    public void setobjFound(String objFound) {
        this.objFound = objFound;
    }

    public objProdMaster(String objItem, String objFound) {


        this.objItem = objItem;
        this.objFound = objFound;

    }

    public objProdMaster() {

    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

//    @Override
    public int describeContents() {
        return 0;
    }

//    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.objItem);
        dest.writeString(this.objFound);


    }

    public static final Parcelable.Creator<objProdMaster> CREATOR = new Parcelable.Creator<objProdMaster>() {
        public objProdMaster createFromParcel(Parcel in) {
            return new objProdMaster(in);
        }

        public objProdMaster[] newArray(int size) {
            return new objProdMaster[size];
        }
    };

    private objProdMaster(Parcel in) {
        this.objItem = in.readString();
        this.objFound = in.readString();

    }
}
