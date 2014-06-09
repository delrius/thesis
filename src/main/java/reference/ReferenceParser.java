package reference;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import utils.ConfigReader;
import utils.CustomLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReferenceParser {

    public static List<Reference> getReferences(String string) throws IOException, LangDetectException {

        List<List<String>> variationsList = ConfigReader.authorVariationsJava();
        List<Reference> references = new ArrayList<>();

        InputStream modelIn = ReferenceParser.class.getResourceAsStream("refs-en.bin");

        String mod = getText(string, modelIn);
        int tmpIndex = 0;

        while (whereRef(mod, tmpIndex) != -1) {
            int in = mod.indexOf("<START:ref>", tmpIndex);
            int bound = whereRef(mod, whereRefEnd(mod, in) + 7);
            if (bound < 0) {
                bound = mod.length();
            }

            String output = mod.substring(in, bound).replaceAll("<START:ref>", "").replaceAll("<END>", "")
                    .replaceAll("\\n", " ");

            String lang = detectLang(output);

            List<SpansListWrapper> textSpans = getTextSpans(output,
                    ReferenceParser.class.getResourceAsStream(getForLang(lang)));

            for (SpansListWrapper span : textSpans) {
                List<String> titles = span.findByType("title");
                if (!titles.isEmpty()) {
                    List<String> persons = span.findByType("person");
                    List<String> result = new ArrayList<>();

                    for (String person : persons) {
                        String normalized = person.trim();
                        String modified = null;
                        for (List<String> list: variationsList) {
                            if (list.contains(normalized)) {
                                modified = list.get(0);
                            }
                        }
                        if (modified != null) {
                            result.add(modified.trim());
                        } else {
                            result.add(normalized);
                        }
                    }

                    references.add(new Reference(result, titles.get(0)));
                }
            }

            tmpIndex = bound;
        }
        return references;
    }

    public static List<String> getAuthors(String string) throws IOException, LangDetectException {
        List<List<String>> variationsList = ConfigReader.authorVariationsJava();
        string = "1. " + string;

        List<String> authors = new ArrayList<>();

        InputStream modelIn = ReferenceParser.class.getResourceAsStream(getForLangName(detectLang(string)));

        List<SpansListWrapper> textSpans = getTextSpans(string, modelIn);

        for (SpansListWrapper span : textSpans) {
            List<String> persons = span.findByType("person");
            if (!persons.isEmpty()) {
                for (String person : persons) {
                    String normalized = person.trim();
                    String modified = null;
                    for (List<String> list: variationsList) {
                        if (list.contains(normalized)) {
                            modified = list.get(0);
                        }
                    }
                    if (modified != null) {
                        authors.add(modified.trim());
                    } else {
                        authors.add(normalized);
                    }
                }
            }
        }
        return authors;
    }

    public static int whereRef(String s, int start) {
        return s.indexOf("<START:ref>", start);
    }

    public static int whereRefEnd(String s, int start) {
        return s.indexOf("<END>", start);
    }

    public static String detectLang(String string) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(string);
        String lang = "ua";
        try {
            lang = detector.detect();
        } catch (Exception e) {
            CustomLog.error(e.getMessage());
        }
        return lang;
    }

    public static String getForLang(String lang) {
        if (lang.contains("uk") || lang.contains("ru")) {
            return "rich-ua.bin";
        } else {
            return "rich-en.bin";
        }
    }

    public static String getForLangName(String lang) {
        if (lang.contains("uk") || lang.contains("ru")) {
            return "name-ua.bin";
        } else {
            return "name-en.bin";
        }
    }

    static {
        try {
            DetectorFactory.loadProfile("/Users/del/profiles");
        } catch (LangDetectException e) {
            e.printStackTrace();
        }
    }

    public static String getText(String str, InputStream modelIn) throws IOException {
        try {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);

            ObjectStream<String> untokenizedLineStream =
                    new PlainTextByLineStream(new StringReader(str.replaceAll(",", " , ")));

            try {
                String line;
                StringBuilder buffer = new StringBuilder();
                while ((line = untokenizedLineStream.read()) != null) {
                    String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);

                    // A new line indicates a new document,
                    // adaptive data must be cleared for a new document

                    if (whitespaceTokenizerLine.length == 0) {
                        nameFinder.clearAdaptiveData();
                    }

                    List<Span> names = new ArrayList<>();

                    Collections.addAll(names, nameFinder.find(whitespaceTokenizerLine));

                    // Simple way to drop intersecting spans, otherwise the
                    // NameSample is invalid
                    Span reducedNames[] = NameFinderME.dropOverlappingSpans(
                            names.toArray(new Span[names.size()]));

                    NameSample nameSample = new NameSample(whitespaceTokenizerLine,
                            reducedNames, false);

                    buffer.append(nameSample.toString()).append("\n");

                }
                return buffer.toString();
            } catch (IOException e) {
                CmdLineUtil.handleStdinIoError(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    CustomLog.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public static List<SpansListWrapper> getTextSpans(String str, InputStream modelIn) throws IOException {
        try {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);

            ObjectStream<String> untokenizedLineStream =
                    new PlainTextByLineStream(new StringReader(str.replaceAll(",", " , ")));

            try {
                String line;
                List<SpansListWrapper> buffer = new ArrayList<>();
                while ((line = untokenizedLineStream.read()) != null) {
                    String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);

                    // A new line indicates a new document,
                    // adaptive data must be cleared for a new document

                    if (whitespaceTokenizerLine.length == 0) {
                        nameFinder.clearAdaptiveData();
                    }

                    List<Span> names = new ArrayList<>();

                    Collections.addAll(names, nameFinder.find(whitespaceTokenizerLine));

                    Span reducedNames[] = NameFinderME.dropOverlappingSpans(
                            names.toArray(new Span[names.size()]));

                    buffer.addAll(Arrays.asList(new SpansListWrapper(whitespaceTokenizerLine, reducedNames)));
                }
                return buffer;
            } catch (IOException e) {
                CmdLineUtil.handleStdinIoError(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    CustomLog.error(e.getMessage());
                }
            }
        }
        return null;
    }

    public static class SpansListWrapper {
        private String whitespaceTokenizerLine[];
        private Span spans[];

        public SpansListWrapper(String[] whitespaceTokenizerLine, Span[] spans) {
            this.whitespaceTokenizerLine = whitespaceTokenizerLine;
            this.spans = spans;
        }

        public List<String> findByType(String type) {
            List<String> lst = new ArrayList<>();
            for (Span span : spans) {
                if (span.getType().equals(type)) {
                    lst.add(getByStartEnd(span.getStart(), span.getEnd()));
                }
            }
            return lst;
        }

        public String getByStartEnd(int start, int end) {
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                builder.append(whitespaceTokenizerLine[i]).append(" ");
            }
            return builder.toString();
        }
    }
}

