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
	const DELIMITER_KEYWORDS_SEARCH_DETAILS_AMAZON = ";||;";
	private $currentProxy = null;

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
		 * Build suggestions from amazon and get latests's books suggestions
		 **/
		$forceBuild = $request->input('force_build', false) == "true" ? true : false;
		$suggestions['latest_suggestions'] = $this->buildSuggestionsFromAmazon($forceBuild);

		/** Get overall suggestions with reference count */
		$suggestions['overall_suggestions'] = $this->getOverallSuggestions();
		
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

	private function getLatestSuggestions($nbrSuggestions, $randomSort = false)
	{
		$suggestions = array();
		$currentSuggestions = $this->getCurrentUser()
		->suggestions()
		->orderBy('updated_at', 'desc')
		->limit($nbrSuggestions)
		->get();
		if ($randomSort)
		{
			$currentSuggestions = $currentSuggestions->shuffle();
			$currentSuggestions = $currentSuggestions->all();
			Log::debug("random sort");
		}
		foreach ($currentSuggestions as $currentSuggestion)
		{
			$suggestions[] = $currentSuggestion->isbn;
		}

		return $suggestions;
	}

	/**
	 * Get suggestions with the most referenced during old builds
	 * 
	 **/
	private function getOverallSuggestions()
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

		try
		{


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
		}
		catch (\Exception $e)
		{
			Log::debug("Exception during extracting EXACT result");
			$url = null;
		}

		return htmlspecialchars_decode($url);
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
	 * Find from the global search amazon page the url of the book in question
	 **/
	private function findExactBookUrlFromSearchPageofAmazon($searchPageHtml)
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
		$amazonBookUrl = $this->getExactResultFromHtml($searchPageHtml, $patternsToSearchExactUrl);

		return $amazonBookUrl;
	}

	/**
	 * Find from the book's page all suggestions that are related to the
	 * book, basically it's just list of ids
	 **/
	private function findSuggestionsFromAmazonBookPage($searchPageHtml)
	{
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
		$amazonSuggestionsIdsFromUrl = $this->getExactResultFromHtml(htmlspecialchars_decode($searchPageHtml), $patternsToSearchSuggestionIds);

		/** Suggestions are fetched as json representation from amazon */
		$suggestionIds = json_decode($amazonSuggestionsIdsFromUrl);
		/** Only first six ids from amazon are really important */
		if ($suggestionIds != null)
		{
			$suggestionIds = array_slice($suggestionIds, 0, 7);			
		}

		return $suggestionIds;
	} 


	private function findNewProxyFromGimmeProxy($usePrivateProxies = false)
	{
		$urlProxies = null;
		if ($usePrivateProxies)
		{
			$urlProxies = "https://api.myprivateproxy.net/v1/fetchProxies/json/full/9lghkyvc5ipt15cshw673zm63zhpbor1";
		}
		else
		{
			$urlProxies = "http://gimmeproxy.com/api/getProxy?country=FR,GB,IT,LU&user-agent=true&protocol=http&supportsHttps=true&minSpeed=199";
		}

		$result = json_decode(file_get_contents($urlProxies), true);
		
		Log::debug("New Proxy Result = ");
		Log::debug($result);
		if ($usePrivateProxies)
		{
			$randomIndex = rand(0, count($result) - 1);
			Log::debug("Result length : " . count($result));
			Log::debug("random index : " . $randomIndex);
			$proxy = $result[$randomIndex];
			$result = $proxy['username'] . ':' . $proxy['password'] . '@' . $proxy['proxy_ip'] . ':' . $proxy['proxy_port'];
			Log::debug("Final built proxy url : " . $result);
		}
		else
		{
			if (key_exists('curl', $result) && key_exists('websites', $result))
			{
				if (key_exists('amazon', $result['websites']) && $result['websites']['amazon'] == true)
				{
					$result = $result['curl'];
					Log::debug("Final result : " . $result);				
				}
				else
				{
					Log::debug("Final result null");
					$result = null;
				}
			}
			else
			{
				Log::debug("Final result null");
				$result = null;
			}
		}
		usleep(1000);

		return $result;
	}

	/**
	 * Get a new proxy to use
	 * 
	 **/
	private function getNewProxy()
	{
		$usePrivateProxies = false;
		Log::debug("GET NEW PROXY BRO method begin");
		if ($this->currentProxy == null)
		{
			$result = $this->findNewProxyFromGimmeProxy($usePrivateProxies);
			while ($result == null)
			{
				$result = $this->findNewProxyFromGimmeProxy($usePrivateProxies);
				Log::debug("Find again a proxy ...");
			}
		// Log::debug("CHELOU status code : " . $result->getStatusCode());
		// Log::debug("Raw result  : " . $result->getBody());
			Log::debug("Nice found a amazon proxy");
			$this->currentProxy = $result;
		}

		return $this->currentProxy;
	}

	/**
	 * Custom file get contents with context
	 * 
	 **/
	private function fileGetContentsWithContext($url)
	{
		$result = null;
		$proxy = $this->getNewProxy();
		$client = new \GuzzleHttp\Client();
		
		Log::debug("FUCKING URL =" . $url);
		
		try
		{
			$res = $client->request('GET', $url , array(
				'proxy' => $proxy,
				'debug' => false,
				'headers' => array(
					'Host' => 'www.amazon.fr',
					'User-Agent' => "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0",
					'Accept' => 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
					'Accept-Encoding' => 'gzip, deflate, br',
					'Accept-Language' => 'en-US,en;q=0.9,fr;q=0.8',
					'Upgrade-Insecure-Requests' => '1',
					'Connection' => 'Keep-Alive'
				)
			));

			Log::debug("GUZZLE RESULT " . $res->getBody());		

			$result = $res->getBody();

		}
		catch (Exception $e)
		{
			Log::debug("Exception during request of file get content");
		}
		catch (\GuzzleHttp\Exception\RequestException $eG)
		{
			Log::debug("Exception during request of file get content guzzle");
		}

		Log::debug("RESULT OF CURL IS NULL =" . empty($result));
		Log::debug("RESULT CURL = " . $result);

		return $result;
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
		$amazonSearchOutput = $this->fileGetContentsWithContext($amazonSearchUrl);
		$suggestionIds = [];
		$amazonSearchOutputIsValid = $this->amazonSearchOutputIsValid($amazonSearchOutput);

		if ($amazonSearchOutputIsValid)
		{
			/** Get the exact url of the book in question */
			$amazonBookUrl = $this->findExactBookUrlFromSearchPageofAmazon($amazonSearchOutput);
			/** Content of exact url of the book */
			$amazonBookUrlContent = $this->fileGetContentsWithContext($amazonBookUrl);

			/** Get suggestions from book's page */
			$amazonSuggestions = $this->findSuggestionsFromAmazonBookPage($amazonBookUrlContent);
		}
		else
		{
			Log::debug("The generated amazon link seems to be not valid");
		}

		return $amazonSuggestions;
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
	private function buildSuggestionsFromAmazon($forceBuild = false)
	{
		$latestSuggestions = [];
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
		if ($allowedToFetchFromAmazon || $forceBuild)
		{
			$userLatestBooks = $this->getCurrentUser()
			->books()
			->latest()
			->limit(3)
			->get();
			foreach ($userLatestBooks as $userLastBook)
			{
				$suggestions = $this->fetchSuggestionsFromAmazonWithIsbn($userLastBook->isbn);
				if ($suggestions != null)
				{
					$latestSuggestions = array_merge($latestSuggestions, $suggestions);
					$this->storeSuggestions($suggestions);
				}
				$this->currentProxy = null;
				Log::debug('trying to fetch the book n=' . $userLastBook->isbn);
			}
			shuffle($latestSuggestions);
		}
		else
		{
			$latestSuggestions = $this->getLatestSuggestions(18, true);
		}

		return array_unique($latestSuggestions);
	}

	/**
	 * Extract the title from the page of the book
	 **/
	private function findAmazonBookTitleFromPage($amazonBookContent)
	{
		/** Amazon book's title search patterns */
		$patternsToSearchTitle = array(
			'patternBeginOverallSearch' => 'div id="booksTitle',
			'patternEndOverallSearch' => 'div id="bookDescription_feature_div',
			'patternBeginCloseToResult' => '<span id="',
			'patternEndCloseToResult' => '<span',
			'patternBeginResult' => '>',
			'patternEndResult' => '</span'
		);
		/** Try to extract the suggestions isbn of the current book */
		$bookTitle = $this->getExactResultFromHtml(
			htmlspecialchars_decode($amazonBookContent),
			$patternsToSearchTitle);

		Log::debug("Amazon book's title =" . $bookTitle);

		return $bookTitle;
	}

	/**
	 * Extract main book's picture
	 **/
	private function findAmazonBookPictureUrlFromPage($amazonBookContent)
	{
		/** Book's main picture url search patterns */
		$patternsToSearchPictureUrl = array(
			'patternBeginOverallSearch' => 'div id="leftCol"',
			'patternEndOverallSearch' => 'div id="centerCol"',
			'patternBeginCloseToResult' => 'img-wrapper',
			'patternEndCloseToResult' => 'type="text/javascript"',
			'patternBeginResult' => 'data-a-dynamic-image="{"',
			'patternEndResult' => '"'
		);

		$bookPictureUrl = $this->getExactResultFromHtml(
			htmlspecialchars_decode($amazonBookContent),
			$patternsToSearchPictureUrl);

		Log::debug("Amazon book's picture url =" . $bookPictureUrl);

		return $bookPictureUrl;
	}

	/**
     * Search book's details from amazon
     **/
	public function searchDetailsFromAmazon($searchKeywordsFields)
	{
		$details = [];

		$searchDetailsIds =
		explode(self::DELIMITER_KEYWORDS_SEARCH_DETAILS_AMAZON,
			$searchKeywordsFields);
		$amazonBuyController = new AmazonBuyController();	
		foreach($searchDetailsIds as $searchDetailsId)
		{
			$gotErrorWhileFileGetContent = false;
			$detailsOfId = [];
			$amazonSearchUrl = $amazonBuyController->generateRawAmazonLinkFromSearch($searchDetailsId);
			try
			{
				$amazonSearchOutput = $this->fileGetContentsWithContext($amazonSearchUrl);
			}
			catch (\Exception $e)
			{
				$gotErrorWhileFileGetContent = true;
			}
			if (!$gotErrorWhileFileGetContent)
			{
				$amazonExactBookUrl = $this->findExactBookUrlFromSearchPageofAmazon($amazonSearchOutput);
				try
				{
					$amazonExactBookContent = $this->fileGetContentsWithContext($amazonExactBookUrl);
				}
				catch (\Exception $e)
				{
					$gotErrorWhileFileGetContent = true;
				}
				if (!$gotErrorWhileFileGetContent)
				{
					$detailsOfId['book_title'] = htmlspecialchars_decode($this->findAmazonBookTitleFromPage($amazonExactBookContent));
					$detailsOfId['book_picture_url'] = $this->findAmazonBookPictureUrlFromPage($amazonExactBookContent);
					$detailsOfId['book_amazon_url'] = $amazonExactBookUrl;
					$details[] = $detailsOfId;
				}
			}
			usleep(1500);
		}

		return $details;
	}
}
