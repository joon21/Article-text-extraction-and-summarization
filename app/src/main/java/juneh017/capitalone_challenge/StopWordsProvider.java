package juneh017.capitalone_challenge;

import net.sf.classifier4J.IStopWordProvider;
import net.sf.classifier4J.util.ToStringBuilder;

import java.util.Arrays;

/**
 * Created by Juneh017 on 3/20/2016.
 */
public class StopWordsProvider implements IStopWordProvider {
    private String[] stopWords = new String[]{"mr", "ms", "mrs", "miss", "would", "will", "be", "he", "she", "we", "us", "him", "his", "hers", "her", "a", "and", "the", "me", "i", "of", "if", "it", "is", "they", "there", "but", "or", "to", "this", "you", "in", "your", "on", "for", "as", "are", "that", "with", "have", "be", "at", "or", "was", "so", "out", "not", "an"};
    private String[] sortedStopWords = null;

    public StopWordsProvider() {
        this.sortedStopWords = this.getStopWords();
        Arrays.sort(this.sortedStopWords);
    }

    public String[] getStopWords() {
        return this.stopWords;
    }

    public boolean isStopWord(String word) {
        return word != null && !"".equals(word)?Arrays.binarySearch(this.sortedStopWords, word.toLowerCase()) >= 0:false;
    }

    public String toString() {
        return (new ToStringBuilder(this)).append("stopWords.size()", (double)this.sortedStopWords.length).toString();
    }
}
