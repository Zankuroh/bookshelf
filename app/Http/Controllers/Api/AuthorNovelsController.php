<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use \App\Models\AuthorNovels;
use DateInterval;
use DateTime;
use \App\Models\AuthorSubscription;
use \App\Models\AuthorNovelsNotification;

use Log;
use DB;
use Carbon\Carbon;

/**
 * Author novel controller, it will able to handle all
 * operation about news novels belongs to the author
 * Initially all operation will impact the notification of the
 * user if this one is following the author
 * For the moment we simply handle addition and deletion
 * 
 * @author Sergen TANGUC
 **/
class AuthorNovelsController extends \App\Http\Controllers\ApiController
{

	/**
	 * Create the new expiration date for the novel from the given
	 * duration, the starting date is the current time of the run time system
	 * please take care about the timezone, it's handled in the current version
	 **/
	private function createNewExpirationDateOfNovel($duration)
	{
		$durationIntervalDate = new DateInterval('PT' . $duration . 'H');

		$currentDate = new DateTime("now");
		$currentDate->add($durationIntervalDate);
		$newExpirationDateTime = $currentDate->format("Y-m-d H:i:s");
		Log::debug("new Expiration date for the novel is : " . $newExpirationDateTime);
		
		return $newExpirationDateTime;
	}

	/**
	 * Add a new novel for the author
	 * All user will be notified about this new novel
	 **/
	public function newNovel(Request $request)
	{
		$mandatoryFields =
		[
			'author_id' => 'required|string',
			'isbn' => 'required|string|between:5,20',
			'duration' => 'required|string|between:1,200',
			'title' => 'required|string'
		];
		if ($this->_ARV->validate($request, $mandatoryFields))
		{
			$authorNovel =  AuthorNovels::where(
				[
					'author_id' => $request->input("author_id"),
					'isbn' => $request->input('isbn')
				]
			)->first();
			
			/** Novel news not exist yet */
			if (is_null($authorNovel))
			{
				$durationOfNovel = $request->input('duration');
				$expirationDate = $this->createNewExpirationDateOfNovel($durationOfNovel);
				$payload =
				[
					'author_id' => $request->input('author_id'),
					'user_id' => $this->getCurrentUser()->id,
					'isbn' => $request->input('isbn'),
					'duration' => $durationOfNovel, //in hours
					'expiration' => $expirationDate,
					'title' => $request->input('title'),
					'content' => $request->has('content') ? $request->input('content') : null
				];
				$newNovel = AuthorNovels::create($payload);
				$this->getJsonResponse()->setData($newNovel);
			}
			else
			{
				$this->setDefaultFailureJsonResponse();
				$this->getJsonResponse()->setOptionnalFields(['title' => 'The novel for the author already exist.']);
			}
		}
		else
		{
			$this->setDefaultFailureJsonResponse();
		}

		return $this->getRawJsonResponse();
	}
    //
    //
    public function deleteNovel(Request $request)
    {
    	if ($this->_ARV->validate($request, ['id' => 'string|required']))
    	{
    		$novelAuthor = AuthorNovels::where(['id' => $request->input('id')])->first();
    		if (is_null($novelAuthor))
    		{
    			$this->setDefaultFailureJsonResponse();
    			$this->getJsonResponse()->setOptionnalFields(['title' => 'The novel doesnt exist.']);
    		}
    		else
    		{
    			$novelAuthor->delete();
    			$this->getJsonResponse()->setData($novelAuthor);
    		}
    	}
    	else
    	{
    		$this->setDefaultFailureJsonResponse();
    	}

    	return $this->getRawJsonResponse();
    }

    /**
     * Fetch all novels from authors
     * In the case where the authors ID is not specified
     * we fetch all of them
     **/
    public function index(Request $request)
    {
    	$authorId = $request->has('author_id') ? $request->input('author_id') : null;

    	if (!is_null($authorId))
    	{
    		$authorNovels = AuthorNovels::where(['author_id' => $authorId])->get();
    		$this->getJsonResponse()->setData($authorNovels);
    	}
    	else
    	{
    		$this->getJsonResponse()->setData(AuthorNovels::all());
    	}

    	return $this->getRawJsonResponse();
    }

    /**
     * This method is called by the user manually to check
     * if there are any news novels belongs to the authors
     * everytime where a user will be notified about a new novel
     * came out, it will be flagged in the database to avoid future
     * useless notifications.
     * Do not froget that a notification (new novels) is also limited by
     * his expiration date, so once the user is notified he will be
     * not notified again in the future.
     **/
    public function checkNewNovels(Request $request)
    {
    	$followedAuthorsCollection = AuthorSubscription::getAllFollowedAuthorsByUser($this->getCurrentUser()->id);

    	$filterNovelsBelongsAuthors = ['authors_id' => array()];
    	foreach ($followedAuthorsCollection as $followedAuthorCollection)
    	{
    		array_push($filterNovelsBelongsAuthors['authors_id'], $followedAuthorCollection->author_id);
    		# code...
    	}
    	Log::debug("USER ID : " . $this->getCurrentUser()->id . ' follows authors ID : ');
    	Log::debug($filterNovelsBelongsAuthors['authors_id']);
    	//$novelsBelongsToAuthors = AuthorNovels::where
    	//
    	//
    	//
    	//
    	//
    	// Get non expired novels of authors
    	$authorsNovels = AuthorNovels::getAllNovelIdsFromAuthors(
    		$filterNovelsBelongsAuthors['authors_id']);

    	Log::debug("authors Novels not expired and belongs to followed authors");
    	Log::debug($authorsNovels);

    	// Get all pushed notification under array format
    	$authorsAlreadyReadNovelsNotificationsIds = 
    		AuthorNovelsNotification::getAllPushedNovelsNotifications($this->getCurrentUser()->id);
    	Log::debug("Already pushed novels ids : ");
    	Log::debug($authorsAlreadyReadNovelsNotificationsIds);
    	$notNotifiedNovels = AuthorNovels::whereNotIn('author_novels.id', $authorsAlreadyReadNovelsNotificationsIds)
    	->whereIn('author_novels.id', $authorsNovels)
    	->get();

    	Log::debug("SQL of not notified novels : ");
    	Log::debug(DB::getQueryLog());
    	//Log::debug
    	Log::debug("Novels not pushed : ");
    	Log::debug($notNotifiedNovels);

    	/**
    	 * Now pushes all fetched novels notification as read
    	 * into database
    	 **/

    	$bulkPushedNotifications = array(); // for bulk creation in database
    	$notNotifiedNovels->each(function($item, $key) use (&$bulkPushedNotifications)
    	{
    		Log::debug("LAMBDA EACH FUNCTION ITEM :");
    		Log::debug($item);
    		array_push($bulkPushedNotifications,
    			[
    				'user_id' => $this->getCurrentUser()->id,
    				'novel_id' => $item['id'],
    				'updated_at' => date('Y-m-d H:i:s'),
    				'created_at' => date('Y-m-d H:i:s')
    			]
    		);
    		Log::debug($bulkPushedNotifications);
    	});
    	Log::debug("BULK PUSHED NOTIFICATIONS");
    	Log::debug($bulkPushedNotifications);
    	AuthorNovelsNotification::insert($bulkPushedNotifications);


    	/**
    	 * TEST ONLY WITH LEFT JOIN 
    	 * NOT WORKING ATM
    	 **/
    	//Log::debug("Novels of authors = ");
    	//Log::debug($authorsNovels);

    	//$todayDateTime = new DateTime('now');
    	///Log::debug("Today date time format : " . $todayDateTime->format('Y:m:d H:i:s'));
		
		//$finalNovels = AuthorNovels::leftJoin('author_subscriptions',
    						//'author_subscriptions.author_id', '=', 'author_novels.author_id')
    	//->leftJoin('author_novels_notifications', 'author_novels_notifications.novel_id', '=',
    						//'author_novels.author_id')
    	//->where('expiration', '>', Carbon::now())
    	//->get();

    	//Log::debug("Big tentative of left join : ");
    	//Log::debug($finalNovels);

    	$this->getJsonResponse()->setData($notNotifiedNovels);
    	return $this->getRawJsonResponse();
    }
}
