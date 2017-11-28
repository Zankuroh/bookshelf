<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Exceptions\TokenBlacklistedException;
use Tymon\JWTAuth\Exceptions\JWTExceptions;

use \App\Models\Author as Author;
use Illuminate\Support\Facades\Auth;

use Validator;
use Illuminate\Support\Facades\Log;

/**
 * Authors controller, able to handle every callback belongs to authors management
 * This api will be in the near future mostly used by admins or qualified employees
 * 
 * @author Sergen TANGUC
 **/
class AuthorController extends \App\Http\Controllers\ApiController
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $this->_response->setData(\App\Models\Author::all());

        return $this->_response->getJson();
    }

    /**
     * Store into the database a new author
     * 
     * Expected fields :
     *     KEY => TYPE
     *     - first_name => String
     *     - last_name => String
     *     - active => boolean
     * 
     * Returning json array
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        if (!$this->_ARV->validate($request, [
                'first_name' => 'required|alpha_num|between:5,30|unique:authors,first_name',
                'last_name' => 'required|alpha_num|between:5,30|unique:authors,last_name'])
            )
        {
            $response = $this->_ARV->getFailureJson();
        }
        else
        {
            $newAuthor = Author::firstOrCreate([
                    'last_name' => $request->input('last_name'),
                    'first_name' => $request->input('first_name'),
                    'added_by' => JWTAuth::toUser(JWTAuth::getToken())->id
            ]);
            $response->setData($newAuthor);
        }

        return $response->getJson();
    }


    /**
     * Delete from database the selected author
     * The request must be filled with 'id' field corresponding to author
     * 
     * @param Request $request
     **/
    public function destroy(Request $request)
    {
        $response = $this->_response;
        if (!$this->_ARV->validate($request,
                    ['id' => 'required|numeric']
            ))
        {
            $response = $this->_ARV->getFailureJson();
        }
        else
        {
            $author = Author::find($request->input(('id')));
            if ($author != null)
            {
                $response->setData($author);
                $author->delete();
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
                $failureReasons = ['title' => 'Author not exist.'];
                $response->setOptionnalFields($failureReasons);
            }
        }

        return $response->getJson();
    }
}
