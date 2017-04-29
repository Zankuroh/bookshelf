<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class WishBook extends Model
{
	/** @var Array mass asignment fillable fields */
	protected $fillable = ['user_id', 'isbn'];
    //
}
