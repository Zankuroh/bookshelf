<?php
namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use JWTAuth;
use Illuminate\Foundation\Auth\AuthenticateUsers;
use Socialite;
use Log;


class ApiAuthController extends \App\Http\Controllers\ApiController
{
    public function __construct()
    {
        $this->middleware('guest', ['except' => 'getLogout']);
        parent::__construct();
    }

    public function authenticate(Request $request)
    {


        // Log::debug("SOCIALITE PHASE");
        // $user = Socialite::driver('google')->userFromToken("4/L--D7EKgNf6pSwPvqsGe7IvMLT7Bx01I75O4nk5ih7g");
        // Log::debug($user);

        // $actual_link = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
        // Log::debug("LOCAL URI : " . $actual_link);

        // $driver = Socialite::driver("google");
        // $driverFB = Socialite::driver("facebook");

        // $access_token = $driver->getAccessTokenResponse("4/9GMhPItm1zz8SjQ3u9v4bATOplxvYjvEoPhmgIGfccs");
        

        // $access_token_fb = $driverFB->getAccessTokenResponse("4/9GMhPItm1zz8SjQ3u9v4bATOplxvYjvEoPhmgIGfccs");


        // Log::debug("Foobar1");

        // $socialUser = $driver->userFromToken($access_token);


        // $socialUserFB = $driverFB->userFromToken($access_token_fb);

        // Log::debug($user);
            
        // Log::debug("second user !!!!!!");
        // Log::debug($socialUser);
        // Log::debug("-----get access token");

        


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
                    $failureAuthenticationReasons = ['title' => 'The email address or password you entered is not valid.'];
                }
            }
            catch (JWTException $ex)
            {
                $authenticated = false;
                $failureAuthenticationReasons = ['title' => 'The email address or password you entered is not valid.'];
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