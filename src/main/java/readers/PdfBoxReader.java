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
import utils.CustomPdfTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pdf.parser.TextBlock.apply;

public class PdfBoxReader {
	public static Set<RefList> refs = new HashSet<>();

    public static void readFile(String name) {
        PDDocument doc = null;

        File ne = new File(PdfBoxReader.class.getResource(name).getFile());
        try (InputStream is = new FileInputStream(ne)) {
            PDFParser parser = null;
            COSDocument cosDoc = null;
            parser = new PDFParser(is);
            parser.parse();
            cosDoc = parser.getDocument();

            doc = new PDDocument(cosDoc);

            try (PrintWriter out = new PrintWriter(new FileOutputStream("del-out.txt"))) {

                int n = doc.getNumberOfPages();
                //               int n = 5;

				long time = System.currentTimeMillis();
				List<Reference> references = new ArrayList<>();
                for (int i = n - 1; i <= n; i++) {

                    final CustomPdfTextStripper stripper = new CustomPdfTextStripper(i);


                    final String text = stripper.getText(doc);

//					out.println(text);

                    final TextBlock block = getBlock(stripper.getResult(), name);


//					System.out.println("-------start of " + i + "---------------");
					references.addAll(block.printReferences());

					//					System.out.println("-------end of " + i + "---------------");
                }

                final CustomPdfTextStripper stripper = new CustomPdfTextStripper(1);
                final String text = stripper.getText(doc);
                final TextBlock block = getBlock(stripper.getResult(), name);

				Tuple2<String, String> authorAndTitle = block.findAuthorAndTitle();

				long endTime = System.currentTimeMillis();
				double ti = (endTime - time) / 1000.;

				System.out.println("Article: "+ authorAndTitle._2()+" by " + authorAndTitle._1());
				System.out.println("was processed in " + ti + " seconds");

				try {
					RefList e = new RefList(ReferenceParser
							.getAuthors(authorAndTitle._1()), authorAndTitle._2(), references);
					e.setAuth_old(authorAndTitle._1());
					refs.add(e);
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


    public static void main(String[] args) {
        readFile("_10_apostol_av.pdf");
    }

    public static TextBlock getTextBlock(int page) {
        PDDocument doc = null;

        try (InputStream is = PdfBoxReader.class.getResourceAsStream("test.pdf")) {
            PDFParser parser = null;
            COSDocument cosDoc = null;

            parser = new PDFParser(is);
            parser.parse();
            cosDoc = parser.getDocument();

            doc = new PDDocument(cosDoc);

            final CustomPdfTextStripper stripper = new CustomPdfTextStripper(page);
            final String text = stripper.getText(doc);

            final TextBlock block = getBlock(stripper.getResult(), "default");

            return block;
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

    public static void printBlock(TextBlock block) {
//        System.out.println(block.toString());
//        System.out.println(block.print());
//        System.out.println(block.printWhite());
//        System.out.println("-------------columns----------------------");
//        System.out.println(block.printColumns());
//          block.printBlocks();
//        block.printReferences();
    }


    public static TextBlock getBlock(List<List<PDFTextStripper.WordWithTextPositions>> lines, String title) {
        final Double pageHeight = (double) lines.get(0).get(0).getTextPositions().get(0).getPageHeight();
        return apply(lines, pageHeight, title);
    }
}
