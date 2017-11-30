<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AuthorNovelsNotification extends Model
{
	protected $fillable = 
	[
		'user_id',
		'novel_id'
	];

	/**
	 * Fetch all already pushed/read notifications
	 * store in database
	 * @return array of novels ids
	 **/
	public static function getAllPushedNovelsNotifications($userId)
	{
		return self::where('user_id', $userId)->pluck('novel_id')->all();
	}
    //
}
