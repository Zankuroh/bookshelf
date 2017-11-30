<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Models\book;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Exceptions\TokenBlacklistedException;
use Tymon\JWTAuth\Exceptions\JWTExceptions;

class BookController extends \App\Http\Controllers\ApiController
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        // if ($this->useSecondDB)
        // {
        //     $books = DB::connection('mysql2')->select('select * from books where active = 1');
        // }
        // else
        // {
        $books = $this->getCurrentUser()->books()->get();
        // }
        // 
        $this->getJsonResponse()->setData($books);

        return ($this->getRawJsonResponse());
    }

    /**
     * Store a newly created resource in storage.
     *
     * Expected fields:
     * - 'isbn'
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        if ($this->_ARV->validate($request, 
            ['isbn' => 'required|string|between:5,20']))
        {
            if (\App\Models\Book::where('isbn', $request->input('isbn'))
                ->where('user_id', $this->getCurrentUser()->id)
                ->exists())
            {
                \Illuminate\Support\Facades\Log::alert('THE BOOK EXISTS');
                $response = $this->_ARV->getFailureJson(false);
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Book already exist.']);
            }
            else
            {
                \Illuminate\Support\Facades\Log::alert('THE BOOK NOT EXISTS');
                $newBook = \App\Models\Book::create(['user_id' => $this->getCurrentUser()->id,
                    'isbn' => $request->input('isbn')]);
                $newBook->save();
                $this->getJsonResponse()->setData($newBook);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }

    /**
     * Remove the specified resource from storage.
     * Expected fields:
     * - 'isbn'
     * - 'deleted' => 'yes'
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy(Request $request)
    {
        if ($this->_ARV->validate($request, 
            ['isbn' => 'required|string|between:5,20',
            'deleted' => 'required|string|accepted']))
        {
            if (\App\Models\Book::where('isbn', $request->input('isbn'))
                ->where('user_id', $this->getCurrentUser()->id)
                ->exists())
            {
                \Illuminate\Support\Facades\Log::alert('THE BOOK EXISTS');
                $deleteBook = \App\Models\Book::where(['user_id' => $this->getCurrentUser()->id,
                    'isbn' => $request->input('isbn')])->first();
                \Illuminate\Support\Facades\Log::alert($deleteBook);
                $deleteBook->delete();
                $this->getJsonResponse()->setData($deleteBook);
            }
            else
            {
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Book not exist.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $response->getJson();
    }
}
