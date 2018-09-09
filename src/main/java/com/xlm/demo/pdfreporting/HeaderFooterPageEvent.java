package com.xlm.demo.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import static com.xlm.demo.utility.Utility.getCurrentDate;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

    public HeaderFooterPageEvent(PdfWriter writer) {

        Rectangle rect = new Rectangle(5, 5, 590, 810);
        writer.setBoxSize("art", rect);

    }

    public Phrase companyHeader() {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 12);
        f1.setColor(BaseColor.BLUE);
        selector1.addFont(f1);
        Phrase ph = selector1.process("DEMO AUTOMATION");
        return ph;
    }

    private Phrase defaultFont(String text) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10);
        f1.setColor(BaseColor.BLACK);
        selector1.addFont(f1);
        Phrase ph = selector1.process(text);
        return ph;
    }

    public void onStartPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, companyHeader(), rect.getWidth() / 2, rect.getTop(), 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(defaultFont(getCurrentDate())), rect.getWidth() / 8, rect.getTop(), 0);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("art");
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Bottom Left"), rect.getWidth() / 10, rect.getBottom(25), 0);
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(defaultFont("Page " + String.valueOf(writer.getPageNumber()))), rect.getWidth() - rect.getWidth() / 10, rect.getBottom(25), 0);
    }
}
