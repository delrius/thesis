package utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomPdfTextStripper extends PDFTextStripper {

    private final List<List<WordWithTextPositions>> lines = new ArrayList<>();

    public CustomPdfTextStripper(int start) throws IOException {
        super();
        setStartPage(start);
        setEndPage(start);
    }

    @Override
    public void setStartPage(int startPageValue) {
        super.setStartPage(startPageValue);
        assert (getEndPage() == startPageValue);
    }

    @Override
    public void setEndPage(int endPageValue) {
        super.setEndPage(endPageValue);
        assert (endPageValue == getStartPage());
    }

    @Override
    protected void writeLine(List<WordWithTextPositions> line, boolean isRtlDominant) throws IOException {
        super.writeLine(line, isRtlDominant);
        lines.add(line);
    }

    @Override
    protected void endDocument(PDDocument pdf) throws IOException {
        //lines.clear();
    }

    public List<List<WordWithTextPositions>> getResult() {
        return lines;
    }
}
