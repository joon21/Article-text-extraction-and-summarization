package juneh017.capitalone_challenge;



import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;

import net.sf.classifier4J.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.PatternSyntaxException;


/**
 * Created by Juneh017 on 3/18/2016.
 */
public class SummaryTask extends AsyncTask<Void, Void, String>{
    int linesCount, stringLen;
    private Set<String> keywordSet, entitySet, wordSet, prioritySet;
    private ArrayList<String> abbrevList;
    private String text, title;
    private String[] finalReturnArray = new String[2];
    private InputStream abbrevFileStream;

    public SummaryTask(String title, String text, String[] keywords,
                       String[] entities, int linesCount, InputStream fileStream) {
        this.text = text;
        this.title = title;
        this.linesCount = linesCount;
        abbrevFileStream = fileStream;
        keywordSet = new LinkedHashSet<>(Arrays.asList(keywords));
        entitySet = new LinkedHashSet<>(Arrays.asList(entities));
        wordSet = new LinkedHashSet<>();
        prioritySet = new LinkedHashSet<>();
        abbrevList = new ArrayList<>();
    }

    @Override
    protected String doInBackground(Void... params) {
        initAbbrevList();
        keywordSet.remove("");
        entitySet.remove("");
        wordSet.addAll(keywordSet);
        wordSet.addAll(entitySet);

        Set<String> tempEntitySet = new LinkedHashSet<>();
        tempEntitySet.addAll(entitySet);
        for(String shortKeyword : tempEntitySet) {
            for(String longKeyword : tempEntitySet) {
                if((!shortKeyword.equals(longKeyword)) && (longKeyword.contains(shortKeyword))) {
                    entitySet.remove(shortKeyword);
                }
            }
        }

        if(text != null) {
            if(wordSet.size() < 1) {
                return this.summarise(text, linesCount, false);
            }
            text = this.summarise(text, linesCount, true);
            stringLen = text.length();
            setHyperLinks(entitySet);
            text += "<br><br><b>#Keywords: " + entitySet.toString() + "</b>";
            return text;
        } else {
            return "";
        }
    }


    public void setHyperLinks(Set<String> wordSet) {
        for(String key : wordSet) {
            if(key.length() > 1) {
                try {
                    text = text.replaceFirst(key,
                            "<a href=\"https://en.wikipedia.org/wiki/" + key + "\">" + key + "</a>");
                } catch (PatternSyntaxException exc) {
                    continue;
                }
            }
        }
    }

    public String summarise(String input, int numSentences, boolean hasKeyword) {
        LinkedHashSet<String> tempSet = new LinkedHashSet<>();
        Set<String> keySet = new LinkedHashSet<>();
        int sentenceCount = 0;
        /** reorder priority of ranked keywords **/
        if(hasKeyword) {
            for(String s : wordSet) {
                if(title.toLowerCase().contains(s.toLowerCase())) {
                    prioritySet.add(s.toLowerCase());
                }
                else if(s.length() >= 2) {
                    tempSet.add(s.toLowerCase());
                }
            }
            keySet.addAll(prioritySet);
            keySet.addAll(tempSet);
        } else {
            Map wordFrequencies = Utilities.getWordFrequency(input);
            keySet = this.getMostFrequentWords(100, wordFrequencies);
        }

        String[] workingSentences = Utilities.getSentences(input.toLowerCase());
        String[] actualSentences = Utilities.getSentences(input);

        LinkedHashSet outputSentences = new LinkedHashSet();
        Iterator it = keySet.iterator();

        while(it.hasNext()) {
            String reorderedOutputSentences = (String)it.next();

            for(int result = 0; result < workingSentences.length; result++) {
                if(workingSentences[result].indexOf(reorderedOutputSentences) >= 0) {
                    outputSentences.add(actualSentences[result]);
                    break;
                }

                if(outputSentences.size() >= numSentences) {
                    break;
                }
            }

            if(outputSentences.size() >= numSentences) {
                break;
            }
        }

        List var13 = reorderSentences(outputSentences, input);
        StringBuffer var12 = new StringBuffer("");
        it = var13.iterator();

        while(it.hasNext()) {
            String sentence = (String)it.next();
            var12.append(sentence);
            var12.append(".");
            if(it.hasNext()) {
                var12.append(" ");
            }
        }

        return var12.toString();
    }

    /** TODO getting actual sentences **/
    public String[] getSentences(String input, boolean isCaseSensitive) {

        int replaceCount = 1, sentenceCount;
        String lowerCaseInput, inputCopy;
        String[] sentences, actualSentences;
        ArrayList<String> sents = new ArrayList<>();
        LinkedHashMap<String, String> replaceMap = new LinkedHashMap<>();
        /**TODO Remove this**/
        if(abbrevList.size() == 0) {
            return new String[0];
        }
        if(input != null) {
            lowerCaseInput = input.toLowerCase();
            for(String abbrev : abbrevList) {
                String replace = "#" + replaceCount + "@";
                lowerCaseInput = lowerCaseInput.replace(" " + abbrev.toLowerCase(), replace);


                replaceMap.put(replace, " " + abbrev.toLowerCase());
                replaceCount++;

            }

            sentences = lowerCaseInput.split("(\\.|!|\\?)+(\\s|\\z)");
            actualSentences = new String[sents.size()];


            for(int index = 0; index < sentences.length; index++) {
                String temp = sentences[index];
                for(Map.Entry<String, String> entry : replaceMap.entrySet()) {
                    sentences[index] = temp.replace(entry.getKey(), entry.getValue());
                }
            }
            String copyInput = input;
            int start, end;
            for(int index = 0; index < sentences.length; index++) {

                start = copyInput.toLowerCase().indexOf(sentences[index].toLowerCase());

                end = start + sentences[index].length();

                actualSentences[index] = copyInput.substring(start, end);
                copyInput = copyInput.substring(end);

            }
            /** Returns array of sentences in lowercase if parameter **/
            /** specifies not case sensitive for returned results    **/
            if(!isCaseSensitive) {
                return sentences;
            }

            if(sentences.length != actualSentences.length) {
                try {
                    throw new IOException("NOT ALL SENT INCLUDED");
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }



            return actualSentences;
        }
        return new String[0];
    }

    public void initAbbrevList() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(abbrevFileStream));

            String word;
            while((word = reader.readLine()) != null) {
                abbrevList.add(word.trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected Set getMostFrequentWords(int count, Map wordFrequencies) {
        return Utilities.getMostFrequentWords(count, wordFrequencies);
    }

    private List reorderSentences(Set outputSentences, final String input) {
        ArrayList result = new ArrayList(outputSentences);
        Collections.sort(result, new Comparator() {
            public int compare(Object arg0, Object arg1) {
                String sentence1 = (String)arg0;
                String sentence2 = (String)arg1;
                int indexOfSentence1 = input.indexOf(sentence1.trim());
                int indexOfSentence2 = input.indexOf(sentence2.trim());
                int result = indexOfSentence1 - indexOfSentence2;
                return result;
            }
        });
        return result;
    }

}
