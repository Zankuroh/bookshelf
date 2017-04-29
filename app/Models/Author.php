<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Author extends Model
{
    /** @var Array Fillable fields by mass assignments */
    protected $fillable =
        [
            'last_name',
            'first_name',
            'added_by'
        ];

    /** @var Array Hidden fields when serializing occurs */
    protected $hidden =
        [
            'current_votes',
            'created_at',
            'updated_at',
            'added_by',
            'active'
        ];
}