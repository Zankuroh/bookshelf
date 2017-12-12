<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Book extends Model
{
	const READ_BOOK_STATE = 0;
	const UNREAD_BOOK_STATE = 1;
	const WISH_BOOK_STATE = 2;
	const BORROW_BOOK_STATE = 3;
	const FAVORITE_BOOK_STATE = 4;

	protected $fillable = ['user_id', 'isbn'];

	//protected $visible = ['user_id', 'status_id', 'category_id'];
    //
    //
    public function setStatus($status)
    {
    	$this->status_id = $status;
    }


}
