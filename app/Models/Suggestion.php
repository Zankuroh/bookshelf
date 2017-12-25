<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Suggestion extends Model
{
	protected $fillable = ['isbn', 'user_id', 'reference_count'];
    //
}
