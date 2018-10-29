package rick.damselflysolutions.com.inventoryscan;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.PageBreakRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.data;
import static android.R.id.list;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Review extends AppCompatActivity {

    public Button btnBack, btnNotScan, btnReview;
    public ListView lvReview;
    public TextView text3;
    String TAG = "ExelLog";
    private objExcelInventory objExcelData = new objExcelInventory();
    private final ArrayList<objExcelInventory> sAExcelData = new ArrayList<>();
    private final ArrayList<String> listofScannedItems = new ArrayList<>();
    private Integer CellCount = 1;
    private String vLocation, vItem, vUoM, vCount, vQty, vDate, vFound;
    private Integer rowCounter = 1;
    private Integer selectedRow, WhichRow;
    public Integer counter = 0;
    public Row row;
    public String vSelectedRecord, vSelectedLocation,vSelectedItem, vSelectedUom;

    public String vView = "review";

    private objProdMaster objProdMasterData = new objProdMaster();
    private final ArrayList<objProdMaster> sAProdMastData = new ArrayList<>();

    public Integer ProdMastCount;
    public String fromReview = "true";
    public Workbook wb;
    public Sheet sheet1;
    public ProgressBar progressBar1;
    public ScrollView SVMoveInventory;
    public String folder_main;
    public String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnNotScan = (Button) findViewById(R.id.btnNotScan);
        btnReview = (Button)findViewById(R.id.btnReview);
        lvReview = (ListView)findViewById(R.id.lvReview);

        progressBar1 = (ProgressBar)findViewById(R.id.progressBar1);

        SVMoveInventory = (ScrollView)findViewById(R.id.SVMoveInventory);

        assert progressBar1 != null;
        progressBar1.setVisibility(View.VISIBLE);

        folder_main = "/INVENTORYSCANS";

        Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + folder_main;

        // Calls the method to read from the ProdMaster Excel file
        readProdMasterFile(this, "/ProdMaster.xls");

        // Calls the method to read from the Excel file
        readExcelFile(this, "/Inventory.xls");

        // This code allows the Listview to have a scroll when the Listview is
        // placed inside of a ScrollView on a page
        lvReview.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        // onClick event listener for the button to set the ListView back to the Reviews
        assert btnReview != null;
        btnReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                vView = "review";

                // Calls the method to read from the ProdMaster Excel file
                readProdMasterFile(Review.this, "/ProdMaster.xls");

                // Calls the method to read from the Excel file
                readExcelFile(Review.this, "/Inventory.xls");

                // Return user to the top of page
                SVMoveInventory.scrollTo(0,0);

            }
        });

        // onClick event listener for the button to go to the Review activity
        assert btnBack != null;
        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(Review.this, MainActivity.class);

                i.putExtra("fromReview", fromReview);

                Review.this.finish();
                //startActivity(i);
            }
        });

        // onClick event listener for the button to go to the Review activity
        assert btnNotScan != null;
        btnNotScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                vView = "notfound";

                ProdMastCount = sAProdMastData.size();
                listofScannedItems.clear();

                // Flows through the rows of sAProdMastData and finds the Items that Status is Not Found
                for (int i = 0; i < ProdMastCount; i++) {

                    String notFoundItem = sAProdMastData.get(i).objItem;
                    String notFound = sAProdMastData.get(i).objFound;

                    if(notFound.equals("Not Found")){

                        // Cretes the Array to be assigned to the Adapter for the ListView
                        listofScannedItems.add("Itm: " + notFoundItem + "  Status: " + notFound);

                    }
                }

                // Creates the ArrayAdapter for the ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Review.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, listofScannedItems) {

                };

                // Assigns the adapter to the ListView
                lvReview.setAdapter(adapter);
            }
        });

        lvReview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                WhichRow = position+1;

                if(vView.equals("review")){

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked

                                    removeRow(Review.this, "/Inventory.xls",WhichRow);
                                    renameSheets(Review.this, "/Inventory.xls");
                                    readExcelFile(Review.this, "/Inventory.xls");

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked

                                    Toast.makeText(Review.this, "Your REQUEST has been Cancelled.", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(Review.this);
                    builder.setMessage("Are you sure you want to Delete row number " + WhichRow).setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            }
        });

        progressBar1.setVisibility(View.GONE);
    }

    // Reads the data in the Inventory Excel File and creates an object for the ListView
    private  void readProdMasterFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(Path, filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                while(cellIter.hasNext()){
                    //while(CellCount<=6){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " +  myCell.toString());
                    //Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();

                    if(rowCounter>=0){

                        // Gets the Cell values and sets the variables
                        if(CellCount==1){

                            vItem = myCell.toString();

                            vFound = "Not Found";

                            CellCount=0;

                            objProdMasterData.setobjItem(vItem);
                            objProdMasterData.setobjFound(vFound);

                            sAProdMastData.add(objProdMasterData);

                            // Sets a new object
                            objProdMasterData = new objProdMaster();

                        }
                    }

                    CellCount++;
                }

                CellCount=1;
                rowCounter++;

            }

        }catch (Exception e){e.printStackTrace(); }

    }

    // Removes the selected row from the Inventory.xls file
    private  void removeRow(Context context, String filename, Integer rowNumber) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        try {

            final File appendfile = new File(Path, "/Inventory.xls");

            FileInputStream inputStream = new FileInputStream(new File(Path, "/Inventory.xls"));
            Workbook workbook = WorkbookFactory.create(inputStream);

            sheet1 = workbook.createSheet("COPYInventory");

            counter = 0;

            int recordcounter = sAExcelData.size();
            for (int i = 0; i < recordcounter; i++) {

                if (rowNumber != i) {

                    /////////////////////////////////////////////////////////////////
                    // Checks to make sure that all required fields have been filled

                    vLocation = sAExcelData.get(i).objLocation;
                    vItem = sAExcelData.get(i).objItem;
                    vCount = sAExcelData.get(i).objCount;
                    vUoM = sAExcelData.get(i).objUoM;
                    vQty = sAExcelData.get(i).objQuantity;
                    vDate = sAExcelData.get(i).objDate;

                    row = sheet1.createRow(counter);

                    Cell c = null;

                    c = row.createCell(0);
                    c.setCellValue(vLocation);

                    c = row.createCell(1);
                    c.setCellValue(vItem);

                    c = row.createCell(2);
                    c.setCellValue(vCount);

                    c = row.createCell(3);
                    c.setCellValue(vUoM);

                    c = row.createCell(4);
                    c.setCellValue(vQty);

                    c = row.createCell(5);
                    c.setCellValue(vDate);

                    sheet1.setColumnWidth(0, (15 * 500));
                    sheet1.setColumnWidth(1, (15 * 500));
                    sheet1.setColumnWidth(2, (15 * 500));
                    sheet1.setColumnWidth(3, (15 * 500));
                    sheet1.setColumnWidth(4, (15 * 500));
                    sheet1.setColumnWidth(5, (15 * 500));

                    counter++;

                    FileOutputStream os = null;

                    try {
                        os = new FileOutputStream(appendfile);
                        workbook.write(os);
                        Log.w("FileUtils", "Writing file" + appendfile);
                        //success = true;
                        //clearObjects();
                    } catch (IOException e) {
                        Log.w("FileUtils", "Error writing " + appendfile, e);
                    } catch (Exception e) {
                        Log.w("FileUtils", "Failed to save file", e);
                    } finally {
                        try {
                            if (null != os)

                                os.close();
                        } catch (Exception ex) {
                        }
                    }
                }
            }

        } catch (IOException | EncryptedDocumentException
                | InvalidFormatException ex) {
            ex.printStackTrace();
        }
    }

    private void renameSheets(Context context, String filename) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        final File appendfile = new File(Path, "/Inventory.xls");

        try{
            // Creating Input Stream
            File file = new File(Path, filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            int index;

            HSSFSheet sheet = myWorkBook.getSheet("myScans");
            if(sheet != null)   {
                index = myWorkBook.getSheetIndex(sheet);
                myWorkBook.removeSheetAt(index);
            }

            HSSFSheet sheet1 = myWorkBook.getSheet("COPYInventory");
            if(sheet1 != null)   {
                index = myWorkBook.getSheetIndex(sheet1);
                myWorkBook.setSheetName(index,"myScans");
            }

            FileOutputStream os = null;

            try {
                os = new FileOutputStream(appendfile);
                myWorkBook.write(os);
                Log.w("FileUtils", "Writing file" + appendfile);
                //success = true;
                //clearObjects();
            } catch (IOException e) {
                Log.w("FileUtils", "Error writing " + appendfile, e);
            } catch (Exception e) {
                Log.w("FileUtils", "Failed to save file", e);
            } finally {
                try {
                    if (null != os)

                        os.close();
                } catch (Exception ex) {
                }
            }

        }catch (Exception e){e.printStackTrace(); }

    }


    private void populatLowerGrid(Context context, String filename,Integer rowNumber) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        rowCounter = 1;
        CellCount = 0;

        try{
            // Creating Input Stream
            File file = new File(Path, filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            listofScannedItems.clear();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " +  myCell.toString());

                    if(rowNumber == rowCounter){

                        if(CellCount==1){

                            objExcelData.setObjLocation(myCell.toString());
                            vLocation = objExcelData.objLocation;

                        }
                        if(CellCount==2){

                            objExcelData.setObjItem(myCell.toString());
                            vItem = objExcelData.objItem;

                        }
                        if(CellCount==3){

                            objExcelData.setObjUoM(myCell.toString());
                            vUoM = objExcelData.objUoM;

                        }
                        if(CellCount==4){

                            objExcelData.setObjCount(myCell.toString());
                            vCount = objExcelData.objCount;
                        }
                        if(CellCount==5){

                            objExcelData.setObjQuantity(myCell.toString());
                            vQty = objExcelData.objQuantity;

                        }
                        if(CellCount==6){

                            objExcelData.setObjDate(myCell.toString());
                            vDate = objExcelData.objDate;
                            CellCount=0;
                            sAExcelData.add(objExcelData);

                        }
                    }

                    listofScannedItems.add("Loc " + vLocation + "Itm " + vItem + "UoM " + vUoM + "Cnt " + vCount + "\n" + "Qty " + vQty + "Date " + vDate);

                    // Sets a new object
                    objExcelData = new objExcelInventory();

                    CellCount++;
                }
                CellCount=1;
                rowCounter++;
            }

            // Creates the ArrayAdapter for the Gridview
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Review.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listofScannedItems) {

            };

            // Assign adapter to ListView
            //lvItems.setAdapter(adapter);

        }catch (Exception e){e.printStackTrace(); }

    }


    // Reads the data in the Inventory Excel File and creates an object for the ListView
    private  void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(Path, filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            // Clears the listofScannedItems Array
            listofScannedItems.clear();

            rowCounter = 0;

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                while(cellIter.hasNext()){

                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " +  myCell.toString());

                    if(rowCounter>=0){

                        // Gets the Cell values and sets the variables
                        if(CellCount==1){

                            vLocation = myCell.toString();

                        }
                        if(CellCount==2){

                            vItem = myCell.toString();

                        }
                        if(CellCount==3){

                            vUoM = myCell.toString();

                        }
                        if(CellCount==4){

                            vCount = myCell.toString();

                        }
                        if(CellCount==5){

                            vQty = myCell.toString();

                        }
                        if(CellCount==6){

                            vDate = myCell.toString();

                            CellCount=0;

                            objExcelData.setObjLocation(vLocation);
                            objExcelData.setObjItem(vItem);
                            objExcelData.setObjUoM(vUoM);
                            objExcelData.setObjCount(vCount);
                            objExcelData.setObjQuantity(vQty);
                            objExcelData.setObjDate(vDate);

                            sAExcelData.add(objExcelData);

                            // Sets a new object
                            objExcelData = new objExcelInventory();

                            if(rowCounter>0){

                                ProdMastCount = sAProdMastData.size();

                                for (int i = 0; i < ProdMastCount; i++) {

                                    String MasterItem = sAProdMastData.get(i).objItem;

                                    if (vItem.equals(MasterItem)) {
                                        sAProdMastData.set(i,sAProdMastData.get(i)).objFound = "Found";
                                        break;
                                    }
                                }
                            }
                        }
                    }

                   CellCount++;
                }

                // Skips the first header row the creates the list of variables to be displayed in the ListView
                if(rowCounter>=1) {
                    listofScannedItems.add("Loc: " + vLocation + " Itm: " + vItem + " UoM: " + vUoM + "\n" + "Cnt: " + vCount + " Qty: " + vQty + " Date: " + vDate);
                }

                CellCount=1;
                rowCounter++;

                }

            // Creates the ArrayAdapter for the Listview
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Review.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listofScannedItems) {

            };

            // Assign adapter to ListView
            lvReview.setAdapter(adapter);

        }catch (Exception e){e.printStackTrace(); }
    }

    // Checks the
    public boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
