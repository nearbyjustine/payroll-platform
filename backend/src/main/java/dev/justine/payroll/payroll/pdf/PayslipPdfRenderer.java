package dev.justine.payroll.payroll.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dev.justine.payroll.employee.Employee;
import dev.justine.payroll.payroll.rules.DeductionEngine.Calculation;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

/** Renders one payslip to PDF bytes with OpenPDF (pure Java, no external process). */
@Component
public class PayslipPdfRenderer {

    private static final Font TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);

    public byte[] render(Employee employee, YearMonth period, Calculation calc) {
        // Not NumberFormat.getCurrencyInstance(PH): it emits "₱", which the built-in Helvetica font has no glyph for,
        // so the symbol silently disappears. Plain numbers + "Amounts in PHP" is unambiguous with any font.
        DecimalFormat peso = new DecimalFormat("#,##0.00");
        var out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A5);
        PdfWriter.getInstance(doc, out);
        doc.open();
        doc.add(new Paragraph("Payslip: " + period.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)), TITLE));
        doc.add(new Paragraph(employee.getFullName() + "  (" + employee.getEmployeeNo() + ")", NORMAL));
        doc.add(new Paragraph(employee.getDepartment().getName() + " · " + employee.getEmploymentType(), NORMAL));
        doc.add(new Paragraph("Amounts in PHP", NORMAL));
        doc.add(Chunk.NEWLINE);

        PdfPTable table = new PdfPTable(new float[] {3, 2});
        table.setWidthPercentage(100);
        row(table, "Gross pay", peso.format(calc.gross()), BOLD);
        calc.lines().forEach(l -> row(table, "  " + l.label(), "(" + peso.format(l.amount()) + ")", NORMAL));
        row(table, "Total deductions", "(" + peso.format(calc.totalDeductions()) + ")", BOLD);
        row(table, "NET PAY", peso.format(calc.net()), BOLD);
        doc.add(table);

        doc.add(Chunk.NEWLINE);
        doc.add(new Paragraph("Rates are simplified for a learning project.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8)));
        doc.close();
        return out.toByteArray();
    }

    private static void row(PdfPTable table, String label, String amount, Font font) {
        PdfPCell l = new PdfPCell(new Phrase(label, font));
        PdfPCell a = new PdfPCell(new Phrase(amount, font));
        a.setHorizontalAlignment(Element.ALIGN_RIGHT);
        l.setBorder(Rectangle.BOTTOM);
        a.setBorder(Rectangle.BOTTOM);
        table.addCell(l);
        table.addCell(a);
    }

    public static String key(YearMonth period, String employeeNo) {
        return "payslips/" + period + "/" + employeeNo + ".pdf";
    }

}
