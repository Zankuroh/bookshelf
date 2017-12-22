<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Models\WishBook;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Exceptions\TokenBlacklistedException;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use Log;

/**
 * @todo Use a constant variable to access to the table of the controller
 * @todo eg. $tableNametarget instead of \App\Models\WishBook
 * Handle wish list of user, this wish list is specifically made foor books
 * 
 */
class WishBookController extends \App\Http\Controllers\ApiController
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $this->getJsonResponse()->setData($this->getCurrentUser()->wishBooks()->get());
        
        return $this->getRawJsonResponse();
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {

        //
    }

    /**
     * Store a newly created resource in storage.
     * Expect fields:
     * - 'isbn'
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        if ($this->_ARV->validate($request, 
            ['isbn' => 'required|string|between:5,20']))
        {
            if (\App\Models\WishBook::where('isbn', $request->input('isbn'))
                ->where('user_id', $this->getCurrentUser()->id)
                ->exists())
            {
                \Illuminate\Support\Facades\Log::alert('THE WISH BOOK EXISTS');
                $this->setDefaultFailureJsonResponse(false);
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Book already exist in wish list.']);
            }
            else
            {
                \Illuminate\Support\Facades\Log::alert('THE WISH BOOK NOT EXISTS');
                $newWishBook = \App\Models\WishBook::create(['user_id' => $this->getCurrentUser()->id,
                    'isbn' => $request->input('isbn')]);
                $newWishBook->save();
                $this->getJsonResponse()->setData($newWishBook);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $wishList = WishBook::where('user_id', '=', $id)->get();
        if ($wishList->isNotEmpty())
        {
            $this->getJsonResponse()->setData($wishList);
        }
        else
        {
            $this->getJsonResponse()->setOptionnalFields(['title' => 'User\'s wish list is empty.']);
        }

        return $this->getRawJsonResponse();
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        //
    }

    /**
     * Remove the specified resource from storage.
     * Expect fields:
     * - 'isbn'
     * - 'deleted' => yes
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy(Request $request)
    {
        if ($this->_ARV->validate($request, 
            ['isbn' => 'required|string|between:5,20',
            'deleted' => 'required|string|accepted']))
        {
            if (\App\Models\WishBook::where('isbn', $request->input('isbn'))
                ->where('user_id', $this->getCurrentUser()->id)
                ->exists())
            {
                \Illuminate\Support\Facades\Log::alert('THE BOOK EXISTS');
                $deleteWishBook = \App\Models\WishBook::where(['user_id' => $this->getCurrentUser()->id,
                    'isbn' => $request->input('isbn')])->first();
                $response->setData($deleteWishBook);
                \Illuminate\Support\Facades\Log::alert($deleteWishBook);
                $deleteWishBook->delete();
            }
            else
            {
                $this->setDefaultFailureJsonResponse(false);
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Book not exist.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }
}
