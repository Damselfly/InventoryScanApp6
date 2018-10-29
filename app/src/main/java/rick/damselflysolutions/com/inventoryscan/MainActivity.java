package rick.damselflysolutions.com.inventoryscan;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import java.util.Calendar;
import java.util.List;



public class MainActivity extends Activity implements View.OnClickListener {
    String TAG = "ExelLog";

    public Button btnExit, btnReview, btnAccept, btnAppend;
    public EditText txtLocation, txtItem, txtCount, txtQty, strDate;
    public Spinner spnUom;
    public Workbook wb;
    public Sheet sheet1;
    public CellStyle cs;
    public Row row;
    public Integer counter = 0;
    public String vLocation, vItem, vCount, vUoM, vQty, vDate, vFound;
    public Date currentTime;
    public File file;
    boolean success = false;
    private Integer rowCounter = 1;
    private Integer CellCount = 1;
    private objProdMaster objProdMasterData = new objProdMaster();
    private final ArrayList<objProdMaster> sAProdMastData = new ArrayList<>();
    public ArrayList<objProdMaster> sAProdMastDataPassed = new ArrayList<>();
    public Integer ProdMastCount;
    public String fromReview = "false";
    public Boolean itemFound = false;
    public ProgressBar progressBar1;
    public Boolean requiredField = false;
    public Boolean qtyclicked = false;
    public Integer fileRowCounter;
    public String folder_main;
    public String Path;
    public Workbook ws;


    private static final int REQUEST_ID_READ_PERMISSION = 100;
    private static final int REQUEST_ID_WRITE_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = (Button)findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);
        btnReview = (Button)findViewById(R.id.btnReview);
        btnReview.setOnClickListener(this);
        btnAccept = (Button)findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(this);
        btnAppend = (Button)findViewById(R.id.btnAppend);
        btnAppend.setOnClickListener(this);

        txtLocation = (EditText)findViewById(R.id.txtLocation);
        txtItem = (EditText)findViewById(R.id.txtItem);
        txtCount = (EditText)findViewById(R.id.txtCount);
        txtQty = (EditText)findViewById(R.id.txtQty);
        strDate = (EditText)findViewById(R.id.strDate);
        spnUom = (Spinner)findViewById(R.id.spnUom);

        progressBar1 = (ProgressBar)findViewById(R.id.progressBar1);

        assert progressBar1 != null;
        progressBar1.setVisibility(View.VISIBLE);

        currentTime = Calendar.getInstance().getTime();
        strDate.setText(currentTime.toString());


        // Gets the values passed from the inventory item lookup activity and
        // assigns the values to variables to be used later.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            fromReview = getIntent().getStringExtra("fromReview");

        }

        folder_main = "/INVENTORYSCANS";

        Path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath() + folder_main;
        System.out.println("Path  : " +Path );
        File FPath = new File(Path);
        if (!FPath.exists()) {
            if (!FPath.mkdir()) {
                System.out.println("***Problem creating Image folder " +Path );
            }
        }




        //New Workbook
        wb = new HSSFWorkbook();

        // Checks to see if the page was intialized from the Review activity
        if(fromReview.equals("false")){

            // Creates a new file object to use when checking if the file is already on the device
            final File oldfile = new File(Path,"/Inventory.xls");

            // Creates a new file object to use when checking if the file is already on the device
            //final File oldfile = new File(MainActivity.this.getExternalFilesDir(null), "Inventory.xls");

            // Checks for the file
            if(oldfile.exists()){

                // Calls the method to read from the ProdMaster Excel file
                readInventoryFile(this, "/Inventory.xls");

                // Calls the method to read from the ProdMaster Excel file
                readProdMasterFile(this, "/ProdMaster.xls");

                // Dialog popup to ask user if they want to create a new file or append to existing file
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked Create New
                                // delete the excel file


                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                //Yes button clicked Create New
                                                // delete the excel file
                                                oldfile.delete();


                                                addHeaderExcelFile(MainActivity.this,"/Inventory.xls");

                                                // show Accept button
                                                btnAccept.setVisibility(View.GONE);

                                                // hide the Append Button
                                                btnAppend.setVisibility(View.VISIBLE);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked Append
                                                // hide the Accept Button
                                                btnAccept.setVisibility(View.GONE);

                                                // show the Append Button
                                                btnAppend.setVisibility(View.VISIBLE);

                                                // Calls the method to read from the ProdMaster Excel file
                                                updateProdMasterObject(MainActivity.this, "/Inventory.xls");

                                                break;
                                        }
                                    }
                                };

                                // message popup to ask user if they want to Create a New Excel file or Append to existing Excel file
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Are you sure you want to delete the current Inventory.xls file and create a new one?").setPositiveButton("YES", dialogClickListener)
                                        .setNegativeButton("NO", dialogClickListener).show();
                        }
                    }
                };

                // message popup to ask user if they want to Create a New Excel file or Append to existing Excel file
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("There is a File already stored on this Device. Do you want to Append to this file or Create a New one?").setPositiveButton("Create New", dialogClickListener)
                        .setNegativeButton("Append To", dialogClickListener).show();
            }
            else{
                //addHeaderExcelFile
                // Calls the method to read from the ProdMaster Excel file
                addHeaderExcelFile(MainActivity.this, "/Inventory.xls");

                // Calls the method to read from the ProdMaster Excel file
                updateProdMasterObject(MainActivity.this, "/Inventory.xls");

                // Calls the method to read from the ProdMaster Excel file
                readProdMasterFile(this, "/ProdMaster.xls");

                // show Accept button
                btnAccept.setVisibility(View.GONE);

                // hide the Append Button
                btnAppend.setVisibility(View.VISIBLE);
            }
        }else{

            // Calls the method to read from the ProdMaster Excel file
            updateProdMasterObject(MainActivity.this, "/Inventory.xls");

            // show Accept button
            btnAccept.setVisibility(View.GONE);

            // hide the Append Button
            btnAppend.setVisibility(View.VISIBLE);

        }

        // This is a Listener for the Next button and then calculates the Quantity based on
        // the selected UoM and the Count entered
        txtCount.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button

                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER){

                    Double v1 = Double.parseDouble(!txtCount.getText().toString().isEmpty() ? txtCount.getText().toString() : "0");
                    Double v2 = Double.parseDouble(!spnUom.getSelectedItem().toString().isEmpty() ? spnUom.getSelectedItem().toString() : "0");
                    Double value = v1 * v2;
                    txtQty.setText(value.toString());

                }

                return false;
            }
        });

        // Onclick Listener for the txtQty EditText to do the calculation of the Count times the UoM
        txtQty.setInputType(InputType.TYPE_NULL);
        txtQty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!txtQty.getText().equals("") && !txtCount.getText().equals("")){

                    Double v1 = Double.parseDouble(!txtCount.getText().toString().isEmpty() ? txtCount.getText().toString() : "0");
                    Double v2 = Double.parseDouble(!spnUom.getSelectedItem().toString().isEmpty() ? spnUom.getSelectedItem().toString() : "0");
                    Double value = v1 * v2;
                    txtQty.setText(value.toString());

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
            }
        });

        // This is a Listener for the Carrage Return from the scanner
        txtItem.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    // For loop to check the ProductMaster Object against the ExcelInventory Object and sets the Status if Found
                    if(rowCounter > 0){

                        ProdMastCount = sAProdMastData.size();

                        for (int i = 0; i < ProdMastCount; i++) {

                            String enteredItem = txtItem.getText().toString();
                            String nameItem = sAProdMastData.get(i).objItem;

                            if (enteredItem.equals(nameItem)) {
                                itemFound = true;
                                break;
                            }
                        }
                    }

                    // If the item is not found in the ProdMaster list then show toast
                    if(!itemFound){

                        txtItem.setText("");
                        txtItem.clearFocus();
                        txtLocation.requestFocus();
                        txtLocation.setText("");
                        Toast.makeText(MainActivity.this, "This Item " + txtLocation.getText() + " was not found in the Product Master File.", Toast.LENGTH_LONG).show();
                    }
                }

                return false;
            }
        });

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txtLocation, InputMethodManager.SHOW_IMPLICIT);

        progressBar1.setVisibility(View.GONE);
    }

    // Button click calls
    public void onClick(View v)
    {
        switch (v.getId())
        {
            // Calls the Append method
            case R.id.btnExit:
                this.finish();
                moveTaskToBack(true);
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());

                // Calls the Review Activity method
            case R.id.btnReview:
                Intent intent = new Intent(MainActivity.this, Review.class);
                startActivity(intent);
                break;

            // Calls the SAVE method
            case R.id.btnAccept:
                saveExcelFile(this,"Inventory.xls");
                break;

            // Calls the Append method
            case R.id.btnAppend:
                appendExcelFile(this,"Inventory.xls");
                break;
        }
    }

    // Reads the data in the Inventory Excel File and creates a Object
    private void checkRowCount(Context context, String filename, int rowCounter) {

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

            fileRowCounter = mySheet.getPhysicalNumberOfRows();

            if(fileRowCounter == 0){
                Toast.makeText(MainActivity.this, "There are no records in the Inventory.xls file. If you to start with a new file, Delete the current file.", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){e.printStackTrace(); }

        progressBar1.setVisibility(View.GONE);

    }

    // Reads the data in the Inventory Excel File and creates a Object
    private  void readInventoryFile(Context context, String filename) {

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

            fileRowCounter = mySheet.getPhysicalNumberOfRows();

            if(fileRowCounter == 0){

                File file1 = new File(filename);
                boolean deleted = file1.delete();
                Log.v("log_tag","deleted: " + deleted);

                Toast.makeText(MainActivity.this, "There are no records in the Inventory.xls file. If you to start with a new file, Delete the current file.", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e){
            Toast.makeText(MainActivity.this, "The APP can't read the Inventory.xls file. Please check to see if it is on this device. ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        progressBar1.setVisibility(View.GONE);
    }

    // Reads the data in the Inventory Excel File and creates a Object
    private  void readProdMasterFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            checkAndRequestPermissions();
            String test = "test";
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

            fileRowCounter = mySheet.getPhysicalNumberOfRows();

            // Flows through all of the records
            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                while(cellIter.hasNext()){
                    //while(CellCount<=6){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " +  myCell.toString());
                    //Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();

                    if(rowCounter>=0){

                        // Gets the Cell values and sets all values to Not Found
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

        }catch (Exception e){
            Toast.makeText(MainActivity.this, "The APP can't read the ProdMaster.xls file. Please check to see if it is on this device. ", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        progressBar1.setVisibility(View.GONE);
    }

    // Reads the data in the Inventory Excel File and creates an object for the ListView
    private  void updateProdMasterObject(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            checkAndRequestPermissions();
            String test = "test";
        }

        rowCounter = 0;

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

            while(rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();

                while (cellIter.hasNext()) {

                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " + myCell.toString());

                    if (rowCounter >= 0) {

                        // Gets the Cell values and sets the variables
                        if (CellCount == 1) {

                            vLocation = myCell.toString();

                        }
                        if (CellCount == 2) {

                            vItem = myCell.toString();

                        }
                        if (CellCount == 3) {

                            vUoM = myCell.toString();

                        }
                        if (CellCount == 4) {

                            vCount = myCell.toString();

                        }
                        if (CellCount == 5) {

                            vQty = myCell.toString();

                        }
                        if (CellCount == 6) {

                            vDate = myCell.toString();

                            // For loop to check the ProductMaster Object against the ExcelInventory Object and sets the Status if Found
                            if(rowCounter > 0){

                                ProdMastCount = sAProdMastData.size();

                                for (int i = 0; i < ProdMastCount; i++) {

                                    String nameItem = sAProdMastData.get(i).objItem;
                                    if (vItem.equals(nameItem)) {
                                        sAProdMastData.set(i,sAProdMastData.get(i)).objFound = "Found";
                                    }
                                }
                            }

                            CellCount = 0;
                        }
                    }

                    CellCount++;
                }

                CellCount=1;
                rowCounter++;
            }

            progressBar1.setVisibility(View.GONE);

        }catch (Exception e){e.printStackTrace(); }
    }


    // Method to append the data to the Excel Worksheet
    private  boolean appendExcelFile(Context context, String fileName) {

        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        String str = outputFormat.format(currentTime);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            checkAndRequestPermissions();
            String test = "test";
        }

        try {
            //Environment.getExternalStorageDirectory().getPath().toString()
            //final File appendfile = new File(MainActivity.this.getExternalFilesDir(null), "Inventory.xls");

            final File appendfile = new File(Path, "/Inventory.xls");

            //FileInputStream inputStream = new FileInputStream(new File(MainActivity.this.getExternalFilesDir(null), "Inventory.xls"));

            FileInputStream inputStream = new FileInputStream(new File(Path, "/Inventory.xls"));

            Workbook workbook = WorkbookFactory.create(inputStream);

            sheet1 = workbook.getSheetAt(0);



            /////////////////////////////////////////////////////////////////
            // Checks to make sure that all required fields have been filled
            vLocation = txtLocation.getText().toString();
            vItem = txtItem.getText().toString();
            vUoM = spnUom.getSelectedItem().toString();
            vCount = txtCount.getText().toString();
            vQty = txtQty.getText().toString();
            vDate = strDate.getText().toString();

            if(vLocation.equals("")){
                requiredField=true;
            }
            if(vItem.equals("")){
                requiredField=true;
            }
            if(vUoM.equals("")){
                requiredField=true;
            }
            if(vCount.equals("")){
                requiredField=true;
            }
            if(vQty.equals("")){
                requiredField=true;
            }

            if(requiredField){
                wrongEntry();
                requiredField=false;
                return true;
            }

            /////////////////////////////////////////////////////////////////
            // check if available and not read only
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Log.e(TAG, "Storage not available or read only");
                return false;
            }

            counter = sheet1.getLastRowNum();

            counter ++;

            row = sheet1.createRow(counter);

            Cell c = null;

            c = row.createCell(0);
            c.setCellValue(vLocation);

            c = row.createCell(1);
            c.setCellValue(vItem);

            c = row.createCell(2);
            c.setCellValue(vUoM);

            c = row.createCell(3);
            c.setCellValue(vCount);

            c = row.createCell(4);
            c.setCellValue(vQty);

            c = row.createCell(5);
            c.setCellValue(str);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            //counter ++;

            FileOutputStream os = null;

            try {
                os = new FileOutputStream(appendfile);
                workbook.write(os);
                Log.w("FileUtils", "Writing file" + appendfile);
                success = true;
                clearObjects();
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

                // For loop to check the ProductMaster Object against the ExcelInventory Object and sets the Status if Found
                if(rowCounter > 0){

                    ProdMastCount = sAProdMastData.size();

                    for (int i = 0; i < ProdMastCount; i++) {

                        String nameItem = sAProdMastData.get(i).objItem;
                        if (vItem.equals(nameItem)) {
                            sAProdMastData.set(i,sAProdMastData.get(i)).objFound = "Found";
                            break;
                        }
                    }
                }
            }

        } catch (IOException | EncryptedDocumentException
                | InvalidFormatException ex) {
            ex.printStackTrace();
        }

        return success;
    }

    // Adds the Header Row to a blank Worksheet
    private  boolean addHeaderExcelFile(Context context, String fileName) {

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("myScans");

        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        String str = outputFormat.format(currentTime);

        // Create a path where we will place our List of objects on external storage
        File file = new File(Path, fileName);
        FileOutputStream os = null;

        /////////////////////////////////////////////////////////////////
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            checkAndRequestPermissions();
            String test = "test";
        }

        Cell c = null;

        /////////////////////////////////////////////////////////////////
        //Cell style for header row
        cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


        if(counter==0){

            /////////////////////////////////////////////////////////////////
            // Generate column headings on the first pass
            row = sheet1.createRow(counter);

            c = row.createCell(0);
            c.setCellValue("LOC");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("ITM");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("UOM");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("CNT");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("QTY");
            c.setCellStyle(cs);

            c = row.createCell(5);
            c.setCellValue("DATE");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            counter ++;

        }

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            clearObjects();
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)

                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }

    private  boolean saveExcelFile(Context context, String filename) {

        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        String str = outputFormat.format(currentTime);

        // Create a path where we will place our List of objects on external storage
        File file = new File(Path, filename);
        FileOutputStream os = null;

        /////////////////////////////////////////////////////////////////
        // Checks to make sure that all required fields have been filled
        vLocation = txtLocation.getText().toString();
        vItem = txtItem.getText().toString();
        vUoM = spnUom.getSelectedItem().toString();
        vCount = txtCount.getText().toString();
        vQty = txtQty.getText().toString();
        vDate = strDate.getText().toString();

        if(vLocation.equals("")){
            requiredField=true;
        }
        if(vItem.equals("")){
            requiredField=true;
        }
        if(vUoM.equals("")){
            requiredField=true;
        }
        if(vCount.equals("")){
            requiredField=true;
        }
        if(vQty.equals("")){
            requiredField=true;
        }

        if(requiredField){
            wrongEntry();
            requiredField=false;
            return true;
        }

        /////////////////////////////////////////////////////////////////
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            checkAndRequestPermissions();
            String test = "test";
        }

        Cell c = null;

        /////////////////////////////////////////////////////////////////
        //Cell style for header row
        cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        currentTime = Calendar.getInstance().getTime();

        if(counter==0){

            /////////////////////////////////////////////////////////////////
            // Generate column headings on the first pass
            row = sheet1.createRow(counter);

            c = row.createCell(0);
            c.setCellValue("LOC");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("ITM");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("UOM");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("CNT");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("QTY");
            c.setCellStyle(cs);

            c = row.createCell(5);
            c.setCellValue("DATE");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            counter ++;

            row = sheet1.createRow(counter);

            c = row.createCell(0);
            c.setCellValue(vLocation);

            c = row.createCell(1);
            c.setCellValue(vItem);

            c = row.createCell(2);
            c.setCellValue(vUoM);

            c = row.createCell(3);
            c.setCellValue(vCount);

            c = row.createCell(4);
            c.setCellValue(vQty);

            c = row.createCell(5);
            c.setCellValue(str);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            counter ++;

        }else{

            row = sheet1.createRow(counter);

            c = row.createCell(0);
            c.setCellValue(vLocation);

            c = row.createCell(1);
            c.setCellValue(vItem);

            c = row.createCell(2);
            c.setCellValue(vUoM);

            c = row.createCell(3);
            c.setCellValue(vCount);

            c = row.createCell(4);
            c.setCellValue(vQty);

            c = row.createCell(5);
            c.setCellValue(currentTime.toString());

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));

            counter ++;
        }

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            clearObjects();
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)

                    os.close();
            } catch (Exception ex) {
            }

            // For loop to check the ProductMaster Object against the ExcelInventory Object and sets the Status if Found
            if(rowCounter > 0){

                ProdMastCount = sAProdMastData.size();

                for (int i = 0; i < ProdMastCount; i++) {

                    String nameItem = sAProdMastData.get(i).objItem;
                    if (vItem.equals(nameItem)) {
                        sAProdMastData.set(i,sAProdMastData.get(i)).objFound = "Found";
                    }
                }
            }
        }

        return success;
    }

    public void clearObjects(){

        txtLocation.setText("");
        txtItem.setText("");
        txtCount.setText("");
        spnUom.setSelection(0);
        txtQty.setText("");
        txtLocation.requestFocus();

    }

    // Shows the toast on the screen when there is required enties missing
    public void wrongEntry() {
        Toast.makeText(MainActivity.this, "You have not filled in all of the required fields.", Toast.LENGTH_LONG).show();
        return;
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








    private  boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
           // ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
