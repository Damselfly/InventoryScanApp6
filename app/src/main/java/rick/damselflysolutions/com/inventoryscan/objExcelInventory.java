package rick.damselflysolutions.com.inventoryscan;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Rick on 2018-04-12.
 */



public class objExcelInventory  {

    public String objLocation;
    public String objItem;
    public String objUoM;
    public String objCount;
    public String objQuantity;
    public String objDate;

    public String getObjLocation() {
        return objLocation;
    }

    public void setObjLocation(String objLocation) {
        this.objLocation = objLocation;
    }

    public String getObjItem() {
        return objItem;
    }

    public void setObjItem(String objItem) {
        this.objItem = objItem;
    }

    public String getObjUoM() {
        return objUoM;
    }

    public void setObjUoM(String objUoM) {
        this.objUoM = objUoM;
    }

    public String getObjCount() {
        return objCount;
    }

    public void setObjCount(String objCount) {
        this.objCount = objCount;
    }

    public String getObjQuantity() {
        return objQuantity;
    }

    public void setObjQuantity(String objQuantity) {
        this.objQuantity = objQuantity;
    }

    public String getObjDate() {
        return objDate;
    }

    public void setObjDate(String objDate) {
        this.objDate = objDate;
    }

    public objExcelInventory(String objLocation, String objItem, String objUoM, String objCount, String objQuantity, String objDate) {

        this.objLocation = objLocation;
        this.objItem = objItem;
        this.objUoM = objUoM;
        this.objCount = objCount;
        this.objQuantity = objQuantity;
        this.objDate = objDate;

    }

    public objExcelInventory() {

    }
}
