package com.work.jfidiles.BillKeeper.Fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.work.jfidiles.BillKeeper.APIConnect;
import com.work.jfidiles.BillKeeper.AppConfig;
import com.work.jfidiles.BillKeeper.Bill;
import com.work.jfidiles.BillKeeper.Response.BillTaskResponse;
import com.work.jfidiles.BillKeeper.Interfaces.CRDBill;
import com.work.jfidiles.BillKeeper.R;
import com.work.jfidiles.BillKeeper.StatusCode;
import com.work.jfidiles.BillKeeper.Utilities;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.http.HttpStatus;

import java.io.FileOutputStream;
import java.util.Date;

public class fragExport extends Fragment implements CRDBill {
    EditText etPDFName;
    Button btnSavePDF;
    private Bill[] paidBills;
    private static String FILE = Environment.getExternalStorageDirectory() + "/";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.export, container, false);
        etPDFName = (EditText) rootView.findViewById(R.id.etPDFName);
        btnSavePDF = (Button) rootView.findViewById(R.id.btnSavePDF);

        loadPaidBill();
        btnSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave(paidBills);
            }
        });
        return rootView;
    }

    private void loadPaidBill() {
        Utilities.setAPIContext(getActivity());
        APIConnect.GetBillsTask getBillsTask = new APIConnect.GetBillsTask();
        getBillsTask.crdDelegate = this;
        getBillsTask.execute(AppConfig.PAID);
    }

    private void attemptSave(Bill[] paidBills) {
        etPDFName.setError(null);
        String pdfName = etPDFName.getText().toString();
        boolean isValid = true;
        View focusView = null;
        if (TextUtils.isEmpty(pdfName)) {
            etPDFName.setError(getString(R.string.error_field_required));
            isValid = false;
            focusView = etPDFName;
        }

        if (isValid) {
            if (isSaveValid(paidBills)) {
                Toast.makeText(getActivity(), AppConfig.PDF_SAVED_AT + FILE.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        } else
            focusView.requestFocus();
    }

    private boolean isSaveValid(Bill[] paidBills) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(FILE +
                    etPDFName.getText().toString() + AppConfig.PDF_EXTENSION));

            document.open();
            addMetaData(document);
            addTitlePage(document, paidBills);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void addMetaData(Document document) {
        document.addTitle("BillKeeper");
    }

    private static void addTitlePage(Document document, Bill[] paidBills)
            throws DocumentException {

        //create table
        Anchor anchor = new Anchor("Raport\n " +
                "Generated at: " + new Date() +
                "\n\n\n", catFont);

        Chapter catPart = new Chapter(new Paragraph(anchor), 1);

        createTable(catPart, paidBills);
        document.add(catPart);
    }

    private static void createTable(Section subCatPart, Bill[] paidBills)
            throws BadElementException {
        int length = paidBills.length;
        PdfPTable table = new PdfPTable(5);

        PdfPCell c1 = new PdfPCell(new Phrase("Title"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Amount"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Category"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Notes"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        for (int i = 0; i < length; i++) {
            table.addCell(paidBills[i].getTitle());
            table.addCell(paidBills[i].getAmount());
            table.addCell(paidBills[i].getDate());
            table.addCell(paidBills[i].getCategory());
            table.addCell(paidBills[i].getNotes());
        }
        subCatPart.add(table);
    }

    @Override
    public void getBillTask(BillTaskResponse billTaskResponse) {
        HttpStatus code = billTaskResponse.code;
        String error = billTaskResponse.error;

        if (StatusCode.isOk(code)) {
            paidBills = billTaskResponse.bills;
        } else if (StatusCode.isUnauthorised(code)) {
            APIConnect.UpdateToken(getContext());
            loadPaidBill();
        } else if (StatusCode.isBadRequest(code)) {
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void deleteBillTask(BillTaskResponse deleteResponse) {}

    @Override
    public void getSingleBillTask(BillTaskResponse singleBillResponse) {}

    @Override
    public void addBillTask(BillTaskResponse addBillResponse) {}
}
