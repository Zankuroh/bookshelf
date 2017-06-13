<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Review extends Model
{
	/*
	Fillable fields for mass assignment
	 */
	protected $fillable =
	[
		'user_id',
		'isbn',
		'content',
		'rate'
	];
    //
}
