<?php

namespace App\Http\Controllers\Api;

use Validator;
use App\Models\User;
use Illuminate\Support\Facades\Hash;
use Illuminate\Http\Request;

class RegisterController extends \App\Http\Controllers\ApiController
{
    /**
     * Register a new user.
     *
     * @example
     *     Mandatory fields:
     *         - 'email' => email format && between 5 and 30 character
     *         - 'password' => string format && between 5 and 30 character
     *         - 'name' => string format && between 5 and 30 character
     *
     * @return \Illuminate\Http\Response
     */
    public function registerNewUser(Request $request)
    {
        if ($this->_ARV->validate($request, [
            'email' => 'required|email|between:5,30|unique:users,email',
            'password' => 'required|string|between:5,30',
            'name' => 'required|string|between:3,30']
            ))
        {
            $newUserEmail = $request->input('email');
            if (\App\Models\User::where('email', $newUserEmail)->get()->isEmpty())
            {
                $newUser = new \App\Models\User();
                $newUser->email = $newUserEmail;
                $newUser->name = $request->input('name');
                $newUser->name = $request->input('name');
                $newUser->password = Hash::make($request->input('password'));
                $newUser->save();
                $this->getJsonResponse()->setData($newUser);
            }
            else
            {
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Email already exist.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }

}
