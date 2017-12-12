<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Book extends Model
{
	public const READ_BOOK_STATE = 0;
	public const UNREAD_BOOK_STATE = 1;
	public const WISH_BOOK_STATE = 2;
	public const BORROW_BOOK_STATE = 3;
	public const FAVORITE_BOOK_STATE = 4;

	protected $fillable = ['user_id', 'isbn'];

	//protected $visible = ['user_id', 'status_id', 'category_id'];
    //
    //
    public function setStatus($status)
    {
    	$this->status_id = $status;
    }

    
}
