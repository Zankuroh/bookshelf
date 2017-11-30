<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use DateTime;

/**
 * Class AuthorNovels
 * Handle all new novels published by authors
 * All novels has an expiration date and a duration (related)
 * In the case where it's expired, the user will be not notified
 * but will only be seen by admins.
 * @author Sergen TANGUC
 **/



class AuthorNovels extends Model
{
	protected $fillable =
	[
		'author_id',
		'user_id',
		'isbn',
		'duration',
		'expiration',
		'title',
		'content'
	];

	/**
	 * Fetch all novels belongs to author(s)
	 * @return Mixed collection
	 **/
	public static function getAllNovelIdsFromAuthors($authors, $includeExpired = false)
	{
		return self::whereIn('author_id', $authors)
		->where('expiration', '>', new DateTime('now'))
		->pluck('id')
		->all();

	}
    //
}
