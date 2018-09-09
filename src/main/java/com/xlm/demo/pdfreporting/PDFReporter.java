package com.xlm.demo.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PDFReporter {

    public static void main(String[] args) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("./Reports/iTextHelloWorld.pdf"));

        /*SET HEADER AND FOOTER FOR THE PDF*/
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(writer);
        writer.setPageEvent(event);

        /*OPEN THE PDF DOCUMENT FOR WRITING*/
        document.open();

        /*SET THE LOGO FOR THE DOCUMENT, THE IMAGE IS PICKED FROM SCREENSHOTS DIRECTORY*/
        setReportLogo(document);

        /*SET TEST OBJECTIVE AND ACCEPTANCE CRITERIA FOR THE DOCUMENT*/
        setTestObjective(document);

        /*SET TEST SUMMARY REPORT WITH THE EXECUTION NUMERIC TABLES */
        setSummaryReport(document);

        PDFTestReportModel model = new PDFTestReportModel();
        model.setTestName("First Test Case");
        model.setTestResult("Pass");

        PDFTestReportModel model1 = new PDFTestReportModel();
        model1.setTestName("Second Test Case");
        model1.setTestResult("Skip");

        PDFTestReportModel model2 = new PDFTestReportModel();
        model2.setTestName("Third Test Case");
        model2.setTestResult("Fail");
        List<String> testsdesc = new ArrayList<>();

        testsdesc.add("First Step Passed");
        testsdesc.add("Seconnd Step Passed");
        testsdesc.add("Third Step Passed");

        model.setTestDescriptions(testsdesc);
        model1.setTestDescriptions(testsdesc);
        model2.setTestDescriptions(testsdesc);

        document.add(new Paragraph("\n\n\n\n\n"));
        document.add(model.setTestResultTable());
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("\n"));
        document.add(model1.setTestResultTable());
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("\n"));
        document.add(model2.setTestResultTable());
        document.add(new Paragraph("\n"));

        document.close();
    }

    private static void setSummaryReport(Document document) throws Exception {

        document.add(new Paragraph("\n\n\n"));
        PdfPTable table = new PdfPTable(2);

        PdfPCell summaryCell = setCellFonts(setFont("Summary Report", 22, BaseColor.BLUE), Element.ALIGN_CENTER, Element.ALIGN_CENTER);
        summaryCell.setColspan(2);
        table.addCell(summaryCell);

        Image img = Image.getInstance("./screenshots/xlm-logo.jpg");
        img.scalePercent(18);

        PdfPCell imageCell = new PdfPCell(img);
        imageCell.setRowspan(4);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(imageCell);

        PdfPTable summarytable = new PdfPTable(4);
        summarytable.addCell(setCellFonts(setFont("Total", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Pass", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Fail", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Skip", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));

        summarytable.addCell(setCellFonts(setFont("10", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("5", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("3", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("2", 14, BaseColor.BLACK), Element.ALIGN_CENTER, Element.ALIGN_CENTER));

        table.addCell(new Paragraph("\n\n\n"));
        table.addCell(summarytable);
        table.addCell(new Paragraph("\n\n\n"));


        document.add(table);
        document.add(new Paragraph("\n\n\n"));
    }

    private static void setTestObjective(Document document) throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        table.setWidths(new int[]{1, 2});

        table.addCell(setCellFonts(setFont("Test Objective", 14, BaseColor.BLUE), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("Getting the current page " +
                "number is easy. You have a " +
                "PdfWriter instance named writer. " +
                "You can ask that instance for " +
                "the current page number", 11, BaseColor.BLACK), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));

        table.addCell(setCellFonts(setFont("Test Acceptance", 14, BaseColor.BLUE), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("Getting the total number of " +
                "pages is impossible unless you can look " +
                "into the future. When you're on page 1,", 11, BaseColor.BLACK), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));

        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        document.add(table);
        document.add(new Paragraph("\n\n"));
    }

    private static void setReportLogo(Document document) throws Exception {
        document.add(new Paragraph("\n\n"));
        Image img = Image.getInstance("./screenshots/xlm-logo.jpg");
        img.setAlignment(Image.ALIGN_CENTER);
        img.scalePercent(50);
        document.add(img);
        document.add(new Paragraph("\n\n"));
    }

    public static Phrase setFont(String text, int size, BaseColor color) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, size);
        f1.setColor(color);
        selector1.addFont(f1);
        Phrase ph = selector1.process(text);
        return ph;
    }

    public static PdfPCell setCellFonts(Phrase phrase, int horizontalAlignment, int verticalAlignment) {
        PdfPCell AlignCell = new PdfPCell(phrase);
        AlignCell.setHorizontalAlignment(horizontalAlignment);
        AlignCell.setVerticalAlignment(verticalAlignment);

        return AlignCell;
    }

}
