package com.hopding.pdflib;

import android.content.res.AssetManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;

import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.itextpdf.text.pdf.PdfAcroForm;
import com.itextpdf.text.Document;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.AcroFields;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.TextField;

import java.io.FileOutputStream;

//import com.itextpdf.text.pdf

/**
 * Created by fukai on 2018/1/19.
 */

public class PDFormFiller {
    static  BaseFont bfChinese =null;
    static {
        try
        {
            bfChinese = BaseFont.createFont("assets/STSONG.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    static String fillForm(ReactApplicationContext reactContext, ReadableMap documentActions) {
        AssetManager assetManager = reactContext.getAssets();

        try {
            //BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

            String formname = documentActions.getString("form");
            String path = documentActions.getString("path");
            PdfReader reader = new PdfReader(assetManager.open(formname));
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(path));
            stamper.setFormFlattening(true);
            AcroFields form = stamper.getAcroFields();        // PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
            ReadableMap fileds = documentActions.getMap("fields");
            ReadableMapKeySetIterator it = fileds.keySetIterator();
            while (it.hasNextKey()) {
                String key = it.nextKey();
               // if(key.equals("USRNAM"))
                     form.setFieldProperty(key,"textfont",bfChinese,null);
                form.setField(key, fileds.getString(key));
            }

            stamper.close();
            reader.close();
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


