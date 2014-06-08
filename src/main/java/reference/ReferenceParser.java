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
import utils.CustomLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReferenceParser {

    public static void main(String[] args) throws IOException, LangDetectException {

        String string = "1. Ананьев Б. Г. О проблемах современного человекознания / \n"
                + "Б. Г. Ананьев. – М. : Наука , 1977.\n"
                + "2. Андон Ф. И. Логические модели интеллек туаль ных инфор-\n"
                + "мационных систем / И. Ф. Андон , А. Е. Яшунин , В. А. Рез-\n"
                + "ниченко. – К. : Наукова думка , 1999. – 396 с.\n"
                + "3. Белнап Н. Логика вопросов и ответов / Н. Белнап , Т. Стил. – \n"
                + "М. : Прогресс , 1981. – 288 с.\n"
                + "4. Величковский Б. М. Когнитивная наука : основы психоло-\n"
                + "гии познания / Б. М. Величковский. – М. : 2006. – Т. I , II.\n"
                + "5. Гаврилова Т. А. Базы знаний интеллектуальных систем / \n"
                + "Т. А. Гаврилова , В. Ф. Хорошевский. – СПб. : Питер , 2000. – \n"
                + "384 с.\n"
                + "6. Глибовець М. М. Штучний інтелект / М. М. Глибовець , \n"
                + "О. В. Олецький. – К. : КМ Академія , 2002. – 366 с. \n"
                + "7. Капитонова Ю. В. Парадигмы и идеи академика В. М. Глуш-\n"
                + "кова / Ю. В. Капитонова , А. А. Летичевский. – К. : Наукова \n"
                + "думка , 2003. – 455 с.\n"
                + "8. Карнап Р. Значение и необходимость / Р. Карнап. – М. , \n"
                + "1959.\n"
                + "9. Кокорева Л. В. , Диалоговые системы и представ ле ние зна-\n"
                + "ний / Л. В. Кокорева , О. Л. Перевозчикова , Е. Л. Ющенко. – \n"
                + "К. : Наукова думка , 1992. – 448 с.\n"
                + "10. Куайн У. В. О. Онтологическая относительность / \n"
                + "У. В. О. Куайн // Современная философия науки: знание , \n"
                + "рациональность , ценности в трудах мыслителей Запада : \n"
                + "Учебная хрестоматия. – М. : Логос , 1996.\n"
                + "11. Линдон Р. Заметки по логике / Р. Линдон. – М. : Мир , 1968. – \n"
                + "128 с.\n"
                + "12. Мальцев А. И. Алгебраические системы / А. И. Мальцев. – \n"
                + "М. : Наука , 1970. – 392 с.\n"
                + "13. Мейтус В. Ю. К проблеме интеллектуализации систем уп-\n"
                + "равления / В. Ю. Мейтус // Матеріали ХІІІ Міжнародної \n"
                + "конференції з автоматичного управління (Автоматика – \n"
                + "2006). Вінниця , 25–28 вересня 2006 р. – Вінниця , 2006. – \n"
                + "С. 466–471. \n"
                + "14. Мейтус В. Ю. К проблеме интеллектуализации компьютер-\n"
                + "ных систем / В. Ю. Мейтус // Математичні машини і систе-\n"
                + "ми. – 2008. – № 2. – С. 24–37.\n"
                + "15. Мендельсон Э. Введение в математическую логику / \n"
                + "Э. Мендельсон. – М. : Наука , 1971. – 320 с.\n"
                + "16. Пиаже Ж. Психология интеллекта / Ж. Пиаже // Избранные \n"
                + "психологические труды. – М. : Просвещение , 1969.\n"
                + "17. Поспелов Д. А. Ситуационное управление : теория и прак-\n"
                + "тика / Д. А. Поспелов. – М. : Наука , 1986. – 288 с.\n"
                + "18. Рубинштейн С. Л. Избранные философско-психологические \n"
                + "труды / С. Л. Рубинштейн. – М. : Наука , 1997.\n"
                + "19. Уотермен Д. Руководство по экспертным системам / Д. Уо-\n"
                + "термен. – М. : МИР , 1989. – 388 с.\n"
                + "20. Хайдеггер М. Бытие и время / М. Хайдеггер. – М. : Ad \n"
                + "Marginem , 1997. – 236 c. \n"
                + "21. Хокинс Д. Об интеллекте / Д. Хокинс , С. Блейксли. – М. : \n"
                + "Вильямс , 2007. – 240 с.\n"
                + "22. Холодная М. А. Психология интеллекта. Парадоксы иссле-\n"
                + "дования / М. А. Холодная. – СПб. : Питер , 2002. – 272 с.\n"
                + "23. Gottfredson L. S. Mainstream Science on Intelligence / \n"
                + "L. S. Gottfredson // Wall Street Journal. – December 13 , 1994. – \n"
                + "P. 18.\n"
                + "24. Glaser R. A research agenda for cognitive psychology and \n"
                + "psychometrics / R. Glaser // Amer. Psychologist. – 1980. – V. 36 \n"
                + "(9). – P. 923–936.\n"
                + "25. Glaser R. Education and thinking: The role of knowledge / \n"
                + "R. Glaser // Amer. Psychologist. 1984. – V. 39(2). – P. 93–104.\n"
                + "26. Gui1fогd I. P. The structure of intellect / I. P. Gui1fогd // \n"
                + "Psychol. Bull – 1956. – V. 53. – P. 267–293.\n"
                + "27. Sternberg  R. J. Introduction / R. J. Sternberg // Models of \n"
                + "intelligence : International perspectives / R. J. Sternberg , \n"
                + "J. Lautrey & T. I. Lubart (Eds.). – Washington , DC : American \n"
                + "Psychological Association , 2003.\n";
        getReferences(string);
    }

    public static List<Reference> getReferences(String string) throws IOException, LangDetectException {

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
                    List<String> person = span.findByType("person");
                    List<String> person1 = new ArrayList<>();

                    for (String title : person) {
                        if (title.contains("Глибовець М. М.") || title.contains("Глибовец Н. Н.")) {
                            person1.add("Глибовець М. М.");
                        } else {
                            person1.add(title);
                        }
                    }
                    references.add(new Reference(person1, titles.get(0)));
                }
            }

            tmpIndex = bound;
        }
        return references;
    }

    public static List<String> getAuthors(String string) throws IOException, LangDetectException {
        string = "1. " + string;

        List<String> authors = new ArrayList<>();

        InputStream modelIn = ReferenceParser.class.getResourceAsStream(getForLangName(detectLang(string)));

        List<SpansListWrapper> textSpans = getTextSpans(string, modelIn);

        for (SpansListWrapper span : textSpans) {
            List<String> titles = span.findByType("person");
            if (!titles.isEmpty()) {
                for (String title : titles) {
                    if (title.contains("Глибовець М. М.") || title.contains("Глибовец Н. Н.")) {
                        authors.add("Глибовець М. М.");
                    } else {
                        authors.add(title);
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

//					NameSample nameSample = new NameSample(whitespaceTokenizerLine,
//							reducedNames, false);

                    //System.out.println(nameSample.toString());

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

