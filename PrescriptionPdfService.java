package com.healthdesk.service;

import com.healthdesk.model.EMR;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class PrescriptionPdfService {

    public byte[] generatePrescriptionPdf(EMR emr) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,  new BaseColor(0, 87, 168));
            Font labelFont  = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 10);
            Font smallFont  = new Font(Font.FontFamily.HELVETICA,  8, Font.ITALIC, BaseColor.GRAY);

            Paragraph title = new Paragraph("HealthDesk Clinic", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Electronic Medical Prescription", normalFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            LineSeparator ls = new LineSeparator();
            ls.setLineColor(new BaseColor(0, 87, 168));
            document.add(new Chunk(ls));
            document.add(Chunk.NEWLINE);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(10);
            addInfoCell(infoTable, "Patient:",        emr.getPatient().getName(), labelFont, normalFont);
            addInfoCell(infoTable, "Doctor:",         "Dr. " + emr.getDoctor().getName(), labelFont, normalFont);
            addInfoCell(infoTable, "Date:",           emr.getVisitDate().toString(), labelFont, normalFont);
            addInfoCell(infoTable, "Specialization:", emr.getDoctor().getSpecialization() != null
                ? emr.getDoctor().getSpecialization() : "General", labelFont, normalFont);
            document.add(infoTable);

            document.add(new Chunk(ls));
            document.add(Chunk.NEWLINE);

            Paragraph diagLabel = new Paragraph("DIAGNOSIS", labelFont);
            diagLabel.setSpacingBefore(10);
            document.add(diagLabel);
            document.add(new Paragraph(emr.getDiagnosis() != null ? emr.getDiagnosis() : "—", normalFont));

            Paragraph rxLabel = new Paragraph("PRESCRIPTION (Rx)", labelFont);
            rxLabel.setSpacingBefore(15);
            document.add(rxLabel);
            document.add(new Paragraph(emr.getPrescription() != null ? emr.getPrescription() : "—", normalFont));

            if (emr.getNotes() != null && !emr.getNotes().isBlank()) {
                Paragraph notesLabel = new Paragraph("NOTES", labelFont);
                notesLabel.setSpacingBefore(15);
                document.add(notesLabel);
                document.add(new Paragraph(emr.getNotes(), normalFont));
            }

            document.add(Chunk.NEWLINE);
            document.add(new Chunk(ls));
            Paragraph footer = new Paragraph(
                "This is a computer-generated prescription from HealthDesk.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font lf, Font nf) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, lf));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, nf));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }
}
