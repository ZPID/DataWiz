package de.zpid.datawiz.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.DeviceRgb;
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

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.service.ExportService;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSVarTypes;

@Component
public class ITextUtil {

	private static Logger log = LogManager.getLogger(ITextUtil.class);

	@Autowired
	private FileUtil fileUtil;

	@Autowired
	private ExportService exportUtil;

	@Autowired
	protected MessageSource messageSource;

	/** A path to a color profile. */
	private final String ICC = "sRGB2014.icc";
	/** A font that will be embedded. */
	private final String FONT = "Calibri.ttf";
	/** TODO LogoImage */
	private final String DATAWIZ_LOGO = "http://localhost:8080/DataWiz/static/images/dw_logo.png";
	private final String ZPID_LOGO = "http://localhost:8080/DataWiz/static/images/ZPID-logo_EN.jpg";

	private final float FONTSIZENORMAL = 12.0f;

	public byte[] createPdf(final RecordDTO record, final StudyDTO study, final ProjectDTO project, final ContributorDTO primaryContri,
	    final boolean encrypt, final boolean withAttachments) throws IOException {
		StringBuilder res = new StringBuilder();
		byte[] content = null;
		String dir = fileUtil.setFolderPath("temp");
		Files.createDirectories(Paths.get(dir));
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
			createRecordMetaDesc(record, study, project, primaryContri, document);
			// variables
			for (SPSSVarDTO var : record.getVariables()) {
				// TODO STATS!!!!!
				SummaryStatistics stats = getBasicVariableStatistics(record.getDataMatrix(), (var.getPosition() - 1), var.getType());
				if (stats != null)
					System.out.println(var.getPosition() - 1 + "  Mean: " + stats.getMean() + " Max: " + stats.getMax() + " Min: " + stats.getMin() + " SD:"
					    + stats.getStandardDeviation());
				document.add(new Paragraph().add(new Text(messageSource.getMessage("export.pdf.line.variable", null, Locale.ENGLISH)).setFontSize(12f))
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
	private void createRecordMetaDesc(final RecordDTO record, final StudyDTO study, final ProjectDTO project, final ContributorDTO primaryContri,
	    final Document document) {
		// title page
		try {
			document.add(new Paragraph().setMarginTop(20).setHeight(50).add(new Image(ImageDataFactory.create(DATAWIZ_LOGO)).setAutoScale(true))
			    .setRelativePosition(0, 0, 0, 0));
			document.add(new Paragraph().setHeight(50).add(new Image(ImageDataFactory.create(ZPID_LOGO)).setAutoScale(true))
			    .setTextAlignment(TextAlignment.RIGHT).setRelativePosition(0, 0, 0, 60));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// record title
		document.add(new Paragraph().setMarginTop(20).setTextAlignment(TextAlignment.RIGHT).setRelativePosition(0, 0, 0, 0)
		    .setFontColor(new DeviceRgb(91, 155, 213)).setFontSize(32).setFixedLeading(32).add(record.getRecordName()));
		// study & project title
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setFontSize(16)
		    .add((project != null && project.getTitle() != null && !project.getTitle().isEmpty() ? project.getTitle()
		        : messageSource.getMessage("export.pdf.empty.project.name", null, Locale.ENGLISH)) + " | "
		        + (study != null && study.getTitle() != null && !study.getTitle().isEmpty() ? study.getTitle()
		            : messageSource.getMessage("export.pdf.empty.study.name", null, Locale.ENGLISH))));
		// primary contributor
		StringBuilder primName = setContributorNameString(primaryContri);
		if (primName.toString().isEmpty()) {
			primName.append(messageSource.getMessage("export.pdf.empty.prim.name", null, Locale.ENGLISH));
		}
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20).setFontColor(new DeviceRgb(91, 155, 213))
		    .add(messageSource.getMessage("project.edit.primaryContributors", null, Locale.ENGLISH)));
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).add(primName.toString()));
		// contributors
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20).setFontColor(new DeviceRgb(91, 155, 213))
		    .add(messageSource.getMessage("project.edit.contributors", null, Locale.ENGLISH)));
		if (study != null && study.getContributors() != null) {
			study.getContributors().forEach(contri -> {
				if (!contri.getPrimaryContributor()) {
					StringBuilder prim = setContributorNameString(contri);
					if (!prim.toString().isEmpty()) {
						document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).add(prim.toString()));
					}
				}
			});
		}
		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
		// about page
		document.add(new Paragraph().setFontSize(16).setTextAlignment(TextAlignment.LEFT).setBold()
		    .add(messageSource.getMessage("export.pdf.line.record.about", null, Locale.ENGLISH)));
		document.add(new Paragraph().setFontSize(16).setMarginTop(35).setTextAlignment(TextAlignment.LEFT).setBold()
		    .add(messageSource.getMessage("export.pdf.line.record.content", null, Locale.ENGLISH)));
		Table table = new Table(new float[] { 150.0f, 450.0f });
		table.setBorder(Border.NO_BORDER);
		table.setWidthPercent(100);
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.desc", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.JUSTIFIED).setBorder(Border.NO_BORDER).add(record.getDescription()));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.constructs", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(record.getDescription()));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.instruments", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(record.getDescription()));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.occasions", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(record.getDescription()));
		document.add(table);
		document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
		    .add(messageSource.getMessage("export.pdf.line.created", new Object[] { record.getCreated(), record.getCreatedBy() }, Locale.ENGLISH)));
		document.add(new Paragraph().setTextAlignment(TextAlignment.CENTER)
		    .add(messageSource.getMessage("export.pdf.line.updated", new Object[] { record.getChanged(), record.getChangedBy() }, Locale.ENGLISH)));

		document
		    .add(new Paragraph().add(new Text(messageSource.getMessage("export.pdf.line.last.changes", null, Locale.ENGLISH)).setBold().setUnderline())
		        .add("\n").add(new Text(record.getChangeLog()).setTextAlignment(TextAlignment.JUSTIFIED)));
		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
	}

	private StringBuilder setContributorNameString(final ContributorDTO primaryContri) {
		StringBuilder primName = new StringBuilder();
		if (primaryContri != null) {
			if (primaryContri.getTitle() != null && !primaryContri.getTitle().isEmpty())
				primName.append(primaryContri.getTitle()).append(" ");
			if (primaryContri.getFirstName() != null && !primaryContri.getFirstName().isEmpty())
				primName.append(primaryContri.getFirstName()).append(" ");
			if (primaryContri.getLastName() != null && !primaryContri.getLastName().isEmpty())
				primName.append(primaryContri.getLastName());
			if (primaryContri.getInstitution() != null && !primaryContri.getInstitution().isEmpty())
				primName.append(" (").append(primaryContri.getInstitution()).append(")");
		}
		return primName;
	}

	/**
	 * @param pdf
	 * @param document
	 */
	private void setPagenumber(PdfADocument pdf, Document document) {

		int n = pdf.getNumberOfPages();
		for (int i = 2; i <= n; i++) {
			Paragraph p = new Paragraph();
			p.add(String.format("page %s of %s", i, n));
			p.setFontSize(8);
			document.showTextAligned(p, 559, 35, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
			p = new Paragraph();
			p.setFontSize(8);
			p.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm")));
			document.showTextAligned(p, 35, 35, i, TextAlignment.LEFT, VerticalAlignment.TOP, 0);
		}
	}

	private Table createVarTable(SPSSVarDTO var) {
		Paragraph p;
		DashedBorder dborder = new DashedBorder(0.5f);
		boolean top = true, right = false, bottom = true, left = false;
		Table table = new Table(new float[] { 200.0f, 400.0f });
		table.setWidthPercent(100);
		table
		    .addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.type", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH), dborder, top, right, bottom, left));
		table
		    .addCell(
		        createCell(messageSource.getMessage("dataset.import.report.codebook.label", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(var.getLabel() != null ? var.getLabel() : "", dborder, top, right, bottom, left));
		table.addCell(
		    createCell(messageSource.getMessage("dataset.import.report.codebook.values", null, Locale.ENGLISH), dborder, top, right, bottom, left));
		Paragraph cVarVal = new Paragraph();
		for (SPSSValueLabelDTO varVal : var.getValues()) {
			cVarVal.add("\"" + varVal.getValue() + "\" = " + varVal.getLabel() + "\n");
		}
		table.addCell(createCell(cVarVal, dborder, top, right, bottom, left));
		if (var.getMissingFormat().equals(SPSSMissing.SPSS_NO_MISSVAL) || var.getMissingFormat().equals(SPSSMissing.SPSS_UNKNOWN)) {
			table
			    .addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.missings", null, Locale.ENGLISH), dborder, top, right, bottom,
			        left))
			    .addCell(createCell(messageSource.getMessage("spss.missings." + SPSSMissing.SPSS_NO_MISSVAL, null, Locale.ENGLISH), dborder, top, right,
			        bottom, left));
		} else {
			Cell cell = new Cell(2, 1);
			cell.add(messageSource.getMessage("dataset.import.report.codebook.missings", null, Locale.ENGLISH));
			cell.setBorder(Border.NO_BORDER);
			cell.setBorderBottom(dborder);
			cell.setBorderTop(dborder);
			table.addCell(cell);
			table.addCell(
			    createCell(messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH), dborder, top, right, bottom, left));
			if (var.getMissingFormat().equals(SPSSMissing.SPSS_ONE_MISSVAL))
				table.addCell(createCell(var.getMissingVal1(), dborder, top, right, bottom, left));
			else if (var.getMissingFormat().equals(SPSSMissing.SPSS_TWO_MISSVAL))
				table.addCell(createCell(var.getMissingVal1() + ", " + var.getMissingVal2(), dborder, top, right, bottom, left));
			else if (var.getMissingFormat().equals(SPSSMissing.SPSS_THREE_MISSVAL))
				table
				    .addCell(createCell(var.getMissingVal1() + ", " + var.getMissingVal2() + ", " + var.getMissingVal3(), dborder, top, right, bottom, left));
			else if (var.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGE))
				table.addCell(createCell(var.getMissingVal1() + " - " + var.getMissingVal2(), dborder, top, right, bottom, left));
			else if (var.getMissingFormat().equals(SPSSMissing.SPSS_MISS_RANGEANDVAL))
				table.addCell(
				    createCell(var.getMissingVal1() + " - " + var.getMissingVal2() + ", " + var.getMissingVal3(), dborder, top, right, bottom, left));
			else
				table.addCell("");
		}
		table
		    .addCell(
		        createCell(messageSource.getMessage("dataset.import.report.codebook.width", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(String.valueOf(var.getWidth()), dborder, top, right, bottom, left));
		table.addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.dec", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(String.valueOf(var.getDecimals()), dborder, top, right, bottom, left));
		table
		    .addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.cols", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(String.valueOf(var.getColumns()), dborder, top, right, bottom, left));
		table
		    .addCell(
		        createCell(messageSource.getMessage("dataset.import.report.codebook.aligment", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(messageSource.getMessage("spss.aligment." + var.getAligment(), null, Locale.ENGLISH), dborder, top, right, bottom, left));
		table
		    .addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.measureLevel", null, Locale.ENGLISH), dborder, top, right,
		        bottom, left))
		    .addCell(createCell(messageSource.getMessage("spss.measureLevel." + var.getMeasureLevel(), null, Locale.ENGLISH), dborder, top, right, bottom,
		        left));
		table
		    .addCell(createCell(messageSource.getMessage("dataset.import.report.codebook.role", null, Locale.ENGLISH), dborder, top, right, bottom, left))
		    .addCell(createCell(messageSource.getMessage("spss.role." + var.getRole(), null, Locale.ENGLISH), dborder, top, right, bottom, left));
		if (var.getAttributes() != null && var.getAttributes().size() > 0) {
			table.addCell(
			    createCell(messageSource.getMessage("dataset.import.report.codebook.userAtt", null, Locale.ENGLISH), dborder, top, right, bottom, left));
			p = new Paragraph();
			for (SPSSValueLabelDTO attr : var.getAttributes()) {
				p.add(new Text(attr.getLabel() + " = " + attr.getValue() + "\n"));
			}
			table.addCell(createCell(p, dborder, top, right, bottom, left));
		}
		if (var.getDw_attributes() != null && var.getDw_attributes().size() > 1) {
			for (SPSSValueLabelDTO dwattr : var.getDw_attributes()) {
				table.addCell(createCell(dwattr.getLabel(), dborder, top, right, bottom, left))
				    .addCell(createCell(dwattr.getValue(), dborder, top, right, bottom, left));
			}
		}
		return table;
	}

	/**
	 * @param cell
	 * @return
	 */
	private Cell createCell(Object content, Border border, boolean top, boolean right, boolean bottom, boolean left) {
		Cell cell = new Cell();
		cell.setBorder(Border.NO_BORDER);
		if (!border.equals(Border.NO_BORDER)) {
			if (right)
				cell.setBorderRight(border);
			if (left)
				cell.setBorderLeft(border);
			if (bottom)
				cell.setBorderBottom(border);
			if (top)
				cell.setBorderTop(border);
		}
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
	private PdfFileSpec getRecordCSVAttachment(final RecordDTO record, PdfADocument pdf, StringBuilder res, boolean matrix) {
		log.trace("Entering getDatamatrix for record[id:{}, version:{}]", () -> record.getId(), () -> record.getVersionId());
		PdfFileSpec fileSpec = null;
		String name = record.getRecordName() + "_" + record.getVersionId() + (matrix ? "(matrix).csv" : "(codebook).csv");
		byte[] datamatrix = exportUtil.exportCSV(record, res, matrix);
		if (datamatrix != null && datamatrix.length > 0) {
			PdfDictionary parameters = new PdfDictionary();
			fileSpec = PdfFileSpec.createEmbeddedFileSpec(pdf, datamatrix, name, name, new PdfName("text/csv"), parameters, PdfName.Data, false);
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
	private PdfADocument openPDFADocument(final RecordDTO record, final String dest, final boolean encrypt, final StringBuilder res) {
		log.trace("Entering openPDFADocument for record[id:{}, version:{}], destination[{}], encrytion[{}]", () -> record.getId(),
		    () -> record.getVersionId(), () -> dest, () -> encrypt);
		WriterProperties prop;
		if (encrypt)
			prop = new WriterProperties().setStandardEncryption("".getBytes(), UUID.randomUUID().toString().getBytes(),
			    EncryptionConstants.ALLOW_PRINTING | EncryptionConstants.ALLOW_FILL_IN | EncryptionConstants.ALLOW_ASSEMBLY,
			    EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
		else
			prop = new WriterProperties();
		prop.setCompressionLevel(CompressionConstants.BEST_COMPRESSION);
		prop.setPdfVersion(PdfVersion.PDF_1_7);
		PdfADocument pdf = null;
		try {
			pdf = new PdfADocument(new PdfWriter(dest, prop), PdfAConformanceLevel.PDF_A_3U,
			    new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", this.getClass().getClassLoader().getResourceAsStream(ICC)));
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
		info.setSubject(record.getDescription());
		info.setAuthor(record.getCreatedBy());
		info.setMoreInfo("DataWiz StudyId", String.valueOf(record.getStudyId()));
		info.setMoreInfo("DataWiz RecordId", String.valueOf(record.getId()));
		info.setMoreInfo("DataWiz VersionId", String.valueOf(record.getVersionId()));
	}

	private SummaryStatistics getBasicVariableStatistics(final List<List<Object>> matrix, final int varPosi, final SPSSVarTypes varType) {
		if (RecordDTO.simplifyVarTypes(varType).equals(SPSSVarTypes.SPSS_FMT_F)) {
			SummaryStatistics stats = new SummaryStatistics();
			if (matrix != null) {
				AtomicBoolean statsExp = new AtomicBoolean(true);
				matrix.parallelStream().forEach(col -> {
					if (col != null && statsExp.get()) {
						try {
							String ent = String.valueOf(col.get(varPosi));
							if (ent != null && !ent.equals("null") && !ent.isEmpty())
								stats.addValue(Double.parseDouble(ent));
						} catch (Exception e) {
							statsExp.set(false);
						}
					}
				});
				if (statsExp.get()) {
					return stats;
				}
			}
		}
		return null;
	}
}
