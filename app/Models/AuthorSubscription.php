<?php

namespace App\Models;
use Illuminate\Database\Eloquent\Model;

use Log;
class AuthorSubscription extends Model
{
	protected $fillable =
	[
		'user_id',
		'author_id'
	];

	public static function hasAlreadySubscribed($userId, $authorId)
	{
		$subscribed = false;
		$whereCond = [
			['user_id', '=', $userId],
			['author_id', '=', $authorId]
		];

		Log::debug("Author ID = " . $authorId . " & user ID = " . $userId);
		Log::debug("Author subscription result first : ");
		Log::debug(AuthorSubscription::where($whereCond)->first());
		if (!is_null(AuthorSubscription::where($whereCond)->first()))
		{
			Log::debug("AuthorSubscription already exist for the user : " . $userId . " and for the author : " . $authorId);
			$subscribed = true;
		}

		return $subscribed;
	}

	/**
	 * Get all followed authors for the given user ID
	 * @return Mixed
	 **/
	public static function getAllFollowedAuthorsByUser($userId)
	{
		$filters = ['user_id' => $userId];
		$authors = AuthorSubscription::where($filters)->get();

		Log::debug("getAllFollowedAuthorsByUser : length=" . $authors->count());
		return $authors;
	}
}