package de.zpid.datawiz.util;

import java.io.FileNotFoundException;
import java.io.IOException;
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
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private Environment env;

	/** A path to a color profile. */
	private final String ICC = "sRGB2014.icc";
	/** A font that will be embedded. */
	private final String FONT = "asap_regular.ttf";
	private final String FONT_BOLD = "asap_bold.ttf";
	private final Border BORDER = Border.NO_BORDER;
	private final float FONTSIZENORMAL = 10.0f;
	private final Color DW_COLOR = new DeviceRgb(91, 155, 213);
	private final Color DW_COLOR_TABLE_BG = new DeviceRgb(240, 240, 240);
	private final Color DW_COLOR_FONT = Color.BLACK;

	/**
	 * 
	 * @param record
	 * @param study
	 * @param project
	 * @param primaryContri
	 * @param encrypt
	 * @param withAttachments
	 * @return
	 * @throws IOException
	 */
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
			// report meta page
			createRecordMetaDesc(record, study, project, primaryContri, document, font_bold);
			// variables
			for (SPSSVarDTO var : record.getVariables()) {
				document.add(new Paragraph().setFontSize(14).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
				    .add(messageSource.getMessage("export.pdf.line.variable", null, Locale.ENGLISH) + var.getName()));
				document.add(createVarTable(var));
				if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
					document.add(new Paragraph().setFontSize(14).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
					    .add(messageSource.getMessage("export.pdf.line.num.stats", null, Locale.ENGLISH)));
					document.add(createStatsTable(record.getDataMatrix(), var));
				}
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
		log.trace("Leaving createPdf");
		return content;
	}

	/**
	 * @param record
	 * @param document
	 */
	private void createRecordMetaDesc(final RecordDTO record, final StudyDTO study, final ProjectDTO project, final ContributorDTO primaryContri,
	    final Document document, final PdfFont font_bold) {
		// title page
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// record title
		document.add(new Paragraph().setMarginTop(20).setTextAlignment(TextAlignment.RIGHT).setRelativePosition(0, 0, 0, 0)
		    .setFontColor(new DeviceRgb(91, 155, 213)).setFontSize(28).setFixedLeading(32).add(record.getRecordName()));
		// study & project title
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setFontSize(14)
		    .add((project != null && project.getTitle() != null && !project.getTitle().isEmpty() ? project.getTitle()
		        : messageSource.getMessage("export.pdf.empty.project.name", null, Locale.ENGLISH)) + " | "
		        + (study != null && study.getTitle() != null && !study.getTitle().isEmpty() ? study.getTitle()
		            : messageSource.getMessage("export.pdf.empty.study.name", null, Locale.ENGLISH))));
		// primary contributor
		StringBuilder primName = setContributorNameString(primaryContri);
		if (primName.toString().isEmpty()) {
			primName.append(messageSource.getMessage("export.pdf.empty.prim.name", null, Locale.ENGLISH));
		}
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).setMarginTop(20).setFontColor(DW_COLOR)
		    .add(messageSource.getMessage("project.edit.primaryContributors", null, Locale.ENGLISH)));
		document.add(new Paragraph().setTextAlignment(TextAlignment.RIGHT).add(primName.toString()));
		// contributors
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
		// about page
		document.add(new Paragraph().setFontSize(14).setMarginTop(35).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
		    .add(messageSource.getMessage("export.pdf.line.record.content", null, Locale.ENGLISH)));
		// Sort used constructs, instruments and occasions
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
		// content table
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
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sConst).toString()));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.instruments", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sInstr).toString()));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.occasions", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(attributeListToStringBuilder(sOcc).toString()));
		document.add(table);
		document.add(new Paragraph().setFontSize(14).setMarginTop(35).setTextAlignment(TextAlignment.LEFT).setFont(font_bold)
		    .add(messageSource.getMessage("export.pdf.line.record.history", null, Locale.ENGLISH)));
		// history table
		table = new Table(new float[] { 150.0f, 450.0f });
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.version", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).add(String.valueOf(record.getVersionId())));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.created", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getCreated() != null ? record.getCreated().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm")) : ""));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.record.last.update", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getChanged() != null ? record.getChanged().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, HH:mm")) : ""));
		table
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(messageSource.getMessage("export.pdf.line.last.changes", null, Locale.ENGLISH)))
		    .addCell(new Cell().setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER)
		        .add(record.getChangeLog() != null ? record.getChangeLog() : ""));
		document.add(table);
		document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

	}

	/**
	 * 
	 * @param var
	 * @return
	 */
	private Table createVarTable(SPSSVarDTO var) {
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
		return table;
	}

	/**
	 * 
	 * @param matrix
	 * @param var
	 * @return
	 */
	private Table createStatsTable(final List<List<Object>> matrix, final SPSSVarDTO var) {
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
		return table;
	}

	/**
	 * 
	 * @param table
	 * @param border
	 * @param hasBackground
	 * @param content
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

	private StringBuilder attributeListToStringBuilder(Set<String> attributes) {
		StringBuilder attrStr = new StringBuilder();
		if (attributes != null) {
			attributes.forEach(s -> attrStr.append(s).append("\n"));
		}
		return attrStr;
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
	}

	private boolean checkIfValIsMissing(final String entry, final SPSSMissing misstype, final String miss1, final String miss2, final String miss3) {
		boolean isMissing = false;
		try {
			Double val, min, max;
			switch (misstype) {
			case SPSS_ONE_MISSVAL:
				isMissing = miss1.trim().equals(entry.trim());
				break;
			case SPSS_TWO_MISSVAL:
				isMissing = (miss1.trim().equals(entry.trim()) || miss2.trim().equals(entry.trim()));
				break;
			case SPSS_THREE_MISSVAL:
				isMissing = (miss1.trim().equals(entry.trim()) || miss2.trim().equals(entry.trim()) || miss3.trim().equals(entry.trim()));
				break;
			case SPSS_MISS_RANGE:
				val = Double.parseDouble(entry.trim());
				min = Double.parseDouble(miss1.trim());
				max = Double.parseDouble(miss2.trim());
				if (val >= min && val <= max)
					isMissing = true;
				break;
			case SPSS_MISS_RANGEANDVAL:
				isMissing = miss3.trim().equals(entry.trim());
				val = Double.parseDouble(entry.trim());
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
		return isMissing;
	}
}
