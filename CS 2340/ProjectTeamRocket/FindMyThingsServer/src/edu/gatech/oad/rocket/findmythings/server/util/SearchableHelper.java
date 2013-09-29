package edu.gatech.oad.rocket.findmythings.server.util;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utility class for indexing and searching items conforming to the Searchable
 * interface.
 *
 * User: zw
 * Date: 4/8/13
 * Time: 2:35 AM
 */
public class SearchableHelper {
	private static final Logger log = Logger.getLogger(SearchableHelper.class.getName());

	// Magic numbers!
	private static final int MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH = 10;

	private static final int MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX = 200;

	public static <T extends Searchable> Query<T> search(Objectify objectify, Class<T> clazz, String searchString) {
		if (searchString == null || searchString.length() == 0 || objectify == null || clazz == null) return null;
		Set<String> queryTokens = getSearchTokens(searchString, MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		Query<T> query = objectify.load().type(clazz);
		if (queryTokens == null) return query;
		return query.filter("searchTokens in", queryTokens);
	}

	public static <T extends Searchable> Query<T> search(LoadType<T> query, String searchString) {
		if (searchString == null || searchString.length() == 0 || query == null) return null;
		Set<String> queryTokens = getSearchTokens(searchString, MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		if (queryTokens == null) return query;
		return query.filter("searchTokens in", queryTokens);
	}

	public static void addSearchFilter(Map<String, Object> queryFilters, String searchString) {
		if (searchString == null || searchString.length() == 0 || queryFilters == null) return;
		Set<String> queryTokens = getSearchTokens(searchString, MAXIMUM_NUMBER_OF_WORDS_TO_SEARCH);
		if (queryTokens == null) return;
		queryFilters.put("searchTokens in", queryTokens);
	}

	public static void updateSearchTokens(Searchable item) {
		Set<String> ftsTokens = item.getSearchTokens();
		ftsTokens.clear();
		
		if (!item.canGetSearchableContent()) {
			return;
		}
		String sb = item.getSearchableContent();
		Set<String> new_ftsTokens = getSearchTokens(sb, MAX_NUMBER_OF_WORDS_TO_PUT_IN_INDEX);

		for (String token : new_ftsTokens) {
			ftsTokens.add(token);
		}
	}

	/**
	 * Uses Apache Lucene English stemming for indexing similar words.
	 *
	 * @param searchableContext A string describing the object to be indexes
	 * @param maximumNumberOfTokens The limit number of tokens to index
	 * @return A set containing indexed, searchable tokens
	 */
	private static Set<String> getSearchTokens(String searchableContext, int maximumNumberOfTokens) {

		String indexCleanedOfHTMLTags = searchableContext.replaceAll("<.*?>"," ");

		try (Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_42)) {
			Set<String> returnSet = new HashSet<>();

			TokenStream stream = analyzer.tokenStream(null, new StringReader(indexCleanedOfHTMLTags));
			CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				String string = cattr.toString();
				if (string != null && string.length() != 0) {
					returnSet.add(string);
				}
				if (returnSet.size() == maximumNumberOfTokens - 1) break;
			}
			stream.end();
			stream.close();

			if (returnSet.size() > 0) return returnSet;
		} catch (IOException e) {
			log.severe(e.getMessage());
		}

		return null;
	}



}
