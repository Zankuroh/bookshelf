<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Models\User;
use Tymon\JWTAuth\Facades\JWTAuth as JWTAuth;
use Log;

class ProfileController extends \App\Http\Controllers\ApiController
{
    /**
     * Display a listing of the resource.
     * @todo Add to returned response others kind of data
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $currentUser = JWTAuth::toUser(JWTAuth::getToken());
        if (is_null($currentUser))
        {
            $this->setDefaultFailureJsonResponse();
            $this->getJsonResponse()->setOptionnalFields(['title' => 'Something went wrong, contact an admin please.']);
        }
        else
        {
            $currentUserInformation = [];

            /** @var array current user's books */
            $currentUserInformation['books'] = $currentUser->books()->get()->all();

            /** @var array user's profile data */
            $currentUserInformation['profile'] = $currentUser->toArray();

            $this->getJsonResponse()->setData($currentUserInformation);
        }

        return $this->getRawJsonResponse();
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
        if ($this->_ARV->validate($request,
            ['name' => 'required|string|between:3,30',
            'password' => 'required|string|between:3,30']))
        {
            $currentUser = JWTAuth::toUser(JWTAuth::getToken());
            \Illuminate\Support\Facades\Log::alert("CURRENT USER PASSWORD" . $currentUser->password . ' and name ' . $currentUser->name);
            \Illuminate\Support\Facades\Log::alert("REQUEST PASSWORD HASH : " . \Illuminate\Support\Facades\Hash::make($request->input('password')));

            if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
            {
                $currentUser->name = $request->input('name');
                $currentUser->save();
                $this->getJsonResponse()->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Bad password.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
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
                $this->getJsonResponse()->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Bad credentials.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
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
                $this->getJsonResponse()->setData($this->getUserProfileData($currentUser));
            }
            else
            {
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields(['title' => 'Bad credentials.']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
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
        if ($this->_ARV->validate($request,
            ['delete' => 'required|accepted']
            ))
        {
            $currentUser =  $this->getCurrentUser();
            if (!$currentUser->social_auth)
            {
                if (\Illuminate\Support\Facades\Hash::check($request->input('password'), $currentUser->password))
                {
                    $currentUser->password = \Illuminate\Support\Facades\Hash::make($request->input('password'));
                    $currentUser->delete();
                    $response->setData(['deleted' => 'true']);
                }
                else
                {
                    $this->setDefaultFailureJsonResponse();
                    $this->getJsonResponse()->setOptionnalFields(['title' => 'Bad credentials.']);
                }
            }
            else
            {
                $currentUser->delete();
                $this->getJsonResponse()->setData(['deleted' => 'true']);
            }
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }

    /** 
     * Search a profile with nickname or email  
     * 
     */
    public function search(Request $request)
    {
        if ($this->_ARV->validate($request,
            ['keywords_search' => 'required|string']))
        {
            $searchingTableField = "name";
            $keywords_search = $request->input('keywords_search');
            if (filter_var($keywords_search, FILTER_VALIDATE_EMAIL))
            {
                $searchingTableField = "email";
                Log::debug("It's an email");
            }
            else
            {
                Log::debug("It's a name");
            }
            $matchedUsers = User::where('users.' . $searchingTableField,
                        'like',
                        '%' . $keywords_search . '%')->get();
            $this->getJsonResponse()->setData($matchedUsers);
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
    }
}
