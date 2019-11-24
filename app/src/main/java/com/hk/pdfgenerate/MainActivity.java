package com.hk.pdfgenerate;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hk.pdfgenerate.Common.Common;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.VerticalText;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Button generateBtn;
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Date dateobj = new Date();
    private String date = df.format(dateobj);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generateBtn = findViewById(R.id.generateButton);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        generateBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                createPDF(Common.getAppPath(MainActivity.this) + "1234_invoice.pdf");

                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void createPDF(String path) {
        if (new File(path).exists())
            new File(path).delete();
        try {
            Document document = new Document();
            //save
            PdfWriter.getInstance(document, new FileOutputStream(path));

            //open
            document.open();


            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("KShope");
            document.addCreator("Kshop Stuff");

            //fonts setting
            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
            float fontSize = 18.0f;
            float valueFontSize = 26.0f;

            BaseFont baseFont = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // Adding Title....
            Font titleFont = new Font(baseFont, 35.0f, Font.NORMAL, colorAccent);
            addnewItem(document, "Order Details", Element.ALIGN_CENTER, titleFont);

            //documents
            Font orderNumberFont = new Font(baseFont, fontSize, Font.NORMAL, colorAccent);   //order no
            addnewItem(document, "order no:", Element.ALIGN_LEFT, orderNumberFont);

            Font orderNumberValue = new Font(baseFont, valueFontSize, Font.NORMAL, BaseColor.BLACK);  //value
            addnewItem(document, "#00001", Element.ALIGN_LEFT, orderNumberValue);
            // LINE SEPARATOR
            addLineSeparator(document);


            addnewItem(document, "Order Date:", Element.ALIGN_LEFT, orderNumberFont);
            addnewItem(document, date, Element.ALIGN_LEFT, orderNumberValue);
            // LINE SEPARATOR
            addLineSeparator(document);

            addnewItem(document, "Account Name:", Element.ALIGN_LEFT, orderNumberFont);
            addnewItem(document, "Hkobir", Element.ALIGN_LEFT, orderNumberValue);
            // LINE SEPARATOR
            addLineSeparator(document);
            addLineSpace(document);

            addnewItem(document, "Product Details: ", Element.ALIGN_CENTER, titleFont);
            // LINE SEPARATOR
            addLineSeparator(document);

            //item 1
            addnewItemWithLeftAndRight(document, "Mens Full Slive Round", "(0.0%)", orderNumberValue, orderNumberValue);
            addnewItemWithLeftAndRight(document, "2*800", "1,600.0)", orderNumberValue, orderNumberValue);
            addLineSeparator(document);

            //item 2
            addnewItemWithLeftAndRight(document, "Blackberry Blazer", "(0.0%)", orderNumberValue, orderNumberValue);
            addnewItemWithLeftAndRight(document, "1*1800", "1,800.0)", orderNumberValue, orderNumberValue);
            addLineSeparator(document);

            //total
            addLineSpace(document);
            addLineSpace(document);
            addnewItemWithLeftAndRight(document, "Total:", "2,400.0)", orderNumberValue, titleFont);

            document.close();

            Toast.makeText(this, "Invoice create successfully", Toast.LENGTH_SHORT).show();

            printPdf();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void printPdf() {
        try {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter documentAdapter = new PdfDocumentAdaptr(MainActivity.this, Common.getAppPath(MainActivity.this) + "1234_invoice.pdf");
            printManager.print("document", documentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            Log.e("Ks", e.getMessage());
        }


    }

    private void addnewItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) throws DocumentException {
        Chunk chunkTextLeft = new Chunk(textLeft, textLeftFont);
        Chunk chunkTextRight = new Chunk(textRight, textRightFont);
        Paragraph p = new Paragraph(chunkTextLeft);
        p.add(new Chunk(new VerticalPositionMark()));
        p.add(chunkTextRight);
        document.add(p);

    }

    private void addLineSeparator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {

        document.add(new Paragraph(""));
    }

    private void addnewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);
        document.add(paragraph);

    }


}
