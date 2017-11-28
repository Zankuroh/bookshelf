<?php
namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use JWTAuth;
use Illuminate\Foundation\Auth\AuthenticateUsers;
use Socialite;
use Log;
use \App\Models\User as User;


class ApiAuthController extends \App\Http\Controllers\ApiController
{
  private $request;

  public function __construct()
  {
    $this->middleware('guest', ['except' => 'getLogout']);
    parent::__construct();
    $this->response = $this->getDefaultJsonResponse();
  }

  /**
  * Handle the profile loaded from the socialte driver and
  * create a JSON response accordingly and return it.
  * If the profile does not exist, we fillfull the response
  * with errors message otherwise the a new token of JWAuth is returned
  * into the response.
  *
  * @param $profile The fetched profile from driver (created inside the driver)
  * 
  * @return User or null
  */
  private function loadUserFromDriver($profile)
  {
    $success = true;

    if (!is_null($profile))
    {
      //$user = User::where('email', '=', $profile->getEmail())->first();

      $user = User::getByEmailOrCreate($email);
      if (is_null($user))
      {
        $user->email = $email;
        $user->name = $name;
        $user->password = null; //we force to laravel db

        $user->setSocialNetworkFlag();
        $user->save();

        // NOT EXISTING WE CREATE USER
        Log::debug("NEW USER HAS BEEN CREATED FOR EMAIL : " . $user->email);
      }
      else
      {
        Log::debug("NEW USER HAS BEEN RETRIEVED : " . $user->email);
      }

      /**
      * Feed request manually to handle it in classical authenticate
      * function to authenticate the user as a normal person.
      */
      $this->request['email'] = $user->email;
      $this->request['password'] = null;

      Log::debug(print_r($profile));
      Log::debug($profile->getEmail());
    }
    else
    {
      $success = false;
      $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
    }

    return $success;
  }

  /**
  * This method will create an object with useful information about the
  * user (maybe new?) that trying to connect with his/her social account.
  * To use this method, the request fields should be validated otherwise
  * unknown errors will occurs
  * @return object or null with meta data obtained from social network --
  * in the case where false is returned we will build the json response
  * with errors message
  */
  public function loadDataFromSocialProvider()
  {
    $providerName = $request->input('provider');
    $token = $request->input('token');
    $driver = Socialite::driver($providerName);
    $profile = null;

    if (strtolower($providerName) == "google")
    {
      /**
      * Handle google's redirect uri mandatory parameter
      * otherwise the auth will not pass
      */
      if ($request->input("redirect_uri"))
      {
        Log::debug("set new redirect uri " . $request->input('redirect_uri'));
        $driver->redirectUrl($request->input('redirect_uri'));
      }

      $accessData = $driver->getAccessTokenResponse($token);
      $accessToken = $accessData['access_token'];

      $profile = $driver->userFromToken($accessToken);
    }
    else if (strtolower($providerName) == "facebook")
    {
      $this->profile = $driver->userFromToken($token);
    }
    else
    {
      $response = $this->getDefaultFailureJsonResponse();
      $response->setData(['errors' => 'provider not exist']);
      $response->setOptionnalFields(['title' => 'The provider : ' . $providerName . ' is not supported, please try to connect with another social network.']);
    }

    return $profile;
  }

  /**
  * Initial oauth function to handle social login
  *
  * @return JsonObject Json Response
  */
  public function oAuthenticate(Request $request)
  {
    $response = $this->getDefaultJsonResponse();
    $this->setRequest($request);

    Log::debug("SOCIALITE PHASE");
    if ($this->_ARV->validate($request,
    ['token' => 'required|string',
    'provider' => 'required|string']))
    {
      try
      {
        Log::debug(" RECEVEID : token=" . $token . " providername =" . $providerName);

        //load profile from fetched social provider data
        $profile = $this->loadDataFromSocialProvider();
        //check if the profile is completely filled and also created or already
        // a loaded from the existing Database
        if (!is_null($profile) && $this->loadUserFromDriver($profile))
        {
          $this->authenticate($request, true);
        }
      }
      catch (Exception $e)
      {
        $response = $this->getDefaultFailureJsonResponse();
        $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
      }
      catch (\Guzzle\Http\Exception\BadResponseException $e)
      {
        $response = $this->getDefaultFailureJsonResponse();
        $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
        Log::debug('Uh oh! ' . $e->getMessage());
      }
      catch (\GuzzleHttp\Exception\ClientException $e)
      {
        $response = $this->getDefaultFailureJsonResponse();
        $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
        Log::debug('Uh oh! ' . $e->getResponse()->getBody(true)->getContents());
        Log::debug('Uh oh! ' . $e->getResponse()->getStatusCode());
      }

    }
    Log::debug("fuck1");

    return $this->getRawJsonResponse();
  }

  /**
  * Main function to handle authentication
  * Multiples fields belongs to the request are required
  * @see Validation fields
  */
  public function authenticate(Request $request, $socialAuth = false)
  {
    $response = $this->getDefaultJsonResponse();
    $authenticated = true;
    $failureAuthenticationReasons = null;

    if ($this->_ARV->validate($request,
    ['email' => 'required|email',
    'password' => 'required|string']
    ))
    {
      $token = null;
      $credentials = $request->only('email', 'password');

      try
      {

        if ($socialAuth)
        {
          $user = User::getByEmail($email);
          if (!is_null($user))
          {
            $token = JWTAuth::fromUser($user);
          }
          else
          {
            $authenticated = false;
          }
          Log::debug("JWT AUTH SOCIAL AUTH : TOKEN : " . $token);
        }
        else
        {
          $token = JWTAuth::attempt($credentials);
        }
        if (is_null($token) || $token == false)
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
        $user = JWTAuth::toUser($token);
        $this->response->setData(['token' => $token, 'user_id' => $user->id]);
      }
      else
      {
        $this->response = $this->_ARV->getFailureJson();
      }
    }
    else
    {
      $this->response = $this->_ARV->getFailureJson();
    }
    $this->response->setOptionnalFields($failureAuthenticationReasons);

    return $this->getRawJsonResponse();
  }

  public function setRequest(Request $request)
  {
    $this->request = $request;
  }

}
