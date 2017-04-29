<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use Tymon\JWTAuth\Facades\JWTAuth as JWTAuth;

class ProfileController extends \App\Http\Controllers\ApiController
{
    /**
     * Display a listing of the resource.
     * @todo Add to returned response others kind of data
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        $currentUser = JWTAuth::toUser(JWTAuth::getToken());

        if (is_null($currentUser))
        {
            $response = $this->_ARV->getFailureJson();
            $response->setOptionnalFields(['title' => 'Something went wrong, contact an admin please.']);
        }
        else
        {
            $currentUserInformation = [];

            /** @var array current user's books */
            $currentUserInformation['books'] = $currentUser->books()->get()->all();

            /** @var array user's profile data */
            $currentUserInformation['profile'] = $currentUser->toArray();

            $response->setData($currentUserInformation);
        }

        return $response->getJson();
    }

    /** Built an array of profile data by given user
    * @param $user User which array will be build
    * @return Array ['profile' => DATA]
    */
    private function getUserProfileData($user)
    {
        $profileData['profile'] = $user->toArray();

        return $profileData;
    }

    /**
     *   ChangerUserName function
     *   It able to change the current user's email
     * In order to change, we need password field
     * 
     * @param Request $request
     */
    public function changeUserName(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        if ($this->_ARV->validate($request,
                ['name' => 'required|string|between:5,30',
                'password' => 'required|string|between:5,30']))
        {
            $currentUser = JWTAuth::toUser(JWTAuth::getToken());
            \Illuminate\Support\Facades\Log::alert("CURRENT USER PASSWORD" . $currentUser->password . ' and name ' . $currentUser->name);
            \Illuminate\Support\Facades\Log::alert("REQUEST PASSWORD HASH : " . \Illuminate\Support\Facades\Hash::make($request->input('password')));

            if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
            {
                $currentUser->name = $request->input('name');
                $currentUser->save();
                $response->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
                $response->setOptionnalFIelds(['title' => 'Bad password.']);
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }

        return $response->getJson();
    }

    /**
     * changeEmail function
     * Able to change user's email
     * Require in the request (fields):
     *     - 'password' Actual password of the user
     * @param Request $request
     */
    public function changeEmail(Request $request)
    {
        $response = $this->getDefaultJsonResponse();

        if ($this->_ARV->validate($request, 
            ['password' => 'required|string|between:5,30',
            'email' => 'required|email|between:5,30|unique:users,email']
            ))
        {
            $currentUser = JWTAuth::toUser(JWTAuth::getToken());
            if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
            {
                $currentUser->email = $request->input('email');
                $currentUser->save();
                $response->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
                $respone->setOptionnalFields(['title' => 'Bad credentials.']);
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }

        return $response->getJson();
    }

    /**
     * ChangePassword function
     * Change current user's password
     * Require in the request (fields):
     *     - 'password' => Password
     *     - 'new_password' => New password
     * @param Request $request
     */
    public function changePassword(Request $request)
    {
        $response = $this->getDefaultJsonResponse();

        if ($this->_ARV->validate($request, 
            ['password' => 'required|string|between:5,30',
            'new_password' => 'required|string|between:5,30']
            ))
        {
            $currentUser = JWTAuth::toUser(JWTAuth::getToken());
            if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
            {
                $currentUser->password = \Illuminate\Support\Facades\Hash::make($request->input('new_password'));
                $currentUser->save();
                $response->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
                $response->setOptionnalFields(['title' => 'Bad credentials.']);
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }

        return $response->getJson();
    }

    /**
     * deleteProfile
     * Delete the user from the app
     * 
     * Mandatory fields :
     *     - 'delete' => yes|1|true
     *     - 'password' => string
     * @param Request $request
     */
    public function deleteProfile(Request $request)
    {
        $response = $this->getDefaultJsonResponse();

        if ($this->_ARV->validate($request, 
            ['password' => 'required|string|between:5,30',
            'delete' => 'required|accepted']
            ))
        {
            $currentUser = JWTAuth::toUser(JWTAuth::getToken());
            if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
            {
                $currentUser->password = \Illuminate\Support\Facades\Hash::make($request->input('new_password'));
                $currentUser->delete();
                $response->setData(['deleted' => 'true']);
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
                $response->setOptionnalFields(['title' => 'Bad credentials.']);
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }

        return $response->getJson();
    }
}
