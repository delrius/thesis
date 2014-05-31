package reference;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.InputStream;

public class ReferenceParser {
    private static String c = "1. Handbook of Automated Reasoning / Edited by A. Robinson \n" +
            "and A.Voronkov. – Elsevier Science Publishers, 2001. – Vol. 1. – \n" +
            "1020 p.\n" +
            "2. Stickel M. A. Prolog Technology Theorem Prover / \n" +
            "M. A. Stickel. – New Generation Comp. – 1984. – Vol. 4. – \n" +
            "P. 371–383. \n" +
            "3. Lloyd J. V. Foundations of Logic Programming / J. V. Lloyd. – \n" +
            "Berlin : Springer, 1987. – 476 p.\n" +
            "4. Apt K. R. Contributions into the Theory of Logic Programming \n" +
            "/ K. R. Apt, M. H. van Emden // JASM. – 1982. – Vol. 3. – \n" +
            "№ 29. – P. 841–862.\n" +
            "5. Gallier J. Logic for computer science: foundations of Automatic \n" +
            "Theorem Proving / J. Gallier. – New York : Harper and Row, \n" +
            "Inc., 1986. \n" +
            "6. Robinson J. A machine-oriented logic based on resolution \n" +
            "principle / J. Robinson // Journal of the ACM. – 1965. – \n" +
            "Vol. 12. – № 1. – P. 23–41. \n" +
            "7. Kanger S. Simplifi ed proof method for elementary logic / \n" +
            "S. Kanger – Comp. Program. and Form. Sys. : Stud. in Logic. \n" +
            "– Amsterdam : North-Holl., Publ. Co., 1963. – P. 87–93. \n" +
            "8. Lyaletski A. Gentzen calculi and admissible substitutions / \n" +
            "A. Lyaletski. – Actes Preliminaieres, du Symposium Franco-";

    public static void main(String[] args) throws IOException {

        InputStream modelIn = ReferenceParser.class.getResourceAsStream("en-ner-person.bin");
        InputStream tokenIn = ReferenceParser.class.getResourceAsStream("en-token.bin");


        try {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);

            TokenizerModel modelToken = new TokenizerModel(tokenIn);

            Tokenizer tokenizer = new TokenizerME(modelToken);

            String tokens[] = tokenizer.tokenize(c);

            int i = 0;
            for (String a : tokens) {
                System.out.println(i + "--->" + a);
                i++;
            }


            Span nameSpans[] = nameFinder.find(tokens);
            // do something with the names

            for (Span na: nameSpans) {
                System.out.println(na.toString());
            }

            nameFinder.clearAdaptiveData();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            modelIn.close();
            tokenIn.close();
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
