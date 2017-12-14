package com.frederic.gan.report;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.frederic.gan.dao.OrderedPizzaRepository;
import com.frederic.gan.entities.OrderEntity;
import com.frederic.gan.entities.Status;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.core.layout.ListLayoutManager;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJCrosstab;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.CrosstabBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.util.SortUtils;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class StatusReports {

	private Style totalHeaderStyle;
	private Style colAndRowHeaderStyle;
	private Style mainHeaderStyle;
	private Style totalStyle;
	private Style measureStyle;
	private Style titleStyle;
	public JasperPrint jp;
	public JasperPrint jpForXls;

	public JasperReport jr;
	protected Map<String, Object> params = new HashMap<>();
	protected DynamicReport dr;

	@Autowired
	private OrderedPizzaRepository orderedPizzaRepository;

	public DynamicReport buildReport() throws Exception {

		/**
		 * Create an empty report (no columns)!
		 */
		FastReportBuilder drb = new FastReportBuilder();
		drb.setTitle("Etat des commandes ").setSubtitle("This report was generated at " + new Date())
				.setPageSizeAndOrientation(Page.Page_A4_Landscape())
				.setPrintColumnNames(false).setUseFullPageWidth(true).setWhenNoData("No data for this report", null)
				.setDefaultStyles(titleStyle, null, null, null);

		DJCrosstab djcross = new CrosstabBuilder().setHeight(200).setWidth(500).setHeaderStyle(mainHeaderStyle)
				.setDatasource("sr", DJConstants.DATA_SOURCE_ORIGIN_PARAMETER, DJConstants.DATA_SOURCE_TYPE_COLLECTION)
				.setUseFullWidth(true).setColorScheme(DJConstants.COLOR_SCHEMA_LIGHT_GREEN).setAutomaticTitle(true)
				.setCellBorder(Border.THIN()).addColumn("Customer", "customer", String.class.getName(), false)
				.addRow("Pizza", "pizza", String.class.getName(), false)
				.addMeasure("status", Status.class.getName(), DJCalculation.NOTHING, "Status", measureStyle)
				.setRowStyles(colAndRowHeaderStyle, totalStyle, totalHeaderStyle)
				.setColumnStyles(colAndRowHeaderStyle, totalStyle, totalHeaderStyle).setCellDimension(17, 80)
				.setColumnHeaderHeight(30).setRowHeaderWidth(100).build();

		drb.addHeaderCrosstab(djcross); // add the crosstab in the header band
		// of the report
		final Collection<OrderEntity> ordersEntity = new ArrayList<>();
		orderedPizzaRepository.findAll().forEach(ordersEntity::add);
		// put a collection in the parameters map to be used by the crosstab
		params.put("sr", SortUtils.sortCollection(ordersEntity, djcross));
		return drb.build();
	}

	@PostConstruct
	private void initStyles() {
		titleStyle = new StyleBuilder(false).setFont(Font.ARIAL_BIG_BOLD).setHorizontalAlign(HorizontalAlign.LEFT)
				.setVerticalAlign(VerticalAlign.MIDDLE).setTransparency(Transparency.OPAQUE)
				.setBorderBottom(Border.PEN_2_POINT()).build();

		totalHeaderStyle = new StyleBuilder(false).setHorizontalAlign(HorizontalAlign.CENTER)
				.setVerticalAlign(VerticalAlign.MIDDLE).setFont(Font.ARIAL_MEDIUM_BOLD).setTextColor(Color.BLUE)
				.build();
		colAndRowHeaderStyle = new StyleBuilder(false).setHorizontalAlign(HorizontalAlign.LEFT)
				.setVerticalAlign(VerticalAlign.TOP).setFont(Font.ARIAL_MEDIUM_BOLD).build();
		mainHeaderStyle = new StyleBuilder(false).setHorizontalAlign(HorizontalAlign.CENTER)
				.setVerticalAlign(VerticalAlign.MIDDLE).setFont(Font.ARIAL_BIG_BOLD).setTextColor(Color.BLACK).build();
		totalStyle = new StyleBuilder(false).setPattern("#,###.##").setHorizontalAlign(HorizontalAlign.RIGHT)
				.setFont(Font.ARIAL_MEDIUM_BOLD).build();
		measureStyle = new StyleBuilder(false).setPattern("#,###.##").setHorizontalAlign(HorizontalAlign.RIGHT)
				.setFont(Font.ARIAL_MEDIUM).build();
	}

	public void testReport() throws Exception {
		dr = buildReport();

		/**
		 * Get a JRDataSource implementation
		 */

		JRDataSource ds = new JRBeanCollectionDataSource(
				SortUtils.sortCollection(((Collection<?>) params.get("sr")), dr.getColumns()));

		/**
		 * Creates the JasperReport object, we pass as a Parameter the
		 * DynamicReport, a new ClassicLayoutManager instance (this one does the
		 * magic) and the JRDataSource
		 */
		jr = DynamicJasperHelper.generateJasperReport(dr, getLayoutManager(), params);
		JasperReport jrForXls = DynamicJasperHelper.generateJasperReport(dr, new ListLayoutManager(), params);
		/**
		 * Creates the JasperPrint object, we pass as a Parameter the
		 * JasperReport object, and the JRDataSource
		 */
		// log.debug("Filling the report");
		if (ds != null) {
			jp = JasperFillManager.fillReport(jr, params, ds);
			jpForXls = JasperFillManager.fillReport(jrForXls, params, ds);
		} else {
			jp = JasperFillManager.fillReport(jr, params);
		}

		// log.debug("Filling done!");
		// log.debug("Exporting the report (pdf, xls, etc)");
		exportReport();

		// log.debug("test finished");

	}

	protected LayoutManager getLayoutManager() {
		return new ClassicLayoutManager();
	}

	protected void exportReport() throws Exception {
		ReportExporter.exportReport(jp,
				System.getProperty("user.dir") + "/target/reports/" + this.getClass().getName() + ".pdf");
		exportToJRXML();
		
		ReportExporter.exportReportXls(jp,
				System.getProperty("user.dir") + "/target/reports/" + this.getClass().getName() + ".xls");
	}

	protected void exportToJRXML() throws Exception {
		if (this.jr != null) {
			DynamicJasperHelper.generateJRXML(this.jr, "UTF-8",
					System.getProperty("user.dir") + "/target/reports/" + this.getClass().getName() + ".jrxml");

		} else {
			DynamicJasperHelper.generateJRXML(this.dr, this.getLayoutManager(), this.params, "UTF-8",
					System.getProperty("user.dir") + "/target/reports/" + this.getClass().getName() + ".jrxml");
		}
	}

	protected void exportToHTML() throws Exception {
		ReportExporter.exportReportHtml(this.jp,
				System.getProperty("user.dir") + "/target/reports/" + this.getClass().getName() + ".html");
	}

	// /**
	// * @return JRDataSource
	// */
	// protected JRDataSource getDataSource() {
	// Collection dummyCollection = TestRepositoryProducts.getDummyCollection();
	// dummyCollection = SortUtils.sortCollection(dummyCollection,
	// dr.getColumns());
	//
	// //here contains dummy hardcoded objects...
	// return new JRBeanCollectionDataSource(dummyCollection);
	// }
}
