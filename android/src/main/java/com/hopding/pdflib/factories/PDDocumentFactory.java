package com.hopding.pdflib.factories;

import android.content.res.AssetManager;

import com.facebook.react.bridge.NoSuchKeyException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSBoolean;
import com.tom_roush.pdfbox.cos.COSDictionary;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDFieldTreeNode;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;

/**
 * Creates a PDDocument object and applies actions from a JSON
 * object to it, such as creating new pages or modifying existing
 * ones. PDDocuments can be created anew or from an existing document.
 */
public class PDDocumentFactory {

    private PDDocument document;
    private String path;

    private PDDocumentFactory(PDDocument document, ReadableMap documentActions) {
        this.path     = documentActions.getString("path");
        this.document = document;
    }

            /* ----- Factory methods ----- */
    public static PDDocument create(ReactApplicationContext reactContext,ReadableMap documentActions) throws NoSuchKeyException, IOException {
        PDDocument document = new PDDocument();
        PDDocumentFactory factory = new PDDocumentFactory(document, documentActions);

        factory.addPages(reactContext,documentActions.getArray("pages"));
        return document;
    }

    public static PDDocument modify(ReactApplicationContext reactContext,ReadableMap documentActions) throws NoSuchKeyException, IOException {
        String path = documentActions.getString("path");
        PDDocument document = PDDocument.load(new File(path));
        PDDocumentFactory factory = new PDDocumentFactory(document, documentActions);

        factory.modifyPages(reactContext,documentActions.getArray("modifyPages"));
        factory.addPages(reactContext,documentActions.getArray("pages"));
        return document;
    }

//    public static PDDocument fillPdfAssetForm(ReactApplicationContext reactContext,ReadableMap documentActions) throws NoSuchKeyException, IOException {
//        AssetManager assetManager = reactContext.getAssets();
//
//
//        String formname = documentActions.getString("form");
//        PDDocument document = PDDocument.load(assetManager.open(formname));
//
//      //  PDFont pdfFont = PDType0Font.load(document, assetManager.open("SongTi.TTF"));
//        document.getDocument().setIsXRefStream(true);
//        PDDocumentCatalog docCatalog = document.getDocumentCatalog();
//
//        PDAcroForm acroForm = docCatalog.getAcroForm();
//
//        acroForm.getDictionary().setItem(COSName.getPDFName("NeedAppearances"), COSBoolean.TRUE);
//
//        ReadableMap fileds = documentActions.getMap("fields");
//        ReadableMapKeySetIterator it = fileds.keySetIterator();
//        while(it.hasNextKey()){
//            String key = it.nextKey();
//            //暂时只支持TextField
//            PDFieldTreeNode pft =  acroForm.getField(key);
//            if(pft == null)
//                continue;
//            PDTextField field = (PDTextField)  pft;
//            //field.setDefaultAppearance("F1");
//            String val = fileds.getString(key);
//            field.setValue(val);
//            field.setReadonly(true);
//
//        }
//
//        return document;
//    }
            /* ----- Document actions (based on JSON structures sent over bridge) ----- */
    private void addPages(ReactApplicationContext reactContext,ReadableArray pages) throws IOException {
        for(int i = 0; i < pages.size(); i++) {
            PDPage page = PDPageFactory.create(reactContext,document, pages.getMap(i));
            document.addPage(page);
        }
    }

    private void modifyPages(ReactApplicationContext reactContext, ReadableArray pages) throws IOException {
        for(int i = 0; i < pages.size(); i++) {
            PDPageFactory.modify(reactContext,document, pages.getMap(i));
        }
    }

            /* ----- Static utilities ----- */
    public static String write(PDDocument document, String path) throws IOException {
        document.save(path);
        document.close();
        return path;
    }



}
