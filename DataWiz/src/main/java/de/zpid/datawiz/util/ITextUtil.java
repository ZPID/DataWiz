package de.zpid.datawiz.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.DashedBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.pdfa.PdfADocument;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;

@Repository
public class ITextUtil {

  private static Logger log = LogManager.getLogger(ITextUtil.class);

  @Autowired
  private FileUtil fileUtil;

  @Autowired
  private ExportUtil exportUtil;

  @Autowired
  protected MessageSource messageSource;

  /** A path to a color profile. */
  private static final String ICC = "sRGB2014.icc";
  /** A font that will be embedded. */
  private static final String FONT = "Calibri.ttf";

  private static final float FONTSIZENORMAL = 12.0f;

  public byte[] createPdf(final RecordDTO record, boolean encrypt, boolean withAttachments) throws IOException {
    StringBuilder res = new StringBuilder();
    byte[] content = null;
    String dir = fileUtil.setFolderPath("temp");
    String filename = UUID.randomUUID().toString() + ".pdf";
    PdfADocument pdf = openPDFADocument(record, dir + filename, encrypt, res);
    if (pdf != null) {
      Document document = new Document(pdf, PageSize.A4);
      // add datamatrix as csv
      byte[] fontBytes = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(FONT));
      document.setFont(PdfFontFactory.createFont(fontBytes, true));
      document.setTextAlignment(TextAlignment.JUSTIFIED);
      document.setFontSize(FONTSIZENORMAL);
      // CSV Attachments
      if (withAttachments) {
        PdfArray array = new PdfArray();
        PdfFileSpec matrix = getRecordCSVAttachment(record, pdf, res, true);
        PdfFileSpec codebook = getRecordCSVAttachment(record, pdf, res, false);
        if (matrix != null)
          array.add(matrix.getPdfObject().getIndirectReference());
        if (codebook != null)
          array.add(codebook.getPdfObject().getIndirectReference());
        pdf.getCatalog().put(new PdfName("Attachments"), array);
      }
      // report meta page
      createRecordMetaDesc(record, document);
      // variables
      for (SPSSVarDTO var : record.getVariables()) {
        document.add(new Paragraph()
            .add(new Text(messageSource.getMessage("export.pdf.line.variable", null, Locale.ENGLISH)).setFontSize(12f))
            .add(new Text(var.getName()).setFontSize(12f).setBold()).add("\n\n"));
        document.add(createVarTable(var));
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
      }
      // Close document
      setPagenumber(pdf, document);
      document.close();
      if (Files.exists(Paths.get(dir + filename))) {
        content = Files.readAllBytes(Paths.get(dir + filename));
        fileUtil.deleteFile(Paths.get(dir + filename));
      }
    }
    return content;
  }

  /**
   * @param record
   * @param document
   */
  private void createRecordMetaDesc(final RecordDTO record, Document document) {
    document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER).setBold().add(
        messageSource.getMessage("export.pdf.line.head", new Object[] { record.getRecordName() }, Locale.ENGLISH)));
    document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
        .add(messageSource.getMessage("export.pdf.line.created",
            new Object[] { record.getCreated(), record.getCreatedBy() }, Locale.ENGLISH))
        .add("\n").add(messageSource.getMessage("export.pdf.line.updated",
            new Object[] { record.getChanged(), record.getChangedBy() }, Locale.ENGLISH))
        .add("\n").add("\n"));
    document.add(new Paragraph()
        .add(new Text(messageSource.getMessage("export.pdf.line.record.desc", null, Locale.ENGLISH)).setBold()
            .setUnderline())
        .add("\n").add(new Text(record.getDescription()).setTextAlignment(TextAlignment.JUSTIFIED)));
    document
        .add(new Paragraph()
            .add(new Text(messageSource.getMessage("export.pdf.line.last.changes", null, Locale.ENGLISH)).setBold()
                .setUnderline())
            .add("\n").add(new Text(record.getChangeLog()).setTextAlignment(TextAlignment.JUSTIFIED)));
    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
  }

  /**
   * @param pdf
   * @param document
   */
  private void setPagenumber(PdfADocument pdf, Document document) {

    int n = pdf.getNumberOfPages();
    for (int i = 1; i <= n; i++) {
      Paragraph p = new Paragraph();
      p.add(String.format("page %s of %s", i, n));
      p.setFontSize(8);
      document.showTextAligned(p, 559, 825, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
    }
  }

  private Table createVarTable(SPSSVarDTO var) {
    Paragraph p;
    DashedBorder dborder = new DashedBorder(0.5f);
    boolean top = true, right = false, bottom = true, left = false;
    Table table = new Table(new float[] { 200.0f, 400.0f });
    table.setWidthPercent(100);
    table
        .addCell(createDashedCell(messageSource.getMessage("dataset.import.report.codebook.type", null, Locale.ENGLISH),
            dborder, top, right, bottom, left))
        .addCell(createDashedCell(messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH), dborder,
            top, right, bottom, left));
    table
        .addCell(
            createDashedCell(messageSource.getMessage("dataset.import.report.codebook.label", null, Locale.ENGLISH),
                dborder, top, right, bottom, left))
        .addCell(createDashedCell(var.getLabel() != null ? var.getLabel() : "", dborder, top, right, bottom, left));
    table.addCell(
        createDashedCell(messageSource.getMessage("dataset.import.report.codebook.values", null, Locale.ENGLISH),
            dborder, top, right, bottom, left));
    Paragraph cVarVal = new Paragraph();
    for (SPSSValueLabelDTO varVal : var.getValues()) {
      cVarVal.add("\"" + varVal.getValue() + "\" = " + varVal.getLabel() + "\n");
    }
    table.addCell(createDashedCell(cVarVal, dborder, top, right, bottom, left));
    if (var.getMissingFormat().equals(SPSSMissing.SPSS_NO_MISSVAL)
        || var.getMissingFormat().equals(SPSSMissing.SPSS_UNKNOWN)) {
      table
          .addCell(createDashedCell(
              messageSource.getMessage("dataset.import.report.codebook.missings", null, Locale.ENGLISH), dborder, top,
              right, bottom, left))
          .addCell(createDashedCell(
              messageSource.getMessage("spss.missings." + SPSSMissing.SPSS_NO_MISSVAL, null, Locale.ENGLISH), dborder,
              top, right, bottom, left));
    } else {
      Cell cell = new Cell(2, 1);
      cell.add(messageSource.getMessage("dataset.import.report.codebook.missings", null, Locale.ENGLISH));
      cell.setBorder(Border.NO_BORDER);
      cell.setBorderBottom(dborder);
      cell.setBorderTop(dborder);
      table.addCell(cell);
      table.addCell(
          createDashedCell(messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH),
              dborder, top, right, bottom, left));
      if (var.getMissingFormat().equals(SPSSMissing.SPSS_ONE_MISSVAL))
        table.addCell(createDashedCell(var.getMissingVal1(), dborder, top, right, bottom, left));
      else if (var.getMissingFormat().equals(SPSSMissing.SPSS_TWO_MISSVAL))
        table.addCell(
            createDashedCell(var.getMissingVal1() + ", " + var.getMissingVal2(), dborder, top, right, bottom, left));
      else if (var.getMissingFormat().equals(SPSSMissing.SPSS_THREE_MISSVAL))
        table.addCell(createDashedCell(var.getMissingVal1() + ", " + var.getMissingVal2() + ", " + var.getMissingVal3(),
            dborder, top, right, bottom, left));
      else if (var.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGE))
        table.addCell(
            createDashedCell(var.getMissingVal1() + " - " + var.getMissingVal2(), dborder, top, right, bottom, left));
      else if (var.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGEANDVAL))
        table
            .addCell(createDashedCell(var.getMissingVal1() + " - " + var.getMissingVal2() + ", " + var.getMissingVal3(),
                dborder, top, right, bottom, left));
      else
        table.addCell("");
    }
    table
        .addCell(
            createDashedCell(messageSource.getMessage("dataset.import.report.codebook.width", null, Locale.ENGLISH),
                dborder, top, right, bottom, left))
        .addCell(createDashedCell(String.valueOf(var.getWidth()), dborder, top, right, bottom, left));
    table
        .addCell(createDashedCell(messageSource.getMessage("dataset.import.report.codebook.dec", null, Locale.ENGLISH),
            dborder, top, right, bottom, left))
        .addCell(createDashedCell(String.valueOf(var.getDecimals()), dborder, top, right, bottom, left));
    table
        .addCell(createDashedCell(messageSource.getMessage("dataset.import.report.codebook.cols", null, Locale.ENGLISH),
            dborder, top, right, bottom, left))
        .addCell(createDashedCell(String.valueOf(var.getColumns()), dborder, top, right, bottom, left));
    table
        .addCell(
            createDashedCell(messageSource.getMessage("dataset.import.report.codebook.aligment", null, Locale.ENGLISH),
                dborder, top, right, bottom, left))
        .addCell(createDashedCell(messageSource.getMessage("spss.aligment." + var.getAligment(), null, Locale.ENGLISH),
            dborder, top, right, bottom, left));
    table
        .addCell(createDashedCell(
            messageSource.getMessage("dataset.import.report.codebook.measureLevel", null, Locale.ENGLISH), dborder, top,
            right, bottom, left))
        .addCell(createDashedCell(
            messageSource.getMessage("spss.measureLevel." + var.getMeasureLevel(), null, Locale.ENGLISH), dborder, top,
            right, bottom, left));
    table
        .addCell(createDashedCell(messageSource.getMessage("dataset.import.report.codebook.role", null, Locale.ENGLISH),
            dborder, top, right, bottom, left))
        .addCell(createDashedCell(messageSource.getMessage("spss.role." + var.getRole(), null, Locale.ENGLISH), dborder,
            top, right, bottom, left));
    if (var.getAttributes() != null && var.getAttributes().size() > 0) {
      table.addCell(
          createDashedCell(messageSource.getMessage("dataset.import.report.codebook.userAtt", null, Locale.ENGLISH),
              dborder, top, right, bottom, left));
      p = new Paragraph();
      for (SPSSValueLabelDTO attr : var.getAttributes()) {
        p.add(new Text(attr.getLabel() + " = " + attr.getValue() + "\n"));
      }
      table.addCell(createDashedCell(p, dborder, top, right, bottom, left));
    }
    if (var.getDw_attributes() != null && var.getDw_attributes().size() > 1) {
      for (SPSSValueLabelDTO dwattr : var.getDw_attributes()) {
        table.addCell(createDashedCell(dwattr.getLabel(), dborder, top, right, bottom, left))
            .addCell(createDashedCell(dwattr.getValue(), dborder, top, right, bottom, left));
      }
    }
    return table;
  }

  /**
   * @param cell
   * @return
   */
  private Cell createDashedCell(Object content, Border border, boolean top, boolean right, boolean bottom,
      boolean left) {
    Cell cell = new Cell();
    cell.setBorder(Border.NO_BORDER);
    if (right)
      cell.setBorderRight(border);
    if (left)
      cell.setBorderLeft(border);
    if (bottom)
      cell.setBorderBottom(border);
    if (top)
      cell.setBorderTop(border);
    if (content instanceof String) {
      cell.add(String.valueOf(content));
    } else if (content instanceof IBlockElement) {
      cell.add((IBlockElement) content);
    } else if (content instanceof Image) {
      cell.add((Image) content);
    }
    return cell;
  }

  /**
   * @param record
   * @param pdf
   * @param res
   * @return
   */
  private PdfFileSpec getRecordCSVAttachment(final RecordDTO record, PdfADocument pdf, StringBuilder res,
      boolean matrix) {
    log.trace("Entering getDatamatrix for record[id:{}, version:{}]", () -> record.getId(),
        () -> record.getVersionId());
    PdfFileSpec fileSpec = null;
    String name = record.getRecordName() + "_" + record.getVersionId() + (matrix ? "(matrix).csv" : "(codebook).csv");
    byte[] datamatrix = exportUtil.exportCSV(record, res, matrix);
    if (datamatrix != null && datamatrix.length > 0) {
      PdfDictionary parameters = new PdfDictionary();
      fileSpec = PdfFileSpec.createEmbeddedFileSpec(pdf, datamatrix, name, name, new PdfName("text/csv"), parameters,
          PdfName.Data, false);
      fileSpec.put(new PdfName("AFRelationship"), new PdfName("Data"));
      pdf.addFileAttachment(name, fileSpec);
    } else {
      res.insert(0, "export.csv.string.empty");
    }
    log.debug("Leaving getDatamatrix with result: {}", fileSpec == null ? "NULL" : "SUCCESS");
    return fileSpec;
  }

  /**
   * @param dest
   * @param encrypt
   * @return
   * @throws FileNotFoundException
   */
  private PdfADocument openPDFADocument(final RecordDTO record, final String dest, final boolean encrypt,
      final StringBuilder res) {
    log.trace("Entering openPDFADocument for record[id:{}, version:{}], destination[{}], encrytion[{}]",
        () -> record.getId(), () -> record.getVersionId(), () -> dest, () -> encrypt);
    WriterProperties prop;
    if (encrypt)
      prop = new WriterProperties().setStandardEncryption("".getBytes(), UUID.randomUUID().toString().getBytes(),
          EncryptionConstants.ALLOW_PRINTING,
          EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
    else
      prop = new WriterProperties();
    prop.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
    prop.setPdfVersion(PdfVersion.PDF_1_7);
    PdfADocument pdf = null;

    try {
      pdf = new PdfADocument(new PdfWriter(dest, prop), PdfAConformanceLevel.PDF_A_3A, new PdfOutputIntent("Custom", "",
          "http://www.color.org", "sRGB IEC61966-2.1", this.getClass().getClassLoader().getResourceAsStream(ICC)));
    } catch (FileNotFoundException e) {
      res.insert(0, "export.error.exception.thown");
      log.error("IOException during reading PDF/A Color: {}", () -> e);
    }
    if (pdf != null) {
      pdf.setTagged();
      pdf.getCatalog().setLang(new PdfString("en-US"));
      pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
      setMetaInformation(record, pdf);
    }
    log.debug("Leaving openPDFADocument with result: {}", pdf == null ? "NULL" : "SUCCESS");
    return pdf;
  }

  /**
   * @param record
   * @param pdf
   */
  private void setMetaInformation(final RecordDTO record, PdfADocument pdf) {
    PdfDocumentInfo info = pdf.getDocumentInfo();
    info.setTitle(record.getRecordName());
    info.setCreator("Datawiz (www.datawiz.de)");
    info.setKeywords("asd,asd,asd,asdfdf,asd");
    info.setSubject(record.getDescription());
    info.setAuthor(record.getCreatedBy());
    info.setMoreInfo("DataWiz StudyId", String.valueOf(record.getStudyId()));
    info.setMoreInfo("DataWiz RecordId", String.valueOf(record.getId()));
    info.setMoreInfo("DataWiz VersionId", String.valueOf(record.getVersionId()));
  }
}
