<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use \App\Models\AuthorSubscription;

use Validator;

/**
 * Subscription of authors for users
 * Each user follows many authors
 * This class handles only subscribe and unsubscribe
 *
 * @author Sergen TANGUC
 **/ 
class AuthorSubscriptionController extends \App\Http\Controllers\ApiController
{
	public function index(Request $request)
	{
		$this->getJsonResponse()->setData(
			AuthorSubscription::where(["user_id" => $this->getCurrentUser()->id])->get());

		return $this->getRawJsonResponse();
	}


	public function subscribe(Request $request)
	{
		Log::debug("REQUEST : " );
		Log::debug($request);
		if ($this->_ARV->validate($request,
			['author_id' => 'string|required']))
		{
			$authorId = $request->input('author_id');
			if (AuthorSubscription::hasAlreadySubscribed($this->getCurrentUser()->id, $authorId))
			{
				$this->setDefaultFailureJsonResponse(false);
				$this->getJsonResponse()->setOptionnalFields(['title' => 'Already subscribed author.']);
			}
			else
			{
				AuthorSubscription::create(
					[
						'user_id' => $this->getCurrentUser()->id,
						'author_id' => $authorId
					]);
				$this->getJsonResponse()->setData(['validation' => 'true']);
			}
		}
		else
		{
			$this->setDefaultFailureJsonResponse();
		}

		return $this->getRawJsonResponse();
	}

	public function unSubscribe(Request $request)
	{
		if ($this->_ARV->validate($request,
			['author_id' => 'integer|required']))
		{
			$authorId = $request->input('author_id');
			if (!AuthorSubscription::hasAlreadySubscribed($this->getCurrentUser()->id, $authorId))
			{
				$this->setDefaultFailureJsonResponse(false);
				$this->getJsonResponse()->setOptionnalFields(['title' => 'Already unsubscribed author.']);
			}
			else
			{
				$whereCond =
				[
					'user_id' => $this->getCurrentUser()->id,
					'author_id'=> $authorId
				];

				AuthorSubscription::where($whereCond)->first()->delete();
				$this->getJsonResponse()->setData(['validation' => 'true']);
			}
		}
		else
		{
			$this->setDefaultFailureJsonResponse();
		}

		return $this->getRawJsonResponse();
	}
}
