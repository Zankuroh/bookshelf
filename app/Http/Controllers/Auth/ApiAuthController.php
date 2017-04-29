<?php
namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use JWTAuth;
use Illuminate\Foundation\Auth\AuthenticateUsers;

use \Illuminate\Support\Facades\Log as Log;

class ApiAuthController extends \App\Http\Controllers\ApiController
{
    public function __construct()
    {
        $this->middleware('guest', ['except' => 'getLogout']);
        parent::__construct();
    }

    public function authenticate(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        $authenticated = true;
        $failureAuthenticationReasons = null;

        if ($this->_ARV->validate($request, 
            ['email' => 'required|email',
            'password' => 'required|string']
            ))
        {
            $credentials = $request->only('email', 'password');
            try
            {
                $token = JWTAuth::attempt($credentials);
                if (!$token)
                {
                    $authenticated = false;
                    $failureAuthenticationReasons = ['title' => 'Bad credentials.'];
                }
            }
            catch (JWTException $ex)
            {
                $authenticated = false;
                $failureAuthenticationReasons = ['title' => 'Something went wrong.'];
            }

            if ($authenticated)
            {
                $response->setData(['token' => $token]);
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }
        $response->setOptionnalFields($failureAuthenticationReasons);

        return $response->getJson();
    }

}