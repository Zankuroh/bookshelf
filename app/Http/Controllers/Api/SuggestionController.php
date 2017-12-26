<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Controllers\ApiController;
use App\Http\Controllers\Api\AmazonBuyController;
use App\Models\Book;
use App\Models\Suggestion;
use Log;
use Carbon\Carbon;


class SuggestionController extends ApiController
{
	/**
	 * Get a list of suggestions (books)
	 * The list is fetched from actual suggestions, amazon,
	 * and also from friends suggestion/books
	 * 
	 * 	 * Returned array contains
	 * - List of suggestions from web
	 * - List of suggestions from friends's suggestions
	 * - Latest acquisitions of friends
	 **/ 
	public function index(Request $request)
	{
		$suggestions = array();
		
		/**
		 * Build suggestions from amazon
		 **/
		$this->buildSuggestionsFromAmazon();

		/** Get actual suggestions */
		$suggestions['suggestions'] = $this->getSuggestionsOnJsonFormat();
		
		/**
		 * Suggestions from user's friends's suggestions
		 **/
		$friendsSuggestions = $this->getMostPopularUserFriendSuggestion();
		$suggestions['friends_suggestions'] = $friendsSuggestions;

		/**
		 * Latest books of user's friends
		 **/
		$latestBooksOfFriends = $this->getLatestBooksOfFriends();
		$suggestions['friends_latest_books'] = $latestBooksOfFriends;

		$this->getJsonResponse()->setData($suggestions);
		
		return $this->getRawJsonResponse();
	}

	/**
	 * Get suggestions with the most referenced during the fetching
	 * 
	 **/
	private function getSuggestionsOnJsonFormat()
	{
		$suggestions = [];
		Log::debug("Fetching from DB for suggestions");
		$currentSuggestions = $this->getCurrentUser()
		->suggestions()
		->orderBy('reference_count', 'desc')
		->limit(10)
		->get();
		foreach($currentSuggestions as $currentSuggestion)
		{
			$suggestions[] = $currentSuggestion->isbn;
		}

		return $suggestions;
	}

	/**
	 * Search a result from according params in a html content
	 * @param $htmlOutput html where we need to extract the result
	 * @param $patternsToSearchResult array
	 *      (
	 *      	'patternBeginOverallSearch'
	 *       	'patternEndOverallSearch'
	 *        	'patternBeginCloseToResult'
	 *         	'patternEndCloseToResult'
	 *          'patternBeginResult'
	 *          'patternEndResult'
	 * 		)
	 * Get the exact href of the book from global research page of amazon
	 **/
	private function getExactResultFromHtml($htmlOutput, $patternsToSearchResult)
	{
		$patternBeginListSearch = $patternsToSearchResult['patternBeginOverallSearch'];
		$patternEndListSearch = $patternsToSearchResult['patternEndOverallSearch'];

		$patternBeginCloseToHref = $patternsToSearchResult['patternBeginCloseToResult'];
		$patternEndCloseToHref = $patternsToSearchResult['patternEndCloseToResult'];

		$patternBeginHref = $patternsToSearchResult['patternBeginResult'];
		$patternEndHref = $patternsToSearchResult['patternEndResult'];

		/** Find where main list search container start */
		$positionBeginListSearchPattern = strpos($htmlOutput, $patternBeginListSearch);
		Log::debug("Position of pattern for the BEGIN of list search : " . $positionBeginListSearchPattern);

		/** Find where main list search container could be ended */
		$positionEndListSearchPattern = strpos($htmlOutput, $patternEndListSearch);
		Log::debug("Position of pattern for the END of list search : " . $positionEndListSearchPattern);
		
		/** Number of characters between begin and end of the list search container */
		$charactersBeginAndEndListSearchContainer = $positionEndListSearchPattern - $positionBeginListSearchPattern;
		Log::debug("Total numbers of characters between two pattern of list search " . $charactersBeginAndEndListSearchContainer);
		
		/** Sub string of the main container of list search, this is where href is hidden */
		$containerListSearchStr = substr($htmlOutput, $positionBeginListSearchPattern, $charactersBeginAndEndListSearchContainer);
		//Log::debug($containerListSearchStr);
		//		
		/** Find the container where the href is the most closest */
		$positionBeginCloseToHref = strpos($containerListSearchStr, $patternBeginCloseToHref);
		Log::debug('Position of BEGIN close to href ' . $positionBeginCloseToHref);

		/** Find the end of container where the href is the most closest */
		$positionEndCloseToHref = strpos($containerListSearchStr, $patternEndCloseToHref, $positionBeginCloseToHref);
		Log::debug("Position of END close to href = " . $positionEndCloseToHref);
		
		/** Container where the final href is */
		$containerCloseToHrefStr = substr($containerListSearchStr, $positionBeginCloseToHref, $positionEndCloseToHref);
		Log::debug("Container where href is : " . $containerCloseToHrefStr);

		/** Position of beginning final href */
		$positionBeginFinalHref = strpos($containerCloseToHrefStr, $patternBeginHref) + strlen($patternBeginHref);
		Log::debug("Position begin final href = " . $positionBeginFinalHref);

		/** Position of end final href */
		$positionEndFinalHref = strpos($containerCloseToHrefStr, $patternEndHref, $positionBeginFinalHref);
		Log::debug("Position end final href = " . $positionEndFinalHref);

		/** Final href url */
		$url = substr($containerCloseToHrefStr, $positionBeginFinalHref, $positionEndFinalHref - $positionBeginFinalHref);
		//var_dump($containerCloseToHrefStr);
		Log::debug("FINAL URL : " . $url);

		return $url;
	}

	/**
	 * Check if the page of search is valid to go ahead
	 **/
	private function amazonSearchOutputIsValid($htmlSearchPage)
	{
		$positionDidNotMatchAnyProducts =
			strpos($htmlSearchPage, "did not match any products") || 
			strpos($htmlSearchPage, "ne correspond à aucun article");
		return $positionDidNotMatchAnyProducts === false ? true : false;
	}

	/**
	 * Fetch suggestions from amazon
	 * This method will use parsing methods
	 * Only 6 suggestions are returned
	 * @return jsonObject
	 **/
	private function fetchSuggestionsFromAmazonWithIsbn($isbn)
	{
		$amazonBuyController = new AmazonBuyController();
		$amazonSearchUrl = $amazonBuyController->generateRawAmazonLinkFromSearch($isbn);
		$amazonSearchOutput = file_get_contents($amazonSearchUrl);
		$suggestionIds = [];
		$amazonSearchOutputIsValid = $this->amazonSearchOutputIsValid($amazonSearchOutput);
		$amazonSearchOutputIsValid = true;

		if ($amazonSearchOutputIsValid)
		{
			/** Exact url of the book search patterns */
			$patternsToSearchExactUrl = array(
				'patternBeginOverallSearch' => "results-list-atf",
				'patternEndOverallSearch' => "centerBelowMinus",
				'patternBeginCloseToResult' => "a-link-normal a-text-normal",
				'patternEndCloseToResult' => "img src",
				'patternBeginResult' => 'href="',
				'patternEndResult' => '">'
			);
			/** Exact url of the book */
			$amazonBookUrl = $this->getExactResultFromHtml($amazonSearchOutput, $patternsToSearchExactUrl);
			/** Content of url of the book */
			$amazonBookUrlContent = file_get_contents($amazonBookUrl);

			/** Suggestions of the book section parts search patterns */
			$patternsToSearchSuggestionIds = array(
				'patternBeginOverallSearch' => 'div id="purchase-sims-feature"',
				'patternEndOverallSearch' => 'div id="view_to_purchase-sims-feature"',
				'patternBeginCloseToResult' => "id_list",
				'patternEndCloseToResult' => "div class",
				'patternBeginResult' => 'id_list":',
				'patternEndResult' => ',"url"'
			);
			/** Try to extract the suggestions isbn of the current book */
			$amazonSuggestionsIdsFromUrl = $this->getExactResultFromHtml(htmlspecialchars_decode($amazonBookUrlContent), $patternsToSearchSuggestionIds);

			/** Suggestions are fetched as json representation from amazon */
			$suggestionIds = json_decode($amazonSuggestionsIdsFromUrl);
			/** Only first six ids from amazon are really important */
			$suggestionIds = array_slice($suggestionIds, 0, 7);
		}
		else
		{
			Log::debug("The generated amazon link seems to be not valid");
		}
		
		return $suggestionIds;
	}

	/**
	 * Store in database fetched suggestions
	 * If a suggestion is already in the table
	 * we'll store the number of reference and avoid duplication
	 **/
	private function storeSuggestions($suggestions)
	{
		$userId = $this->getCurrentUser()->id;
		foreach($suggestions as $suggestion)
		{
			if (ctype_digit($suggestion))
			{
				$suggestionRow = Suggestion::firstOrNew(
					[
						'isbn' => $suggestion,
						'user_id' => $userId
					]
				);
				$suggestionRow->reference_count = $suggestionRow->reference_count + 1;
				$suggestionRow->save();
			}			
		}
	}

	/**
	 * Get the latest 10 books of friends
	 * All friends are mixed together
	 **/
	private function getLatestBooksOfFriends()
	{
		$suggestions = array();
		$userFriends = $this->getCurrentUser()->getFriends();
		foreach($userFriends as $userFriend)
		{
			$userFriendBooks = $userFriend->books()->limit(2)->latest()->get();
			foreach($userFriendBooks as $userFriendBook)
			{
				$suggestions[] = $userFriendBook->isbn;
			}	
		}

		return array_slice($suggestions, 0, 10);
	}

	/**
	 * Fetch suggestions from user's friends
	 * We'll take the most 3 popular suggestions of the friend in question
	 **/
	private function getMostPopularUserFriendSuggestion()
	{
		$suggestions = [];
		$userFriends = $this->getCurrentUser()->getFriends();
		foreach($userFriends as $userFriend)
		{
			$friendsSuggestions = Suggestion::where('user_id', '=', $userFriend->friend_id)->limit(3)->get();
			foreach($friendsSuggestions as $friendSuggestions)
			{
				$suggestions[] = $friendSuggestions->isbn;
			}
		}

		return $suggestions;
	}

	/**
	 * Fetch suggestions from amazon and store them in DB
	 **/
	private function buildSuggestionsFromAmazon()
	{
		$allowedToFetchFromAmazon = true;
		$suggestionsNotEmpty = $this->getCurrentUser()
		->suggestions()
		->limit(1)
		->get()
		->isNotEmpty();
		if ($suggestionsNotEmpty)
		{
			$latestSuggestionTime = new Carbon(
				$this->getCurrentUser()
				->suggestions()
				->latest()
				->first()
				->updated_at
			);
			$allowedToFetchFromAmazon = $latestSuggestionTime->diffInHours(Carbon::now()) > 23;
		}

		/** Suggestions from amazon can be fetched only every 24 hours */
		if ($allowedToFetchFromAmazon)
		{
			$userLatestBooks = $this->getCurrentUser()
			->books()
			->latest()
			->limit(3)
			->get();
			foreach ($userLatestBooks as $userLastBook)
			{
				$suggestions = $this->fetchSuggestionsFromAmazonWithIsbn($userLastBook->isbn);
				Log::debug('trying to fetch the book n=' . $userLastBook->isbn);
				$this->storeSuggestions($suggestions);
			}
		}
	}
}
