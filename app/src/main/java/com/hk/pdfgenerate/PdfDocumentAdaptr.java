package com.hk.pdfgenerate;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.printservice.PrintDocument;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class PdfDocumentAdaptr extends PrintDocumentAdapter {
    private Context context;
    private String path;

    public PdfDocumentAdaptr(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    @Override
    public void onLayout(PrintAttributes printAttributes, PrintAttributes printAttributes1, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle bundle) {

        if(cancellationSignal.isCanceled()){
            layoutResultCallback.onLayoutCancelled();
        }
        else{
            PrintDocumentInfo.Builder builder =new PrintDocumentInfo.Builder("invoice_kshop");
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build();
            layoutResultCallback.onLayoutFinished(builder.build(),!printAttributes1.equals(printAttributes));
        }
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor parcelFileDescriptor, CancellationSignal cancellationSignal, WriteResultCallback writeResultCallback) {

        InputStream inputStream  = null;
        OutputStream outputStream = null;
        try {
            File file =new File(path);
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());

            byte[] buffer = new byte[16384];
            int size;
            while ((size = inputStream.read(buffer)) >=0 && !cancellationSignal.isCanceled()){
                outputStream.write(buffer,0,size);

            }
            if(cancellationSignal.isCanceled()){
                writeResultCallback.onWriteCancelled();
            }
            else{
                writeResultCallback.onWriteFinished(new PageRange[] {PageRange.ALL_PAGES});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Ks",e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Ks",e.getMessage());
        }
        finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Ks",e.getMessage());
            }
        }
    }
}
