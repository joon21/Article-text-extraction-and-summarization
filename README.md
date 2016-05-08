## [CapitalOne Challenge](https://www.mindsumo.com/contests/565)
###_Article text extraction and summarization application on Android platform_
* Takes URL input from standard IO or Android 'share via' functionality and Intent 
  filter with action SEND Intent action.

* For better accuracy and parse success rate in data extraction, the extraction is 
  performed using two different libraries, jsoup Java HTML parser and AlchemyAPI.

* jsoup library provides URL connection instance with functionalities to parse 
  any HTML tags into list of Elements via CSS query selector. For set data 
  targets and tags, proper CSS query is used with selector to parse most of 
  desired data for each target.

* AlchemyAPI provides NLP based text, keyword and entity extraction via 
  function calls, and returns priority list for the extractions.

* Targets for extraction includes the name, favicon of webpage, and author,
  title, image, ranked keywords, ranked entities, and text of article.

* For summarization process, it is done by methods that implements Java 
  interfaces provided in Classifier4J library, which implements statistical 
  text tokenizer, summarizer and string utilities. The default summarizer 
  reorders sentences from parsed text based on the frequency of words that
  appears in the text. In this application, a modified summarizer utilizes 
  both frequency list and priority list of keywords that is provided by API 
  service to reorder sentences according to their priority.

* In user interface design, it has functionality to resize number of 
  sentences that the original text should summarized into. Also, it provides
  option to select text extractor between jsoup and AlchemyAPI. Since 
  AlchemyAPI based on NLP or linguistic approach, it has sometimes show limited
  ability for text extraction. In some situations, jsoup selectors implemeted 
  in this app can be more effective in extraction, especially for articles in 
  foreign languages that are not provided by  AlchemyAPI. Provides links to 
  Wikipedia pages for ranked entities and keywords in the article.

* List of articles is stored to file and can be accessed until it is cleared by
  user. Serialization of article and webpage data from extraction makes user 
  accessible to article without internet connectivity. 

###_Installation_
```
 - Runs on minimum SDK Version: 15 (Android 4.0.3, 4.0.4, ICE_CREAM_SANDWICH_MR1)
 - resource folder contains debug build apk which can be downloaded 
   into Android devices and must be installed in internal storage.  
 - Source compile and build confirmed on Android studio with build
   tool version 21 or newer. Not confirmed for versions lower than 21.
   ```
###_User Interface_
![ScreenShot](https://cloud.githubusercontent.com/assets/12173775/14069000/df9f03c8-f460-11e5-995a-f9cf281537ea.png)![ScreenShot](https://cloud.githubusercontent.com/assets/12173775/14070219/cb7cb24e-f471-11e5-821d-43e0b3c16469.png)
```
Send article url intent from Android share via list.
1. Toggle between url parsers.
2. Add a sentence to current summary.
3. Remove a sentence from current summary.
```

