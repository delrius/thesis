package readers;

import com.cybozu.labs.langdetect.LangDetectException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import pdf.parser.TextBlock;
import reference.RefList;
import reference.Reference;
import reference.ReferenceParser;
import scala.Tuple2;
import utils.CustomLog;
import utils.CustomPdfTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pdf.parser.TextBlock.apply;

public class PdfBoxReader {
	public static Set<RefList> refs = new HashSet<>();

	public static void clear() {
		refs = new HashSet<>();
	}

	public static void readFile(String folder, String name) {
		PDDocument doc = null;

		File ne = new File("tmp" + File.separator + folder + File.separator + name);
		try (InputStream is = new FileInputStream(ne)) {

			String fileName = "refs" + File.separator + folder + File.separator + name + ".txt";
//			new File(fileName).createNewFile();

			try (PrintWriter out = new PrintWriter(fileName)) {
				PDFParser parser;
				COSDocument cosDoc;
				parser = new PDFParser(is);
				parser.parse();
				cosDoc = parser.getDocument();

				doc = new PDDocument(cosDoc);

				int n = doc.getNumberOfPages();

				long time = System.currentTimeMillis();
				List<Reference> references = new ArrayList<>();
				for (int i = n - 1; i <= n; i++) {

					final CustomPdfTextStripper stripper = new CustomPdfTextStripper(i);
					// need to read text to extract lines
					stripper.getText(doc);

					try {
						final TextBlock block = getBlock(stripper.getResult(), name);
						references.addAll(block.printReferences());
						out.println(block.printReferenceBlocks());
					} catch (IndexOutOfBoundsException ie) {
						CustomLog.info("Skipped page " + i + " of " + folder + File.separator + name
								+ " due to the emptiness");
					}

				}

				final CustomPdfTextStripper stripper = new CustomPdfTextStripper(1);
				stripper.getText(doc);
				final TextBlock block = getBlock(stripper.getResult(), name);

				Tuple2<String, String> authorAndTitle = block.findAuthorAndTitle();

				long endTime = System.currentTimeMillis();
				double ti = (endTime - time) / 1000.;

				CustomLog.info("Article: " + authorAndTitle._2() + " by " + authorAndTitle._1());
				CustomLog.info("was processed in " + ti + " seconds");

				try {
					List<String> authors = ReferenceParser
							.getAuthors(authorAndTitle._1());
					String title = authorAndTitle._2();
					RefList e = new RefList(authors, title, references);
					e.setAuth_old(authorAndTitle._1());
					refs.add(e);

					out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					out.println("Parsed");
					for (String author: authors) {
						out.print(author +" ");
					}
					out.println();
					out.println(title);
					out.println("references");
					for (Reference reference: references) {
						out.println(reference);
					}
				} catch (LangDetectException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static TextBlock getTextBlock(int page) {
		PDDocument doc = null;

		try (InputStream is = PdfBoxReader.class.getResourceAsStream("test.pdf")) {
			PDFParser parser;
			COSDocument cosDoc;

			parser = new PDFParser(is);
			parser.parse();
			cosDoc = parser.getDocument();

			doc = new PDDocument(cosDoc);

			final CustomPdfTextStripper stripper = new CustomPdfTextStripper(page);
			stripper.getText(doc);

			return getBlock(stripper.getResult(), "default");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static TextBlock getBlock(List<List<PDFTextStripper.WordWithTextPositions>> lines, String title) {
		final Double pageHeight = (double) lines.get(0).get(0).getTextPositions().get(0).getPageHeight();
		return apply(lines, pageHeight, title);
	}
}
