package de.zpid.datawiz.util;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.pdfa.PdfADocument;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.service.ExportService;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSVarTypes;

/**
 * In this class all functions for creating a PDF document with the iText7 library are available <br />
 * <br />
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 *          TODO Descriptive statistics for date and string variables
 */
@Component
public class ITextUtil {

	private static Logger log = LogManager.getLogger(ITextUtil.class);

	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ExportService exportUtil;
	@Autowired
	protected MessageSource messageSource;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private Environment env;

	/** path to a color profile. */
	private final String ICC = "sRGB2014.icc";
	/** fonts that will be embedded. */
	private final String FONT = "asap_regular.ttf";
	private final String FONT_BOLD = "asap_bold.ttf";

	private final Border BORDER = Border.NO_BORDER;
	private final float FONTSIZENORMAL = 10.0f;
	private final Color DW_COLOR = new DeviceRgb(91, 155, 213);
	private final Color DW_COLOR_TABLE_BG = new DeviceRgb(240, 240, 240);
	private final Color DW_COLOR_FONT = Color.BLACK;

	/**
	 * This function handles the creation of the entire record codebook as a PDF-A document and returns it as byte array.
	 * 
	 * @param record
	 *          Contains the record meta data
	 * @param study
	 *          Contains the study meta data
	 * @param project
	 *          Contains the project meta data
	 * @param primaryContri
	 *          Contains the primary contributor
	 * @param encrypt
	 *          True if the PDF-A should be encrypted, otherwise false.
	 * @param withAttachments
	 *          True, if the codebook and the matrix should be attached to the PDF-A file, otherwise false
	 * @return The PDF-A document as byte array
	 * @throws Exception
	 */
	public byte[] createRecordCodeBookPDFA(final RecordDTO record, final StudyDTO study, final ProjectDTO project, final ContributorDTO primaryContri,
	    final boolean encrypt, final boolean withAttachments) throws Exception {
		log.trace("Entering createRecordCodeBookPDFA for record[id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
		StringBuilder res = new StringBuilder();
		byte[] content = null;
		String dir = fileUtil.setFolderPath("temp");
		Files.createDirectories(Paths.get(dir));
		String filename = UUID.randomUUID().toString() + ".pdf";
		// open PDF-A document
		PdfADocument pdf = openPDFADocument(record, dir + filename, encrypt);
		if (pdf != null) {
			Document document = new Document(pdf, PageSize.A4);
			// add datamatrix as csv
			byte[] fontBytes = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(FONT));
			PdfFont font_bold = PdfFontFactory.createFont(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(FONT_BOLD)), true);
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
			// cover sheet
			createRecordCoverSheet(record, study, project, primaryContri, document);
			// record descrition
			createRecordDescription(record, document, font_bold);
			// variables codebook & statistic
			for (SPSSVarDTO var : record.getVariables()) {
				document.add(new Paragraph().setFontSize(14).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
				    .add(messageSource.getMessage("export.pdf.line.variable", null, Locale.ENGLISH) + var.getName()));
				document.add(createVariableCodeBookTable(var));
				if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
					document.add(new Paragraph().setFontSize(14).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
					    .add(messageSource.getMessage("export.pdf.line.num.stats", null, Locale.ENGLISH)));
					document.add(createVariableDescriptiveStatistikTable(record.getDataMatrix(), var));
				}
				document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
			}
			// set footer
			setPagefooter(pdf, document);
			// close document
			document.close();
			// create content as byte [] and delete temporary PDF file
			if (Files.exists(Paths.get(dir + filename))) {
				content = Files.readAllBytes(Paths.get(dir + filename));
				fileUtil.deleteFile(Paths.get(dir + filename));
			}
		} else {
			throw new DataWizSystemException("PDF-A was not created", DataWizErrorCodes.NO_DATA_ERROR);
		}
		log.trace("Leaving createRecordCodeBookPDFA for record[id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
		return content;
	}

	/**
	 * This function creates the PDF cover sheet
	 * 
	 * @param record
	 *          Contains the record meta data
	 * @param study
	 *          Contains the study meta data
	 * @param project
	 *          Contains the project meta data
	 * @param primaryContri
	 *          Contains the primary contributor
	 * @param document
	 *          The document in which the cover sheet is inserted
	 */
	private void createRecordCoverSheet(final RecordDTO record, final StudyDTO study, final ProjectDTO project, final ContributorDTO primaryContri,
	    final Document document) {
		log.trace("Entering createRecordCoverSheet for record[id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
		try {
			document.add(new Paragraph().setMarginTop(20).setHeight(50)
			    .add(new Image(ImageDataFactory.create(request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath())
			        + env.getRequiredProperty("application.logo.url"))).setAutoScale(true))
			    .setRelativePosition(0, 0, 0, 0));
			document.add(new Paragraph().setHeight(50)
			    .add(new Image(ImageDataFactory.create(request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath())
			        + env.getRequiredProperty("application.logo.zpid"))).setAutoScale(true))
			    .setTextAlignment(TextAlignment.RIGHT).setRelativePosition(0, 0, 0, 60));
		} catch (MalformedURLException e) {
			log.warn("WARN: createRecordCoverSheet error during loading cover sheet images - PDF was created without images! Exception: ", () -> e);
		}
		document.add(new Paragraph().setMarginTop(20).setTextAlignment(TextAlignment.RIGHT).setRelativePosition(0, 0, 0, 0)
		    .setFontColor(new DeviceRgb(91, 155, 213)).setFontSize(28).setFixedLeading(32).add(record.getRecordName()));
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setFontSize(14)
		    .add((project != null && project.getTitle() != null && !project.getTitle().isEmpty() ? project.getTitle()
		        : messageSource.getMessage("export.pdf.empty.project.name", null, Locale.ENGLISH)) + " | "
		        + (study != null && study.getTitle() != null && !study.getTitle().isEmpty() ? study.getTitle()
		            : messageSource.getMessage("export.pdf.empty.study.name", null, Locale.ENGLISH))));
		StringBuilder primName = setContributorNameString(primaryContri);
		if (primName.toString().isEmpty()) {
			primName.append(messageSource.getMessage("export.pdf.empty.prim.name", null, Locale.ENGLISH));
		}
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20).setFontColor(DW_COLOR)
		    .add(messageSource.getMessage("project.edit.primaryContributors", null, Locale.ENGLISH)));
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).add(primName.toString()));
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20).setFontColor(DW_COLOR)
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
		log.trace("Leaving createRecordCoverSheet for record[id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
	}

	/**
	 * The function creates the record description based on the metadata passed to it.
	 * 
	 * @param record
	 *          Contains the record meta data
	 * @param document
	 *          The document in which the description is inserted
	 * @param font_bold
	 *          Bold font for creating headlines.
	 */
	private void createRecordDescription(final RecordDTO record, final Document document, final PdfFont font_bold) {
		log.trace("Entering createRecordDescription for record[id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
		document.add(new Paragraph().setFontSize(14).setMarginTop(35).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
		    .add(messageSource.getMessage("export.pdf.line.record.content", null, Locale.ENGLISH)));
		Set<String> sConst = new HashSet<>();
		Set<String> sInstr = new HashSet<>();
		Set<String> sOcc = new HashSet<>();
		if (record != null && record.getVariables() != null) {
			record.getVariables().parallelStream().forEach(var -> {
				if (var.getDw_attributes() != null)
					var.getDw_attributes().forEach(attr -> {
						if (attr.getValue() != null && !attr.getValue().trim().isEmpty())
							switch (attr.getLabel()) {
							case "dw_construct":
								sConst.add(attr.getValue());
								break;
							case "dw_measocc":
								sOcc.add(attr.getValue());
								break;
							case "dw_instrument":
								sInstr.add(attr.getValue());
								break;
							}
					});
			});
		}
		final Table desTable = new Table(new float[] { 150.0f, 450.0f });
		desTable.setBorder(Border.NO_BORDER);
		desTable.setWidthPercent(100);
		desTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.desc", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.JUSTIFIED).setBorder(Border.NO_BORDER).add(record.getDescription()));
		desTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.constructs", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sConst).toString()));
		desTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.instruments", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sInstr).toString()));
		desTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.occasions", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sOcc).toString()));
		document.add(desTable);
		document.add(new Paragraph().setFontSize(14).setMarginTop(35).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
		    .add(messageSource.getMessage("export.pdf.line.record.history", null, Locale.ENGLISH)));
		final Table historyTable = new Table(new float[] { 150.0f, 450.0f });
		historyTable.setBorder(Border.NO_BORDER);
		historyTable.setWidthPercent(100);
		historyTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.version", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(String.valueOf(record.getVersionId())));
		historyTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.created", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getCreated() != null ? record.getCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm")) : ""));
		historyTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.last.update", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getChanged() != null ? record.getChanged().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm")) : ""));
		historyTable
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.last.changes", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getChangeLog() != null ? record.getChangeLog() : ""));
		document.add(historyTable);
		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
		log.trace("Leaving createRecordDescription for record[id: {}; version: {}] with Number of Rows[Description-Table: {}; HistoryTable: {}]",
		    () -> record.getId(), () -> record.getVersionId(), () -> desTable.getNumberOfRows(), () -> historyTable.getNumberOfRows());
	}

	/**
	 * This function creates the codebook table for the numerical variable passed.
	 * 
	 * @param var
	 *          Variable for which the table has to be created
	 * @return Table with the provided codebook content
	 */
	private Table createVariableCodeBookTable(final SPSSVarDTO var) {
		log.trace("Entering createVariableCodeBookTable for variable[id:{}]", () -> var.getId());
		Table table = new Table(new float[] { 200.0f, 400.0f });
		table.setWidthPercent(100);
		String construct = "", measocc = "", instrument = "", itemtext = "", filtervar = "";
		if (var.getDw_attributes() != null) {
			for (SPSSValueLabelDTO attr : var.getDw_attributes()) {
				switch (attr.getLabel()) {
				case "dw_construct":
					construct = attr.getValue();
					break;
				case "dw_measocc":
					measocc = attr.getValue();
					break;
				case "dw_instrument":
					instrument = attr.getValue();
					break;
				case "dw_itemtext":
					itemtext = attr.getValue();
					break;
				case "dw_filtervar":
					filtervar = attr.getValue();
					break;
				}
			}
		}
		createAndAddRow(table, true, messageSource.getMessage("dataset.import.report.codebook.label", null, Locale.ENGLISH) + ":",
		    var.getLabel() != null ? var.getLabel() : "");
		createAndAddRow(table, false, messageSource.getMessage("dataset.import.report.codebook.itemtext", null, Locale.ENGLISH) + ":", itemtext);
		createAndAddRow(table, true, messageSource.getMessage("dataset.import.report.codebook.position", null, Locale.ENGLISH) + ":",
		    String.valueOf(var.getPosition()));
		createAndAddRow(table, false, messageSource.getMessage("dataset.import.report.codebook.type", null, Locale.ENGLISH) + ":",
		    messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH));
		createAndAddRow(table, false, "\n", "");
		createAndAddRow(table, true, messageSource.getMessage("dataset.import.report.codebook.construct", null, Locale.ENGLISH) + ":", construct);
		createAndAddRow(table, false, messageSource.getMessage("dataset.import.report.codebook.measocc", null, Locale.ENGLISH) + ":", measocc);
		createAndAddRow(table, true, messageSource.getMessage("dataset.import.report.codebook.instrument", null, Locale.ENGLISH) + ":", instrument);
		createAndAddRow(table, false, messageSource.getMessage("dataset.import.report.codebook.filtervar", null, Locale.ENGLISH) + ":",
		    filtervar.equals("1") ? messageSource.getMessage("export.boolean.true", null, Locale.ENGLISH)
		        : messageSource.getMessage("export.boolean.false", null, Locale.ENGLISH));
		createAndAddRow(table, false, "\n", "");
		table.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER).setBackgroundColor(DW_COLOR_TABLE_BG)
		    .setFontColor(DW_COLOR_FONT).add(messageSource.getMessage("dataset.import.report.codebook.values", null, Locale.ENGLISH)));
		Table varTable = new Table(new float[] { 200.0f, 250.0f });
		for (SPSSValueLabelDTO varVal : var.getValues()) {
			createAndAddRow(varTable, true, varVal.getValue(), varVal.getLabel());
		}
		table.addCell(new Cell().setBorder(this.BORDER).setBackgroundColor(DW_COLOR_TABLE_BG).add(varTable));
		table.addCell(new Cell(2, 1).setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER)
		    .add(messageSource.getMessage("dataset.import.report.codebook.missings", null, Locale.ENGLISH)));
		table.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER)
		    .add(messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH)));
		StringBuilder miss = new StringBuilder();
		switch (var.getMissingFormat()) {
		case SPSS_ONE_MISSVAL:
			miss.append(var.getMissingVal1());
			break;
		case SPSS_TWO_MISSVAL:
			miss.append(var.getMissingVal1()).append("\n").append(var.getMissingVal2());
			break;
		case SPSS_THREE_MISSVAL:
			miss.append(var.getMissingVal1()).append("\n").append(var.getMissingVal2()).append("\n").append(var.getMissingVal3());
			break;
		case SPSS_MISS_RANGE:
			miss.append(var.getMissingVal1()).append(" - ").append(var.getMissingVal2());
			break;
		case SPSS_MISS_RANGEANDVAL:
			miss.append(var.getMissingVal1()).append(" - ").append(var.getMissingVal2()).append(", ").append(var.getMissingVal3());
			break;
		default:
			break;
		}
		table.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER).add(miss.toString()));
		log.trace("Leaving createVariableCodeBookTable for variable[id:{}] with Number of Rows[numOfRows: {}]", () -> var.getId(),
		    () -> table.getNumberOfRows());
		return table;
	}

	/**
	 * This function creates a table with descriptive statistics and frequency distribution for the numerical variable passed.
	 * 
	 * @param matrix
	 *          Entire data matrix of the record.
	 * @param var
	 *          Variable for which the table has to be created
	 * @return If no error occurs during data extraction and statistics calculation, the table with the calculated content is returned. Otherwise an
	 *         empty table.
	 * 
	 */
	private Table createVariableDescriptiveStatistikTable(final List<List<Object>> matrix, final SPSSVarDTO var) {
		log.trace("Entering createVariableDescriptiveStatistikTable for variable[id:{}]", () -> var.getId());
		Table table = new Table(new float[] { 200.0f, 400.0f });
		SynchronizedSummaryStatistics stats = new SynchronizedSummaryStatistics();
		Frequency freq = new Frequency();
		AtomicInteger countWithoutMissings = new AtomicInteger(0);
		AtomicInteger fullCount = new AtomicInteger(0);
		Set<String> uniqueSet = new HashSet<>();
		if (matrix != null) {
			AtomicBoolean statsExp = new AtomicBoolean(true);
			matrix.parallelStream().forEach(col -> {
				if (col != null && statsExp.get()) {
					try {
						String ent = String.valueOf(col.get(var.getPosition() - 1));
						if (ent != null && !ent.equals("null") && !ent.isEmpty()) {
							if (!checkIfValIsMissing(ent, var.getMissingFormat(), var.getMissingVal1(), var.getMissingVal2(), var.getMissingVal3())) {
								stats.addValue(Double.parseDouble(ent));
								uniqueSet.add(ent.trim());
								countWithoutMissings.incrementAndGet();
							}
							freq.addValue(String.valueOf(ent).trim());
							fullCount.incrementAndGet();
						}
					} catch (Exception e) {
						statsExp.set(false);
					}
				}
			});
			if (statsExp.get()) {
				Map<SPSSValueLabelDTO, Long> valueCount = new LinkedHashMap<>();
				if (var.getValues() != null) {
					var.getValues().forEach(val -> {
						long count = freq.getCount(val.getValue().trim());
						valueCount.put(val, count);
						uniqueSet.remove(val.getValue());
					});
				}
				AtomicBoolean bg = new AtomicBoolean(true);
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.valid", null, Locale.ENGLISH), "");
				valueCount.forEach((k, v) -> {
					if (!checkIfValIsMissing(k.getValue().trim(), var.getMissingFormat(), var.getMissingVal1(), var.getMissingVal2(), var.getMissingVal3()))
						createAndAddRow(table, bg.getAndSet(!bg.get()), k.getLabel(), String.valueOf(v));
				});
				uniqueSet.forEach(key -> {
					createAndAddRow(table, bg.getAndSet(!bg.get()), key, String.valueOf(freq.getCount(key.trim())));
				});
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.valid.sum", null, Locale.ENGLISH),
				    String.valueOf(countWithoutMissings.get()));
				createAndAddRow(table, bg.getAndSet(!bg.get()), "\n", "");
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.invalid", null, Locale.ENGLISH), "");
				switch (var.getMissingFormat()) {
				case SPSS_ONE_MISSVAL:
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal1(), String.valueOf(freq.getCount(var.getMissingVal1().trim())));
					break;
				case SPSS_TWO_MISSVAL:
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal1(), String.valueOf(freq.getCount(var.getMissingVal1().trim())));
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal2(), String.valueOf(freq.getCount(var.getMissingVal2().trim())));
					break;
				case SPSS_THREE_MISSVAL:
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal1(), String.valueOf(freq.getCount(var.getMissingVal1().trim())));
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal2(), String.valueOf(freq.getCount(var.getMissingVal2().trim())));
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal3(), String.valueOf(freq.getCount(var.getMissingVal3().trim())));
					break;
				case SPSS_MISS_RANGE:
					createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.missing.range", null, Locale.ENGLISH) + " "
					    + var.getMissingVal1() + " - " + var.getMissingVal2(), String.valueOf(freq.getCount(var.getMissingVal2().trim())));
					break;
				case SPSS_MISS_RANGEANDVAL:
					DoubleRange dr = new DoubleRange(Double.valueOf(var.getMissingVal1().trim()), Double.valueOf(var.getMissingVal2().trim()));
					Iterator<Comparable<?>> vIter = freq.valuesIterator();
					long missCount = 0;
					while (vIter.hasNext()) {
						Comparable<?> value = vIter.next();
						if (dr.containsDouble(Double.valueOf(value.toString().trim()))) {
							missCount += freq.getCount(value);
						}
					}
					createAndAddRow(table, bg.getAndSet(!bg.get()), var.getMissingVal3(), String.valueOf(freq.getCount(var.getMissingVal3().trim())));
					createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.missing.range", null, Locale.ENGLISH) + " "
					    + var.getMissingVal1() + " - " + var.getMissingVal2(), String.valueOf(String.valueOf(missCount)));
					break;
				default:
					break;
				}
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.blank", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(matrix.size() - fullCount.get())));
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.invalid.sum", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(matrix.size() - countWithoutMissings.get())));
				createAndAddRow(table, bg.getAndSet(!bg.get()), "\n", "");
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.mean", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(stats.getMean())));
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.sd", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(stats.getStandardDeviation())));
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.min", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(stats.getMin())));
				createAndAddRow(table, bg.getAndSet(!bg.get()), messageSource.getMessage("export.pdf.line.max", null, Locale.ENGLISH),
				    String.valueOf(String.valueOf(stats.getMax())));
				stats.clear();
				freq.clear();
			}
		}
		log.trace("Leaving createVariableDescriptiveStatistikTable for variable[id:{}] with Number of Rows[numOfRows: {}]", () -> var.getId(),
		    () -> table.getNumberOfRows());
		return table;
	}

	/**
	 * This function adds a new row with a variable number of columns to the table. The amount of columns in a row depends on the amount of varargs.
	 * 
	 * @param table
	 *          Table object to which the row has to be appended
	 * @param hasBackground
	 *          Boolean, to create striped Tables
	 * @param content
	 *          Varargs for the cell content.
	 * 
	 */
	private void createAndAddRow(final Table table, final boolean hasBackground, final String... content) {
		for (String s : content)
			if (hasBackground) {
				table.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER).setBackgroundColor(DW_COLOR_TABLE_BG)
				    .setFontColor(DW_COLOR_FONT).add(s));
			} else {
				table.addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(this.BORDER).add(s));
			}
	}

	/**
	 * This function set the CSV attachments to a PdfFileSpec object, if the user selected PDF-A with attachments.
	 * 
	 * @param record
	 *          Record object which includes the record data.
	 * @param pdf
	 *          Current PDF-document
	 * @param res
	 *          StringBuilder object to save errors that may occur, and return them to the calling function for exception handling.
	 * @param matrix
	 *          True if the data matrix, false if the codebook should be saved into the PDF-A.
	 * @return A PdfFileSpec which includes the attachment
	 */
	private PdfFileSpec getRecordCSVAttachment(final RecordDTO record, final PdfADocument pdf, final StringBuilder res, final boolean matrix) {
		log.trace("Entering getRecordCSVAttachment for record[id:{}, version:{}]", () -> record.getId(), () -> record.getVersionId());
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
		log.trace("Leaving getRecordCSVAttachment with result: {}", fileSpec == null ? "NULL" : "SUCCESS");
		return fileSpec;
	}

	/**
	 * This function opens a new PDF document and it returns it. It uses a destination path for temporary saving the file to the file system and an
	 * boolean to turn on or off the PDF encryption. CompressionConstants.BEST_COMPRESSION is selected as compression level and the PDF version is set
	 * to 1.7, which is the latest version at the time of development. For the creation of a PDF-A document it is necessary that all used contents, such
	 * as images, fonts and the used color profile, are embedded in the PDF-A, because otherwise it is not possible to create a valid PDF-A file.
	 * Therefore, this function saves the ICC profile into the PDF-A document.
	 * 
	 * @param record
	 *          Record object which includes the record data.
	 * @param dest
	 *          Path to (temporary) file storage
	 * @param encrypt
	 *          True if the PDF-A should be encrypted, otherwise false.
	 * @param res
	 *          StringBuilder object to save errors that may occur, and return them to the calling function for exception handling.
	 * @return The created PdfADocument
	 */
	private PdfADocument openPDFADocument(final RecordDTO record, final String dest, final boolean encrypt) {
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
			pdf = null;
			log.error("IOException during reading PDF/A Color: {}", () -> e);
		}
		if (pdf != null) {
			pdf.setTagged();
			pdf.getCatalog().setLang(new PdfString("en-US"));
			pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
			setMetaInformation(record, pdf);
		}
		log.trace("Leaving openPDFADocument with result: {}", pdf == null ? "NULL" : "SUCCESS");
		return pdf;
	}

	/**
	 * This function writes meta informations into the PDF Document, including record name, record description, record author, DataWiz as record creator
	 * and the identifier of the record, study and version
	 * 
	 * @param record
	 *          Record object which includes the meta informations.
	 * @param pdf
	 *          Current PDF-document
	 */
	private void setMetaInformation(final RecordDTO record, final PdfADocument pdf) {
		log.trace("Entering setMetaInformation for record [id: {}; version: {}]", () -> record.getId(), () -> record.getVersionId());
		PdfDocumentInfo info = pdf.getDocumentInfo();
		info.setTitle(record.getRecordName());
		info.setCreator("Datawiz (www.datawiz.de)");
		info.setSubject(record.getDescription());
		info.setAuthor(record.getCreatedBy());
		info.setMoreInfo("DataWiz StudyId", String.valueOf(record.getStudyId()));
		info.setMoreInfo("DataWiz RecordId", String.valueOf(record.getId()));
		info.setMoreInfo("DataWiz VersionId", String.valueOf(record.getVersionId()));
		log.trace("Leaving setMetaInformation");
	}

	/**
	 * This function composes all attributes, which are included in the passed List, to a StringBuilder object.
	 * 
	 * @param attributes
	 *          List of attributes
	 * @return StingBuilder object with attributes
	 */
	private StringBuilder attributeListToStringBuilder(final Set<String> attributes) {
		log.trace("Entering attributeListToStringBuilder");
		StringBuilder attrStr = new StringBuilder();
		if (attributes != null) {
			attributes.forEach(s -> attrStr.append(s).append("\n"));
		}
		log.trace("Leaving attributeListToStringBuilder");
		return attrStr;
	}

	/**
	 * This function composes the full contributor name, consisting of title, first and last name.
	 * 
	 * @param contributor
	 *          The Contributor Object
	 * @return The Contributor name as StringBuilder object
	 */
	private StringBuilder setContributorNameString(final ContributorDTO contributor) {
		log.trace("Entering setContributorNameString for Contributor [title: {}; firstName: {}; lastName: {}]", () -> contributor.getTitle(),
		    () -> contributor.getFirstName(), () -> contributor.getLastName());
		StringBuilder primName = new StringBuilder();
		if (contributor != null) {
			if (contributor.getTitle() != null && !contributor.getTitle().isEmpty())
				primName.append(contributor.getTitle()).append(" ");
			if (contributor.getFirstName() != null && !contributor.getFirstName().isEmpty())
				primName.append(contributor.getFirstName()).append(" ");
			if (contributor.getLastName() != null && !contributor.getLastName().isEmpty())
				primName.append(contributor.getLastName());
			if (contributor.getInstitution() != null && !contributor.getInstitution().isEmpty())
				primName.append(" (").append(contributor.getInstitution()).append(")");
		}
		log.trace("Leaving setContributorNameString with result: [name: {}]", () -> primName.toString());
		return primName;
	}

	/**
	 * This function sets the PDF footer. It includes a current time-stamp and the mail address of the person who exported the document on the left
	 * side. And on the right side the current and maximum number of pages.
	 * 
	 * @param pdf
	 *          The current PDF-A document
	 * @param document
	 *          The current PDF-A document
	 */
	private void setPagefooter(final PdfADocument pdf, final Document document) {
		log.trace("Entering setPagefooter");
		UserDTO user = UserUtil.getCurrentUser();
		String currTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm"));
		int n = pdf.getNumberOfPages();
		for (int i = 2; i <= n; i++) {
			Paragraph p = new Paragraph();
			p.add(messageSource.getMessage("export.pdf.line.page.num", new Object[] { i, n }, Locale.ENGLISH));
			p.setFontSize(8);
			document.showTextAligned(p, 559, 30, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
			p = new Paragraph();
			p.setFontSize(8);
			p.add(messageSource.getMessage("export.pdf.line.doc.create", new Object[] { currTime, user.getEmail() }, Locale.ENGLISH));
			document.showTextAligned(p, 30, 30, i, TextAlignment.LEFT, VerticalAlignment.TOP, 0);
		}
		log.trace("Leaving setPagefooter");
	}

	/**
	 * This functions checks if the passed value is a discrete missing value, or whether the value is in the range of the missing values.
	 * 
	 * @param value
	 *          The value which has to be checked.
	 * @param misstype
	 *          The type of the missing format.
	 * @param miss1
	 *          Missing value one.
	 * @param miss2
	 *          Missing value two.
	 * @param miss3
	 *          Missing value three.
	 * @return True, if the value is a missing value, otherwise false
	 */
	private boolean checkIfValIsMissing(final String value, final SPSSMissing misstype, final String miss1, final String miss2, final String miss3) {
		boolean isMissing = false;
		log.trace("Entering checkIfValIsMissing for [value: {}; misstype: {}; miss1: {}; miss2: {}; miss3: {}]", () -> value, () -> misstype, () -> miss1,
		    () -> miss2, () -> miss3);
		try {
			Double val, min, max;
			switch (misstype) {
			case SPSS_ONE_MISSVAL:
				isMissing = miss1.trim().equals(value.trim());
				break;
			case SPSS_TWO_MISSVAL:
				isMissing = (miss1.trim().equals(value.trim()) || miss2.trim().equals(value.trim()));
				break;
			case SPSS_THREE_MISSVAL:
				isMissing = (miss1.trim().equals(value.trim()) || miss2.trim().equals(value.trim()) || miss3.trim().equals(value.trim()));
				break;
			case SPSS_MISS_RANGE:
				val = Double.parseDouble(value.trim());
				min = Double.parseDouble(miss1.trim());
				max = Double.parseDouble(miss2.trim());
				if (val >= min && val <= max)
					isMissing = true;
				break;
			case SPSS_MISS_RANGEANDVAL:
				isMissing = miss3.trim().equals(value.trim());
				val = Double.parseDouble(value.trim());
				min = Double.parseDouble(miss1.trim());
				max = Double.parseDouble(miss2.trim());
				if (val >= min && val <= max)
					isMissing = true;
				break;
			default:
				break;
			}
		} catch (Exception e) {

		}
		log.trace("Leaving checkIfValIsMissing with result: [ismissing: {}]", isMissing);
		return isMissing;
	}
}
